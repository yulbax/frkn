package io.github.yulbax.frkn

import android.app.Application
import io.github.yulbax.frkn.di.appModule
import io.github.yulbax.frkn.util.Telemetry
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FrknApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Telemetry.install()
        startKoin {
            androidContext(this@FrknApplication)
            modules(appModule)
        }
    }
}