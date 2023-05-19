package com.codex.budgetexpense.modules

import com.codex.budgetexpense.dataClasses.UserDataClass
import com.codex.budgetexpense.utils.DataHolderClass
import com.codex.budgetexpense.utils.FireBaseRefrences
import com.codex.budgetexpense.utils.UserDataHolder
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val singleModule = module {

    single { FirebaseAuth.getInstance() }
    single { FireBaseRefrences(androidContext()) }
    single { UserDataHolder() }
    single { DataHolderClass() }
}