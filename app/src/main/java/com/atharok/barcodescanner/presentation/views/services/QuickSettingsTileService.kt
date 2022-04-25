package com.atharok.barcodescanner.presentation.views.services

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.atharok.barcodescanner.presentation.views.activities.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsTileService: TileService() {
    override fun onClick() {
        super.onClick()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivityAndCollapse(intent)
    }
}