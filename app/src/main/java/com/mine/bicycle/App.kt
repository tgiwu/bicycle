package com.mine.bicycle

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mine.bicycle.config.ConfigManager
import com.mine.bicycle.config.model.Config
import dagger.hilt.android.HiltAndroidApp
import java.util.function.Function
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ConfigManager.readConfig()
        }
    }


}