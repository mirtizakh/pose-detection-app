package com.task.posedetection.app

import android.app.Application
import com.task.posedetection.DialogManager
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class AppController : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: AppController
        private val kodein = Kodein.lazy {
            bind() from singleton { DialogManager() }
        }

        fun kodein() = kodein
    }
}