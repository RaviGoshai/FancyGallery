package com.vanravi.fancygallery

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FancyGalleryApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: FancyGalleryApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}