package de.mindmarket.echojournal.echos.presentation.preview

import de.mindmarket.echojournal.echos.presentation.echos.models.PlaybackState
import de.mindmarket.echojournal.echos.presentation.models.EchoUi
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data object PreviewModels {
    val echoUi = EchoUi(
        id = 0,
        title = "My own memo",
        mood = MoodUi.STRESSED,
        recordedAt = Instant.now(),
        note = (1..50).joinToString(" ") { "Hello" },
        topics = listOf("Love", "Work"),
        amplitudes = (1..30).map { Random.nextFloat() },
        playbackTotalDuration = 250.seconds,
        playbackCurrentDuration = 120.seconds,
        playbackState = PlaybackState.PAUSED
    )
}