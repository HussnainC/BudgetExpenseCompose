package com.codex.budgetexpense.sealdclasses

import com.codex.budgetexpense.R

enum class Screen(val route: String,val title: String, val icon: Int) {
    Exit("exit","Return", R.drawable.ic_exit),
    Expense("expense", "Expense", R.drawable.ic_expensies),
    Saving("saving","Saving" ,R.drawable.ic_savings),
    Budget("budget", "Budget",R.drawable.ic_budget)
}