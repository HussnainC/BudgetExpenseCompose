package com.codex.budgetexpense.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.codex.budgetexpense.R
import com.codex.budgetexpense.enums.ExpenseTypes
import com.codex.budgetexpense.enums.SavingTypes
import com.codex.budgetexpense.ui.theme.BudgetExpenseTheme

import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


fun <T : Any> Activity.startNewActivity(mClass: Class<T>, finish: Boolean) {
    startActivity(Intent(this, mClass))
    if (finish) {
        finish()
    }
}

fun <T : Any> Activity.startNewActivity(
    mClass: Class<T>,
    finish: Boolean,
    values: (Intent) -> Unit
) {
    startActivity(Intent(this, mClass).also {
        values(it)
    })
    if (finish) {
        finish()
    }
}


fun Context.getResourceColor(colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getMyColorStateList(colorId: Int): ColorStateList {
    return ColorStateList.valueOf(getResourceColor(colorId))
}

fun String.showLog(message: String?) {
    Log.d(this, message ?: "")
}


fun Context.getIcon(icon: String): Int {
    return resources.getIdentifier(icon, "drawable", packageName)
}

fun Activity.requestForPermission(permission: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

infix fun String.getNameWithOutExtension(prefix: String): String {
    val name = this.split(prefix)[0]
    return if (name != null) {
        name.trim()
    } else {
        this
    }
}

fun Long.formatTimeUnit(): String {
    return String.format(
        "%02d:%02d",
        java.lang.Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(this)),
        java.lang.Long.valueOf(
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    this
                )
            )
        )
    )
}

fun Context.updateNewFileAdd(file: File) {
    val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
    intent.data = Uri.fromFile(file)
    sendBroadcast(intent)
}

fun Long.videoDuration(): String {
    return ""
}

@Composable
fun Activity.setNewContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    statusBarColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    BudgetExpenseTheme(statusBarColor = statusBarColor) {
        Surface(
            modifier = modifier
        ) {
            content()
        }
    }
}

fun getImage(title: String): Int {
    return if (title == SavingTypes.Emergency.title) {
        R.drawable.ic_saving_emergency
    } else if (title == SavingTypes.WEDDING.title) {
        R.drawable.ic_saving_wedding
    } else if (title == SavingTypes.CAR.title) {
        R.drawable.ic_car
    } else if (title == SavingTypes.Education.title) {
        R.drawable.baseline_school_24
    } else if (title == ExpenseTypes.Entertainment.title) {
        R.drawable.ic_entertainment
    } else if (title == ExpenseTypes.Food.title) {
        R.drawable.ic_budget_food
    } else if (title == ExpenseTypes.Shopping.title) {
        R.drawable.ic_shopping
    } else if (title == ExpenseTypes.Rent.title) {
        R.drawable.ic_rent
    } else if (title == ExpenseTypes.CarLoan.title) {
        R.drawable.ic_car
    } else {
        R.drawable.ic_saving_emergency
    }
}

fun Long.dateTimeFormat(pattern: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(pattern).format(this)
}









