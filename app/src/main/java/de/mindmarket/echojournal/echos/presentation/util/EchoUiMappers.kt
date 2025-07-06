package de.mindmarket.echojournal.echos.presentation.util

import de.mindmarket.echojournal.echos.domain.echo.Echo
import de.mindmarket.echojournal.echos.presentation.echos.models.PlaybackState
import de.mindmarket.echojournal.echos.presentation.models.EchoUi
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import kotlin.time.Duration

fun Echo.toEchoUi(
    currentPlaybackDuration: Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.STOPPED

): EchoUi {
    return EchoUi(
        id = id!!,
        title = title,
        mood = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        amplitudes = audioAmplitudes,
        audioFilePath = audioFilePath,
        playbackTotalDuration = audioPlaybackLength,
        playbackCurrentDuration = currentPlaybackDuration,
        playbackState = playbackState
    )
}