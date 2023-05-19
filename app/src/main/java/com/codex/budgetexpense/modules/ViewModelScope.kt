package com.codex.budgetexpense.modules

import com.codex.budgetexpense.repositories.FireBaseRepo
import com.codex.budgetexpense.viewmodels.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelScope = module {
    viewModel { MainViewModel(FireBaseRepo(androidContext(), get(), get())) }
}