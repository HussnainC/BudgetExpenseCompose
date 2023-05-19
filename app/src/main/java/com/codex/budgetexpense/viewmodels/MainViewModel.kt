package com.codex.budgetexpense.viewmodels

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.budgetexpense.dataClasses.*
import com.codex.budgetexpense.interfaces.ResultCallBack
import com.codex.budgetexpense.repositories.FireBaseRepo
import com.codex.budgetexpense.sealdclasses.ResultStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.Calendar
import java.util.HashMap

class MainViewModel(val fireBaseRepo: FireBaseRepo) : ViewModel() {

    private val _monthsYears: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val monthsYears = _monthsYears.asStateFlow()

    private val _months: MutableStateFlow<List<MonthsDataClass>> = MutableStateFlow(listOf())
    val months = _months.asStateFlow()

    private val _savings: MutableStateFlow<ResultStates> = MutableStateFlow(ResultStates.Initial)
    val savings = _savings.asStateFlow()

    private val _budgets: MutableStateFlow<ResultStates> = MutableStateFlow(ResultStates.Initial)
    val budgets = _budgets.asStateFlow()

    private val _selectedDate: MutableStateFlow<String> =
        MutableStateFlow(SimpleDateFormat("MMM yyyy").format(Calendar.getInstance().timeInMillis))
    val selectedDate = _selectedDate.asStateFlow()

    fun loadUserInfo(resultCallBack: ResultCallBack<UserDataClass>) =
        viewModelScope.launch(Dispatchers.IO) {
            fireBaseRepo.loadUserData(resultCallBack)
        }

    fun startSignUp(userData: UserDataClass, resultCallBack: ResultCallBack<UserDataClass>) =
        viewModelScope.launch(Dispatchers.IO) {
            fireBaseRepo.signUpUser(userData, resultCallBack)
        }

    fun loadMonthAndYears() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDate = LocalDate.now()
            val next10Year = currentDate.year + 10
            val last4Years = currentDate.year - 4
            val monthYearList = mutableListOf<String>()
            for (year in last4Years..next10Year) {
                for (month in Month.values()) {
                    val monthYear =
                        "${month.name[0]}${month.name.substring(1, 3).lowercase()} $year"
                    monthYearList.add(monthYear)
                }
            }
            _monthsYears.value = monthYearList
        }
    }

    fun moveForwardMonth() {
        val indexOf = monthsYears.value.indexOf(_selectedDate.value)
        if (indexOf < monthsYears.value.lastIndex) {
            _selectedDate.value = monthsYears.value[indexOf + 1]
        }
    }

    fun moveBackwardMonth() {
        val indexOf = monthsYears.value.indexOf(_selectedDate.value)
        if (indexOf > 0) {
            _selectedDate.value = monthsYears.value[indexOf - 1]
        }
    }

    fun loadMonths() = viewModelScope.launch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monthList = mutableListOf<MonthsDataClass>()
            for (month in Month.values()) {
                val monthName = month.name.substring(0, 3)
                monthList.add(MonthsDataClass(monthName))
            }
            val currentMonth = SimpleDateFormat("MMM").format(Calendar.getInstance().timeInMillis)
            monthList[monthList.indexOf(MonthsDataClass(currentMonth.uppercase()))].isSelected =
                true
            _months.value = monthList
        }
    }



    fun getSelectedMonthIndex(): Int {
        return _months.value.indexOf(_months.value.find { it.isSelected })
    }

    fun createSavingGoal(data: SavingDataClass, resultCallBack: ResultCallBack<SavingDataClass>) =
        viewModelScope.launch(Dispatchers.IO) {
            fireBaseRepo.createSavingGoal(data, resultCallBack = resultCallBack)
        }


    fun loadSavings() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _savings.value = ResultStates.Loading
            fireBaseRepo.loadSavings(object : ResultCallBack<List<SavingDataClass>> {
                override fun onSuccess(result: List<SavingDataClass>) {
                    _savings.value = ResultStates.Result(result)
                }

                override fun onFail(message: Exception) {
                    _savings.value = ResultStates.Fail(message)

                }

            })
        } catch (ex: java.lang.Exception) {
            _savings.value = ResultStates.Fail(ex)
        }
    }
    fun loadBudgets() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _budgets.value = ResultStates.Loading
            fireBaseRepo.loadBudgets(object : ResultCallBack<List<ExpenseDataClass>> {
                override fun onSuccess(result: List<ExpenseDataClass>) {
                    _budgets.value = ResultStates.Result(result)
                }

                override fun onFail(message: Exception) {
                    _budgets.value = ResultStates.Fail(message)

                }

            })
        } catch (ex: java.lang.Exception) {
            _budgets.value = ResultStates.Fail(ex)
        }
    }

    fun addDepositIntoSavings(
        map: HashMap<String, SavingDataClass.Savings>,
        data: SavingDataClass,
        resultCallBack: ResultCallBack<HashMap<String, SavingDataClass.Savings>>
    ) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.addDepositIntoSavings(map, data, resultCallBack)
    }

    fun createNewBudget(data: ExpenseDataClass, resultCallBack: ResultCallBack<ExpenseDataClass>)=viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.createBudget(data,resultCallBack)
    }

    fun addExpenseIntoBudget(
        map: HashMap<String, ExpenseDataClass.Expense>,
        selectedData: ExpenseDataClass,
        resultCallBack: ResultCallBack<HashMap<String, ExpenseDataClass.Expense>>
    ) {
        fireBaseRepo.addExpenseIntoBudget(map, selectedData, resultCallBack)
    }

    fun updateBudget(amount: Int, pushId: String?)=viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.updateBudget(amount,pushId)
    }
}