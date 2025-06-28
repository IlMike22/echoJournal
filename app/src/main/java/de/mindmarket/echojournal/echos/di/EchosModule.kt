package de.mindmarket.echojournal.echos.di

import de.mindmarket.echojournal.echos.data.audio.AndroidAudioPlayer
import de.mindmarket.echojournal.echos.data.recording.AndroidVoiceRecorder
import de.mindmarket.echojournal.echos.data.recording.InternalRecordingStorage
import de.mindmarket.echojournal.echos.domain.audio.AudioPlayer
import de.mindmarket.echojournal.echos.domain.recording.RecordingStorage
import de.mindmarket.echojournal.echos.domain.recording.VoiceRecorder
import de.mindmarket.echojournal.echos.presentation.create_echo.CreateEchoViewModel
import de.mindmarket.echojournal.echos.presentation.echos.EchosViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier._q
import org.koin.dsl.bind
import org.koin.dsl.module

val echosModule = module {
    singleOf(::AndroidVoiceRecorder).bind(VoiceRecorder::class)
    singleOf(::InternalRecordingStorage).bind(RecordingStorage::class)
    singleOf(::AndroidAudioPlayer).bind(AudioPlayer::class)

    viewModelOf(::EchosViewModel)
    viewModelOf(::CreateEchoViewModel)
}