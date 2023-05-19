package com.codex.budgetexpense.utils

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FireBaseRefrences(val context: Context) {

    val reference = FirebaseDatabase.getInstance().getReference("users")
}