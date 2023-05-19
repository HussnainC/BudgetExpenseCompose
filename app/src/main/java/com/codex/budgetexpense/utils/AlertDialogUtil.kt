package com.codex.budgetexpense.utils

import android.app.AlertDialog
import android.content.Context

class AlertDialogUtil(val context: Context) {
    private var alertDialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null

    init {
        alertDialogBuilder = AlertDialog.Builder(context)
    }

    fun showAlertDialog(
        title: String,
        message: String,
        positiveButton: Pair<String, () -> Unit>,
        negativeButton: String
    ) {
        alertDialogBuilder?.apply {
            this.setTitle(title)
            this.setMessage(message)
            this.setCancelable(false)
            this.setPositiveButton(positiveButton.first) { dialog, which ->
                positiveButton.second()
            }
            this.setNegativeButton(negativeButton) { dialog, which ->
                dialog.dismiss()
            }
        }
        alertDialog = alertDialogBuilder?.create()
        alertDialog?.show()
    }

    fun dismissAlertDialog() {
        alertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}
