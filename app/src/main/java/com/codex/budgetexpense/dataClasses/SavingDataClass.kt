package com.codex.budgetexpense.dataClasses

data class SavingDataClass(
    var title: String? = null,

    var goal: Int = 0,
) {
    var pushId: String? = null
    var savings: HashMap<String, Savings> = HashMap()

    data class Savings(val time: Long=System.currentTimeMillis(), val amount: Int = 0, val savingPushId: String? = null):java.io.Serializable
}