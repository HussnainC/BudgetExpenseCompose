package com.codex.budgetexpense.dataClasses

data class ExpenseDataClass(
    var title: String = "",

    var budget: Int = 0
) : java.io.Serializable {
    var pushId: String? = null
    var budgets: HashMap<String, Expense> = HashMap()
    var time: Long = System.currentTimeMillis()

    data class Expense(
        val time: Long = System.currentTimeMillis(),
        val amount: Int = 0,
        val expensePushId: String? = null,
        var title: String = ""
    ) : java.io.Serializable
}