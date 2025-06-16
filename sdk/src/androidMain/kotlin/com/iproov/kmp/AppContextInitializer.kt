// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

internal class AppContextInitializer : Initializer<Context> {
    override fun create(context: Context): Context {
        AppContext.setUp(context.applicationContext)
        return AppContext.get()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

/**
 * Provides access to the Application context.
 */
internal object AppContext {
    private lateinit var application: Application

    fun setUp(context: Context) {
        application = context as Application
    }

    fun get(): Context {
        if (::application.isInitialized.not())
            throw Exception("Application context isn't initialized")
        return application
    }
}