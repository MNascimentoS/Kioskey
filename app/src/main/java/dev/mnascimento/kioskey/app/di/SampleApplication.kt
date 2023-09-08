package dev.mnascimento.kioskey.app.di

import android.app.Application
import dev.mnascimento.kioskey.Kioskey

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Kioskey.watch(this)
    }

}