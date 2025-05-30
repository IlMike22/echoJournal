package de.mindmarket.echojournal.echos.presentation.echos

sealed interface EchosEvent {
    data object RequestAudioPermission: EchosEvent
}