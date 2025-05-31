package de.mindmarket.echojournal.app.di

import de.mindmarket.echojournal.app.EchoJournalApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as EchoJournalApp).applicationScope
    }
}