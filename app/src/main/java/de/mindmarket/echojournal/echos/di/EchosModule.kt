package de.mindmarket.echojournal.echos.di

import de.mindmarket.echojournal.echos.data.audio.AndroidAudioPlayer
import de.mindmarket.echojournal.echos.data.echo.RoomEchoDataSource
import de.mindmarket.echojournal.echos.data.recording.AndroidVoiceRecorder
import de.mindmarket.echojournal.echos.data.recording.InternalRecordingStorage
import de.mindmarket.echojournal.echos.domain.audio.AudioPlayer
import de.mindmarket.echojournal.echos.domain.echo.EchoDataSource
import de.mindmarket.echojournal.echos.domain.recording.RecordingStorage
import de.mindmarket.echojournal.echos.domain.recording.VoiceRecorder
import de.mindmarket.echojournal.echos.presentation.create_echo.CreateEchoViewModel
import de.mindmarket.echojournal.echos.presentation.echos.EchosViewModel
import de.mindmarket.echojournal.echos.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echosModule = module {
    singleOf(::AndroidVoiceRecorder).bind(VoiceRecorder::class)
    singleOf(::InternalRecordingStorage).bind(RecordingStorage::class)
    singleOf(::AndroidAudioPlayer).bind(AudioPlayer::class)
    singleOf(::RoomEchoDataSource) bind EchoDataSource::class

    viewModelOf(::EchosViewModel)
    viewModelOf(::CreateEchoViewModel)
    viewModelOf(::SettingsViewModel)
}