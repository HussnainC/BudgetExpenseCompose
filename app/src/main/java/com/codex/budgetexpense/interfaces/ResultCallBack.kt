package com.codex.budgetexpense.interfaces

interface ResultCallBack<T> {
    fun onSuccess(result: T)
    fun onFail(message: java.lang.Exception)
}