package de.mindmarket.echojournal.echos.di

import de.mindmarket.echojournal.echos.data.recording.AndroidVoiceRecorder
import de.mindmarket.echojournal.echos.domain.recording.VoiceRecorder
import de.mindmarket.echojournal.echos.presentation.echos.EchosViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echosModule = module {
    single {
        AndroidVoiceRecorder(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind VoiceRecorder::class

    viewModelOf(::EchosViewModel)
}