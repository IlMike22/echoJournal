package de.mindmarket.echojournal.echos.domain.recording

import kotlinx.coroutines.flow.StateFlow

interface VoiceRecorder {
    val recordingDetails: StateFlow<RecordingDetails>

    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun cancel()
}