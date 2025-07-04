package de.mindmarket.echojournal.core.database.di

import androidx.room.Room
import de.mindmarket.echojournal.core.database.EchoDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<EchoDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            EchoDatabase::class.java,
            "echos.db",
        ).build()
    }
    single {
        get<EchoDatabase>().echoDao
    }
}