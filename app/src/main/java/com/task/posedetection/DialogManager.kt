package com.task.posedetection

import android.app.AlertDialog
import android.content.Context

class DialogManager {
    // region METHODS
    fun twoItemDialog(
        context: Context?,
        firstItem: String,
        secondItem: String,
        alertDialogListener: AlertDialogItemClickListener? = null
    ) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        val items = arrayOf(firstItem, secondItem)
        alertDialogBuilder.setItems(items) { dialogInterface, i ->
            alertDialogListener?.onItemClicked(i)
            dialogInterface?.dismiss()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun twoButtonDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = context.getString(R.string.ok),
        negativeButtonText: String = context.getString(R.string.cancel),
        alertDialogListener: AlertDialogListener? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder
            .setPositiveButton(positiveButtonText) { dialogInterface, _ ->
                alertDialogListener?.onPositiveButtonClicked()
                dialogInterface?.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialogInterface, _ ->
                alertDialogListener?.onNegativeButtonClicked()
                dialogInterface.dismiss()
            }

        builder.setTitle(title)
        builder.setMessage(message)
        val alertDialog = builder.create()
        alertDialog.setOnDismissListener { alertDialogListener?.onDialogDismissed() }
        alertDialog.show()
    }
    //end region METHODS

    // region INTERFACE
    interface AlertDialogListener {

        fun onPositiveButtonClicked() {}

        fun onNegativeButtonClicked() {}

        fun onDialogDismissed() {}
    }

    interface AlertDialogItemClickListener {
        fun onItemClicked(which: Int) {}
    }
    //end region INTERFACE
}