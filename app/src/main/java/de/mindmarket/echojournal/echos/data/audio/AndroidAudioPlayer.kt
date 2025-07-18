package de.mindmarket.echojournal.echos.data.audio

import android.media.MediaPlayer
import de.mindmarket.echojournal.echos.domain.audio.AudioPlayer
import de.mindmarket.echojournal.echos.domain.audio.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class AndroidAudioPlayer(
    private val applicationScope: CoroutineScope
) : AudioPlayer {
    private val _activeTrack = MutableStateFlow<AudioTrack?>(null)
    override val activeTrack = _activeTrack.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var durationJob: Job? = null

    override fun play(filePath: String, onComplete: () -> Unit) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            val fileInputStream = FileInputStream(File(filePath))
            try {
                setDataSource(fileInputStream.fd)

                prepare()
                start()

                _activeTrack.update {
                    AudioTrack(
                        totalDuration = this.duration.milliseconds,
                        durationPlayed = Duration.ZERO,
                        isPlaying = true
                    )
                }

                trackDuration()

                setOnCompletionListener {
                    onComplete()
                    stop()
                }

            } catch (exception: Exception) {
                Timber.e(exception, "error playing audio")
            } finally {
                fileInputStream.close()
            }
        }
    }

    override fun pause() {
        if (_activeTrack.value?.isPlaying == false) {
            return
        }

        _activeTrack.update {
            it?.copy(
                isPlaying = false
            )
        }
        durationJob?.cancel()
        mediaPlayer?.pause()
    }

    override fun resume() {
        if (_activeTrack.value?.isPlaying == true) {
            return
        }

        _activeTrack.update {
            it?.copy(
                isPlaying = true
            )
        }

        mediaPlayer?.start()
        trackDuration()

    }

    override fun stop() {
        _activeTrack.update {
            it?.copy(
                isPlaying = false,
                durationPlayed = Duration.ZERO
            )
        }

        durationJob?.cancel()
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }

        mediaPlayer = null
    }

    private fun trackDuration() {
        durationJob?.cancel()
        durationJob = applicationScope.launch {
            do {
                _activeTrack.update {
                    it?.copy(
                        durationPlayed = mediaPlayer?.currentPosition?.milliseconds ?: Duration.ZERO
                    )
                }
                delay(10L)
            } while (activeTrack.value?.isPlaying == true && mediaPlayer?.isPlaying == true)
        }
    }
}