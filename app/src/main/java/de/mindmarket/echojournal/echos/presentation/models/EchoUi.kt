package de.mindmarket.echojournal.echos.presentation.models

import de.mindmarket.echojournal.echos.presentation.echos.models.PlaybackState
import de.mindmarket.echojournal.echos.presentation.util.toReadableTime
import java.time.Instant
import kotlin.time.Duration

data class EchoUi(
    val id: Int,
    val title: String,
    val mood: MoodUi,
    val recordedAt: Instant,
    val note: String,
    val topics: List<String>,
    val amplitudes: List<Float>,
    val playbackCurrentDuration: Duration = Duration.ZERO,
    val playbackTotalDuration: Duration,
    val playbackState: PlaybackState = PlaybackState.STOPPED
) {
    val formattedRecordedAt = recordedAt.toReadableTime()
    val playbackRatio = (playbackCurrentDuration / playbackTotalDuration).toFloat()
}
