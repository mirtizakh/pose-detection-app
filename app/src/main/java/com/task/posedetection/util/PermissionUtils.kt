package com.task.posedetection.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.task.posedetection.app.AppController

object PermissionUtils {
    fun hasStoragePermission(): Boolean {
        return if (isAndroidRAndAbove()) {
            ContextCompat.checkSelfPermission(
                AppController.instance,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                AppController.instance,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     *  Checks if app has been granted CAMERA permission
     *  @return true , if granted
     * **/
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            AppController.instance,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAndroidRAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}