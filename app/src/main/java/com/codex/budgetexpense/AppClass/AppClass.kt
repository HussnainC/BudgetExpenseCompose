package com.codex.budgetexpense.AppClass

import android.app.Application
import com.codex.budgetexpense.modules.factoryModule
import com.codex.budgetexpense.modules.singleModule
import com.codex.budgetexpense.modules.viewModelScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppClass)
            androidLogger(level = Level.ERROR)
            modules(modules = listOf(viewModelScope, factoryModule, singleModule))
        }
    }

}