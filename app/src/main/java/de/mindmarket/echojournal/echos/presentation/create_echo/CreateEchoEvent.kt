package de.mindmarket.echojournal.echos.presentation.create_echo

sealed interface CreateEchoEvent {
    data object FailedToSaveFile: CreateEchoEvent
    data object EchoSuccessfullySaved: CreateEchoEvent
}