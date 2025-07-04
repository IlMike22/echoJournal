package de.mindmarket.echojournal.app

import android.app.Application
import android.os.Build
import de.mindmarket.echojournal.BuildConfig
import de.mindmarket.echojournal.app.di.appModule
import de.mindmarket.echojournal.core.database.di.databaseModule
import de.mindmarket.echojournal.echos.di.echosModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.Timer

class EchoJournalApp: Application() {
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@EchoJournalApp)
            modules(
                appModule,
                echosModule,
                databaseModule
            )
        }
    }
}