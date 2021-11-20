package com.task.posedetection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.task.posedetection.util.PermissionUtils

object Util {
    private enum class CHOOSER {
        CAMERA,
        GALLERY
    }

    fun showCameraAndGalleryOption(
        activity: MainActivity,
        dialogManager: DialogManager
    ) {
        dialogManager.twoItemDialog(
            activity,
            activity.getString(R.string.gallery_option),
            activity.getString(R.string.camera_option),
            object : DialogManager.AlertDialogItemClickListener {
                override fun onItemClicked(which: Int) {
                    when (which) {
                        0 -> checkPermission(activity, CHOOSER.GALLERY)
                        1 -> checkPermission(activity, CHOOSER.CAMERA)
                    }
                }
            })
    }


    private fun checkPermission(activity: MainActivity, item: CHOOSER) {
        when (item) {

            CHOOSER.CAMERA -> {
                if (PermissionUtils.hasCameraPermission()) {
                    openCamera(activity)
                } else {
                    checkListOfPermission(activity, listOf(Manifest.permission.CAMERA), item)
                }
            }
            CHOOSER.GALLERY -> {
                if (PermissionUtils.hasStoragePermission()) {
                    openAlbum(activity)
                } else {
                    if (PermissionUtils.isAndroidRAndAbove()) {
                        checkListOfPermission(
                            activity,
                            listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            item
                        )
                    } else {
                        checkListOfPermission(
                            activity,
                            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            item
                        )
                    }
                }
            }
        }
    }

    private fun openCamera(activity: MainActivity) {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        activity.cameraResultLauncher.launch(intent)
    }

    private fun openAlbum(activity: MainActivity) {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_GET_CONTENT
            val mimeTypes = arrayOf("image/*")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            type = "image/*"
        }
        activity.galleryResultLauncher.launch(intent)

    }

    private fun checkListOfPermission(
        activity: MainActivity,
        permissions: Collection<String>,
        item: CHOOSER,
    ) {
        Dexter.withContext(activity)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    p0?.let {
                        if (it.areAllPermissionsGranted()) {
                            if (item == CHOOSER.CAMERA) {
                                openCamera(activity)
                            } else if (item == CHOOSER.GALLERY) {
                                openAlbum(activity)
                            }
                        } else if (it.isAnyPermissionPermanentlyDenied) {
                            showPermissionSettingsDialog(activity)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    permissionToken?.continuePermissionRequest()
                }
            })
            .check()
    }


    private fun showPermissionSettingsDialog(
        activity: Activity,
        listener: DialogManager.AlertDialogListener? = null
    ) {
        DialogManager().twoButtonDialog(
            context = activity,
            title = activity.getString(R.string.permission),
            message = activity.getString(R.string.grant_permission_from_settings),
            positiveButtonText = activity.getString(R.string.open_settings),
            alertDialogListener = object : DialogManager.AlertDialogListener {
                override fun onPositiveButtonClicked() {
                    activity.let {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + it.packageName)
                        activity.startActivity(intent)
                        listener?.onPositiveButtonClicked()
                    }
                }

                override fun onNegativeButtonClicked() {
                    super.onNegativeButtonClicked()
                    listener?.onNegativeButtonClicked()
                }
            }
        )
    }


}