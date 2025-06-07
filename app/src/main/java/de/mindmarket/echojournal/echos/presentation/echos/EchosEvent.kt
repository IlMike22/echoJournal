package de.mindmarket.echojournal.echos.presentation.echos

import de.mindmarket.echojournal.echos.domain.recording.RecordingDetails

sealed interface EchosEvent {
    data object RequestAudioPermission: EchosEvent
    data object RecordingTooShort: EchosEvent
    data class OnDoneRecording(val details: RecordingDetails): EchosEvent
}