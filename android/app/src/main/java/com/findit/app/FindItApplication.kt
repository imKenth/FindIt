package com.findit.app

import android.app.Application
import com.google.firebase.FirebaseApp

class FindItApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }

    companion object {
        lateinit var instance: FindItApplication
            private set
    }
}
