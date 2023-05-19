package com.codex.budgetexpense.sealdclasses

sealed class ResultStates {
    object Loading : ResultStates()
    object Initial : ResultStates()
    class Fail(val exception: java.lang.Exception)  : ResultStates()
    class Result<T>(val result: T) : ResultStates()
}