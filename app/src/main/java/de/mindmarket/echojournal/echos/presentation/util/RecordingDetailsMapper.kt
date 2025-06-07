package de.mindmarket.echojournal.echos.presentation.util

import de.mindmarket.echojournal.app.navigation.NavigationRoute
import de.mindmarket.echojournal.echos.domain.recording.RecordingDetails
import kotlin.time.Duration.Companion.milliseconds

fun RecordingDetails.toCreateEchoRoute(): NavigationRoute.CreateEcho {
    return NavigationRoute.CreateEcho(
        recordingPath = this.filePath
            ?: throw IllegalArgumentException("Recording path cant be null!"),
        duration = this.duration.inWholeMilliseconds,
        amplitudes = this.amplitudes.joinToString(";")
    )
}

fun NavigationRoute.CreateEcho.toRecordingDetails(): RecordingDetails {
    return RecordingDetails(
        duration = this.duration.milliseconds,
        amplitudes = this.amplitudes.split(";").map { it.toFloat() },
        filePath = recordingPath
    )
}