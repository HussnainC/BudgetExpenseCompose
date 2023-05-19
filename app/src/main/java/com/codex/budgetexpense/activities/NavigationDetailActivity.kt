package com.codex.budgetexpense.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.ExpenseDataClass
import com.codex.budgetexpense.dataClasses.MonthsDataClass
import com.codex.budgetexpense.dataClasses.SavingDataClass
import com.codex.budgetexpense.enums.ExpenseTypes
import com.codex.budgetexpense.enums.SavingTypes
import com.codex.budgetexpense.interfaces.ResultCallBack
import com.codex.budgetexpense.sealdclasses.ResultStates
import com.codex.budgetexpense.sealdclasses.Screen
import com.codex.budgetexpense.ui.theme.*
import com.codex.budgetexpense.utils.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class NavigationDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mainViewModel.loadMonthAndYears()
        }
        setContent {
            setNewContent {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = Color(0xFFFFFFFF)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Screen.values().forEach {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clickable(enabled = true, onClick = {
                                if (it.route == Screen.Exit.route) {
                                    finish()
                                } else {
                                    navController.navigate(it.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        addVerticalSpace(space = 10.dp)
                        Image(
                            painter = painterResource(id = it.icon),
                            contentDescription = it.title,
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        )
                        Text(
                            text = it.title,
                            style = textStyle(textSize = 14f),
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            }
        }) {
            NavHost(
                navController = navController,
                startDestination = Screen.Expense.route,
                modifier = Modifier.padding(it)
            ) {
                composable(Screen.Expense.route) {
                    ExpenseScreen(Screen.Expense.title, navController, modifier = Modifier)
                }
                composable(Screen.Saving.route) {
                    SavingScreen(Screen.Saving.title, navController, modifier = Modifier)
                }
                composable(Screen.Budget.route) {
                    BudgetScreen(Screen.Budget.title, navController, modifier = Modifier)
                }
            }
        }
    }

    var createBudget: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalMaterial3Api::class)
    private
    @Composable
    fun BudgetScreen(title: String, navController: NavHostController, modifier: Modifier) {
        var selectedMonth by remember { mutableStateOf("") }
        window.statusBarColor = budgetBgColor.toArgb()
        mainViewModel.loadMonths()
        if (mainViewModel.budgets.value == ResultStates.Initial) {
            mainViewModel.loadBudgets()
        }
        val monthState = rememberLazyListState()
        var months by remember { mutableStateOf(listOf<MonthsDataClass>()) }
        var newBudgetsDialog by remember { mutableStateOf(false) }
        var changeBudgetDialog by remember { mutableStateOf(false) }
        var selectedData by remember { mutableStateOf(ExpenseDataClass()) }
        var totalBudget by remember { mutableStateOf("") }
        var isExpended by remember { mutableStateOf(false) }
        var expenseAmount by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(ExpenseTypes.Entertainment) }
        var budgets by remember { mutableStateOf(listOf<ExpenseDataClass>()) }
        var dataList by remember { mutableStateOf(listOf<ExpenseDataClass>()) }
        var addExpenseDialog by remember {
            mutableStateOf(false)
        }
        var dataLoading by remember {
            mutableStateOf(false)
        }
        if (dataLoading) {
            loadingDialog(message = "Getting data...")
        }
        lifecycleScope.launch {
            mainViewModel.months.collectLatest {
                months = it
                selectedMonth = it[mainViewModel.getSelectedMonthIndex()].month
            }
        }
        lifecycleScope.launch {
            mainViewModel.budgets.collectLatest {
                when (it) {
                    is ResultStates.Loading -> {
                        dataLoading = true
                    }
                    is ResultStates.Initial -> {
                        dataLoading = false
                    }
                    is ResultStates.Fail -> {
                        dataLoading = false
                        showToast(it.exception.message)

                    }
                    is ResultStates.Result<*> -> {
                        dataLoading = false
                        dataList = it.result as List<ExpenseDataClass>
                        budgets = dataList
                    }
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = budgetBgColor)
                .padding(horizontal = 15.dp)
        ) {

            addVerticalSpace(space = 15.dp)
            Text(
                text = "Hello ${userDataHolder.currentUser.name}, pick your budgeting month",
                style = textStyle(textSize = 18f),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            addVerticalSpace(space = 15.dp)
            LaunchedEffect(Unit) {
                monthState.animateScrollToItem(
                    mainViewModel.getSelectedMonthIndex(),
                    months.size
                )

            }
            LazyRow(state = monthState) {
                items(months) {
                    Card(
                        modifier = Modifier
                            .width(80.dp)
                            .height(50.dp),
                        shape = AbsoluteRoundedCornerShape(corner = CornerSize(size = 30.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = if (it.month == selectedMonth) budgetItemCardColor else Color.Gray),
                        onClick = {
                            selectedMonth = it.month
                            budgets = getFilterData(dataList, selectedMonth)
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = it.month,
                                style = textStyle(textSize = 13f)
                            )
                        }
                    }
                    addHorizontalSpace(space = 10.dp)
                }
            }
            addVerticalSpace(space = 15.dp)
            Text(
                text = "Hello ${userDataHolder.currentUser.name}, let’s view at your budgets",
                style = textStyle(textSize = 18f),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            addVerticalSpace(space = 15.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = true), state = rememberLazyListState()
            ) {
                items(
                    budgets
                ) {
                    val currencyAmount = it.budgets.values.sumOf { it.amount }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = budgetItemCardColor),
                        shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = getImage(it.title ?: "")),
                                    contentDescription = it.title,
                                    modifier = Modifier.size(36.dp)
                                )
                                addHorizontalSpace(space = 10.dp)
                                Text(text = it.title ?: "", style = textStyle(textSize = 14f))
                            }
                            addVerticalSpace(space = 10.dp)
                            Text(
                                text = "Current: RM ${currencyAmount}",
                                style = textStyle(textSize = 14f, color = Color.Red)
                            )
                            addVerticalSpace(space = 6.dp)
                            LinearProgressIndicator(
                                progress = calculatePercentage(currencyAmount, it.budget),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .shadow(elevation = 3.dp), color = Color.Red,
                                trackColor = Color.LightGray
                            )
                            Text(
                                text = "Budget: RM ${it.budget}",
                                style = textStyle(textSize = 14f),
                                modifier = Modifier.align(alignment = Alignment.End)
                            )
                            addVerticalSpace(space = 5.dp)
                            Button(
                                onClick = {
                                    selectedData = it
                                    addExpenseDialog = true
                                },
                                shape = AbsoluteRoundedCornerShape(corner = CornerSize(30.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = positiveButtonColor,
                                    disabledContainerColor = Color.DarkGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Add Expense",
                                    style = textStyle(textSize = 14f),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                            addVerticalSpace(space = 5.dp)

                            Button(
                                onClick = {
                                    selectedData = it
                                    changeBudgetDialog = true
                                },
                                shape = AbsoluteRoundedCornerShape(corner = CornerSize(30.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = positiveButtonColor,
                                    disabledContainerColor = Color.DarkGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Change Budget",
                                    style = textStyle(textSize = 14f),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }


                        }
                    }
                    addVerticalSpace(space = 15.dp)

                }
            }
            addVerticalSpace(space = 6.dp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = budgetItemCardColor),
                shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                onClick = {
                    newBudgetsDialog = true
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_btn_new_budget),
                        contentDescription = "Create a new budget",
                        modifier = Modifier.size(50.dp)
                    )
                    addHorizontalSpace(space = 10.dp)
                    Text(text = "Create a new budget", style = textStyle(textSize = 14f))
                }
            }
            addVerticalSpace(space = 15.dp)

        }
        if (newBudgetsDialog) {
            AlertDialog(
                onDismissRequest = {
                    newBudgetsDialog = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),

                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = homeBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 12.dp)
                    ) {
                        Text(
                            text = "Create your new budget",
                            style = textStyle(textSize = 18f, color = Color.White)
                        )
                        addVerticalSpace(space = 5.dp)
                        Text(
                            text = "Add budget Amount:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 2.dp)
                        OutlinedTextField(
                            value = totalBudget,
                            onValueChange = {
                                totalBudget = it

                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        addVerticalSpace(space = 5.dp)

                        Text(
                            text = "Select Budget Type:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 2.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .clickable {
                                    isExpended = !isExpended
                                }
                        ) {
                            Text(
                                text = selectedType.title,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(16.dp),
                                color = Color.White
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)

                            )
                            DropdownMenu(
                                expanded = isExpended,
                                onDismissRequest = {
                                    isExpended = false
                                },
                                modifier = Modifier
                                    .background(color = Color.Gray), properties = PopupProperties(
                                    dismissOnBackPress = true, dismissOnClickOutside = true
                                )
                            ) {
                                ExpenseTypes.values().forEachIndexed { index, item ->
                                    DropdownMenuItem(onClick = {
                                        selectedType = item
                                        isExpended = false
                                    }, text = {
                                        Text(
                                            text = item.title,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    })
                                }
                            }
                        }
                        addVerticalSpace(space = 10.dp)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                            onClick = {
                                if (totalBudget.isEmpty()) {
                                    showToast("Fill complete info.")
                                } else {
                                    newBudgetsDialog = false
                                    createNewBudget(totalBudget, selectedType)
                                }
                            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Create Budget",
                                style = textStyle(textSize = 12f, Color.Red),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }

                    }
                }
            }
        }
        if (createBudget.value) {
            loadingDialog(message = "Creating budget..")
        }
        if (addExpenseDialog) {
            AlertDialog(
                onDismissRequest = { addExpenseDialog = false }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),

                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = homeBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 15.dp)
                    ) {
                        Text(
                            text = "Add Expense in (${selectedData.title})",
                            style = textStyle(textSize = 18f, color = Color.White)
                        )
                        addVerticalSpace(space = 10.dp)
                        Text(
                            text = "Enter Expense Amount:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 5.dp)
                        OutlinedTextField(
                            value = expenseAmount,
                            onValueChange = {
                                expenseAmount = it
                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )

                        addVerticalSpace(space = 20.dp)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                            onClick = {
                                if (expenseAmount.isEmpty()) {
                                    showToast("Fill complete info.")
                                } else {
                                    addExpenseDialog = false
                                    addExpenseIntoBudget(expenseAmount, selectedData)
                                }
                            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Add Expense",
                                style = textStyle(textSize = 12f, Color.Red),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }

                    }
                }
            }
        }
        if (changeBudgetDialog) {
            totalBudget = selectedData.budget.toString()
            AlertDialog(
                onDismissRequest = { changeBudgetDialog = false }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),

                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = homeBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 15.dp)
                    ) {
                        Text(
                            text = "Update Budget in (${selectedData.title})",
                            style = textStyle(textSize = 18f, color = Color.White)
                        )
                        addVerticalSpace(space = 10.dp)
                        Text(
                            text = "Enter Budget Amount:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 5.dp)
                        OutlinedTextField(
                            value = totalBudget,
                            onValueChange = {
                                totalBudget = it
                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )

                        addVerticalSpace(space = 20.dp)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                            onClick = {
                                if (totalBudget.isEmpty()) {
                                    showToast("Fill complete info.")
                                } else {
                                    changeBudgetDialog = false
                                    updateBudget(totalBudget, selectedData)
                                }
                            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Update Budget",
                                style = textStyle(textSize = 12f, Color.Red),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }

                    }
                }
            }
        }

    }

    private fun getFilterData(
        dataList: List<ExpenseDataClass>,
        selectedMonth: String?
    ): List<ExpenseDataClass> {
        val data: ArrayList<ExpenseDataClass> = arrayListOf()
        dataList.forEachIndexed { index, expenseDataClass ->
            if (expenseDataClass.time.dateTimeFormat("MMM").equals(selectedMonth, true)) {
                data.add(expenseDataClass)
            }
        }
        return data
    }


    private fun updateBudget(totalBudget: String, selectedData: ExpenseDataClass) {
        mainViewModel.updateBudget(totalBudget.toInt(), selectedData.pushId)
    }

    private fun addExpenseIntoBudget(expenseAmount: String, selectedData: ExpenseDataClass) {
        val pushKey = fireBaseRefrences.reference.push().key
        val map: HashMap<String, ExpenseDataClass.Expense> = selectedData.budgets
        map[pushKey!!] = ExpenseDataClass.Expense(
            System.currentTimeMillis(),
            expenseAmount.toInt(),
            pushKey,
            selectedData.title
        )

        mainViewModel.addExpenseIntoBudget(
            map,
            selectedData,
            object : ResultCallBack<HashMap<String, ExpenseDataClass.Expense>> {
                override fun onSuccess(result: HashMap<String, ExpenseDataClass.Expense>) {
                    mainViewModel.loadSavings()
                }

                override fun onFail(message: java.lang.Exception) {
                }

            })
    }

    private fun createNewBudget(totalBudget: String, selectedType: ExpenseTypes) {
        val data = ExpenseDataClass(title = selectedType.title, budget = totalBudget.toInt())
        data.time = System.currentTimeMillis()
        mainViewModel.createNewBudget(data, resultCallBack = object :
            ResultCallBack<ExpenseDataClass> {
            override fun onSuccess(result: ExpenseDataClass) {
                createBudget.value = false
                showToast("Data add successful")
            }

            override fun onFail(message: Exception) {
                createBudget.value = false
                showToast(message = message.message)
            }

        })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private
    @Composable
    fun SavingScreen(title: String, navController: NavHostController, modifier: Modifier) {
        window.statusBarColor = savingBgColor.toArgb()
        if (mainViewModel.savings.value == ResultStates.Initial) {
            mainViewModel.loadSavings()
        }
        var newGoalDialog by remember { mutableStateOf(false) }
        var selectedData by remember { mutableStateOf(SavingDataClass()) }
        var goalAmount by remember { mutableStateOf("") }
        var isExpended by remember { mutableStateOf(false) }
        var depositAmount by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(SavingTypes.Emergency) }
        var savings by remember { mutableStateOf(listOf<SavingDataClass>()) }
        var depositDialogShow by remember {
            mutableStateOf(false)
        }
        var dataLoading by remember {
            mutableStateOf(false)
        }
        lifecycleScope.launch {
            mainViewModel.savings.collectLatest {
                when (it) {
                    is ResultStates.Loading -> {
                        dataLoading = true
                    }
                    is ResultStates.Initial -> {
                        dataLoading = false
                    }
                    is ResultStates.Fail -> {
                        dataLoading = false
                        showToast(it.exception.message)

                    }
                    is ResultStates.Result<*> -> {
                        dataLoading = false
                        savings = it.result as List<SavingDataClass>
                    }
                }
            }
        }
        if (dataLoading) {
            loadingDialog(message = "Getting data...")
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = savingBgColor)
                .padding(horizontal = 15.dp)
        ) {

            addVerticalSpace(space = 15.dp)
            Text(
                text = "Hello ${userDataHolder.currentUser.name}, let’s view at your saving goals ",
                style = textStyle(textSize = 18f),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = true)
            ) {
                items(savings)
                {
                    addVerticalSpace(space = 15.dp)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = expenseItemBgColor),
                        shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            val currentSaving = it.savings.toList().sumOf { it.second.amount }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = getImage(it.title ?: "")),
                                    contentDescription = it.title,
                                    modifier = Modifier.size(36.dp)
                                )
                                addHorizontalSpace(space = 10.dp)
                                Text(text = it.title ?: "", style = textStyle(textSize = 14f))
                            }
                            addVerticalSpace(space = 10.dp)
                            Text(
                                text = "Current: RM ${currentSaving}",
                                style = textStyle(textSize = 14f, color = positiveButtonColor)
                            )
                            addVerticalSpace(space = 6.dp)
                            Log.d(
                                "rtert",
                                calculatePercentage(currentSaving, it.goal).toString()
                            )
                            LinearProgressIndicator(
                                progress = calculatePercentage(currentSaving, it.goal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .shadow(elevation = 3.dp),
                                color = positiveButtonColor,
                                trackColor = Color.LightGray
                            )
                            Text(
                                text = "Goal: RM ${it.goal}",
                                style = textStyle(textSize = 14f),
                                modifier = Modifier.align(alignment = Alignment.End)
                            )
                            addVerticalSpace(space = 5.dp)
                            Button(
                                onClick = {
                                    selectedData = it
                                    depositDialogShow = true
                                },
                                shape = AbsoluteRoundedCornerShape(corner = CornerSize(30.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PurpleDark,
                                    disabledContainerColor = Color.DarkGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Add deposits",
                                    style = textStyle(textSize = 14f),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                            addVerticalSpace(space = 5.dp)
                            Button(
                                onClick = {
                                    startNewActivity(
                                        DepositActivity::class.java,
                                        false
                                    ) { mIntent ->
                                        mIntent.putExtra("collection", it.savings)
                                    }
                                },
                                shape = AbsoluteRoundedCornerShape(corner = CornerSize(30.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PurpleDark,
                                    disabledContainerColor = Color.DarkGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "View list of deposits",
                                    style = textStyle(textSize = 14f),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }

                        }
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = expenseItemBgColor),
                shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                onClick = {
                    newGoalDialog = true
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_saving_new_goal),
                        contentDescription = "NewGoal",
                        modifier = Modifier.size(50.dp)
                    )
                    addHorizontalSpace(space = 10.dp)
                    Text(text = "Create a new goal", style = textStyle(textSize = 14f))
                }
            }
            addVerticalSpace(space = 15.dp)

        }
        if (newGoalDialog) {
            AlertDialog(
                onDismissRequest = {
                    newGoalDialog = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),

                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = homeBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 12.dp)
                    ) {
                        Text(
                            text = "Create your new goal",
                            style = textStyle(textSize = 18f, color = Color.White)
                        )
                        addVerticalSpace(space = 5.dp)
                        Text(
                            text = "Add Goal Amount:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 2.dp)
                        OutlinedTextField(
                            value = goalAmount,
                            onValueChange = {
                                goalAmount = it

                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        addVerticalSpace(space = 5.dp)
                        Text(
                            text = "Add Some Deposit:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 2.dp)
                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = {
                                depositAmount = it
                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        addVerticalSpace(space = 5.dp)
                        Text(
                            text = "Select Saving Type:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 2.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .clickable {
                                    isExpended = !isExpended
                                }
                        ) {
                            Text(
                                text = selectedType.title,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(16.dp),
                                color = Color.White
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)

                            )
                            DropdownMenu(
                                expanded = isExpended,
                                onDismissRequest = {
                                    isExpended = false
                                },
                                modifier = Modifier
                                    .background(color = Color.Gray), properties = PopupProperties(
                                    dismissOnBackPress = true, dismissOnClickOutside = true
                                )
                            ) {
                                SavingTypes.values().forEachIndexed { index, item ->
                                    DropdownMenuItem(onClick = {
                                        selectedType = item
                                        isExpended = false
                                    }, text = {
                                        Text(
                                            text = item.title,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    })
                                }
                            }
                        }
                        addVerticalSpace(space = 10.dp)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                            onClick = {
                                if (goalAmount.isEmpty() || depositAmount.isEmpty()) {
                                    showToast("Fill complete info.")
                                } else {
                                    newGoalDialog = false
                                    isLoading.value = true
                                    createNewGoal(goalAmount, depositAmount, selectedType)
                                }
                            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Create Goal",
                                style = textStyle(textSize = 12f, Color.Red),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }

                    }
                }
            }
        }
        if (isLoading.value) {
            loadingDialog(message = "Creating goal..")
        }
        if (depositDialogShow) {
            AlertDialog(
                onDismissRequest = { depositDialogShow = false }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),

                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = homeBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 15.dp)
                    ) {
                        Text(
                            text = "Add Deposit in (${selectedData.title})",
                            style = textStyle(textSize = 18f, color = Color.White)
                        )
                        addVerticalSpace(space = 10.dp)
                        Text(
                            text = "Enter Deposit Amount:",
                            style = textStyle(textSize = 12f, color = Color.White)
                        )
                        addVerticalSpace(space = 5.dp)
                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = {
                                depositAmount = it
                            },
                            modifier = Modifier
                                .background(
                                    color = textInputColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )

                        addVerticalSpace(space = 20.dp)
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                            onClick = {
                                if (depositAmount.isEmpty()) {
                                    showToast("Fill complete info.")
                                } else {
                                    depositDialogShow = false
                                    addDepositIntoSavings(depositAmount, selectedData)
                                }
                            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Add deposit",
                                style = textStyle(textSize = 12f, Color.Red),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }

                    }
                }
            }
        }
    }


    private fun addDepositIntoSavings(depositAmount: String, data: SavingDataClass) {
        val pushKey = fireBaseRefrences.reference.push().key
        val map: HashMap<String, SavingDataClass.Savings> = data.savings
        map[pushKey!!] =
            SavingDataClass.Savings(System.currentTimeMillis(), depositAmount.toInt(), pushKey)

        mainViewModel.addDepositIntoSavings(
            map,
            data,
            object : ResultCallBack<HashMap<String, SavingDataClass.Savings>> {
                override fun onSuccess(result: HashMap<String, SavingDataClass.Savings>) {
                    mainViewModel.loadSavings()
                }

                override fun onFail(message: java.lang.Exception) {
                }

            })
    }

    private fun createNewGoal(
        goalAmount: String,
        depositAmount: String,
        selectedType: SavingTypes

    ) {
        val data = SavingDataClass(title = selectedType.title, goal = goalAmount.toInt())
        val pushId = fireBaseRefrences.reference.push().key
        data.savings = hashMapOf(
            Pair(
                pushId!!,
                SavingDataClass.Savings(
                    System.currentTimeMillis(),
                    amount = depositAmount.toInt(),
                    savingPushId = pushId
                )
            )
        )
        mainViewModel.createSavingGoal(data, resultCallBack = object :
            ResultCallBack<SavingDataClass> {
            override fun onSuccess(result: SavingDataClass) {
                isLoading.value = false
                showToast("Data add successful")
            }

            override fun onFail(message: Exception) {
                showToast(message = message.message)

            }

        })
    }

    private val emptyPieDta: PieData
        get() {
            val pieEntries = listOf(
                PieEntry(40f, ""),
                PieEntry(40f, ""),
                PieEntry(40f, "")
            )
            val pieDataSet = PieDataSet(pieEntries, "Expense Chart")
            // Apply some styling to the chart
            pieDataSet.apply {
                colors = ColorTemplate.COLORFUL_COLORS.toList()
                valueTextSize = 16f
                sliceSpace = 5f
            }
            return PieData(pieDataSet)
        }

    private fun getPieData(data: List<ExpenseDataClass>): PieData {
        val pieEntries: ArrayList<PieEntry> = arrayListOf()
        data.groupBy { it.title }.forEach {
            var count = 0
            it.value.forEachIndexed { index, expenseDataClass ->
                count += expenseDataClass.budgets.size
            }
            pieEntries.add(PieEntry(count.toFloat(), it.key))
        }
        val pieDataSet = PieDataSet(pieEntries, "Expense Chart")
        pieDataSet.apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 16f
            sliceSpace = 5f
        }
        return PieData(pieDataSet)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private
    @Composable
    fun ExpenseScreen(title: String, navController: NavHostController, modifier: Modifier) {
        var pieData by remember { mutableStateOf(emptyPieDta) }
        var selectedDate by remember { mutableStateOf(mainViewModel.selectedDate.value) }

        window.statusBarColor = expenseBgColor.toArgb()
        if (mainViewModel.budgets.value == ResultStates.Initial) {
            mainViewModel.loadBudgets()
        }
        var budgets by remember {
            mutableStateOf(listOf<ExpenseDataClass.Expense>())
        }
        var tempData by remember {
            mutableStateOf(listOf<ExpenseDataClass.Expense>())
        }
        var highestExpense by remember {
            mutableStateOf(ExpenseDataClass.Expense())
        }
        var averageExpense by remember {
            mutableStateOf(0)
        }
        lifecycleScope.launch {
            mainViewModel.budgets.collectLatest {
                when (it) {
                    is ResultStates.Loading -> {
                    }
                    is ResultStates.Initial -> {
                    }
                    is ResultStates.Fail -> {
                        showToast(it.exception.message)
                    }
                    is ResultStates.Result<*> -> {
                        val expenseDataList = it.result as List<ExpenseDataClass>
                        val newList: ArrayList<ExpenseDataClass.Expense> = ArrayList()
                        if (expenseDataList.isNotEmpty()) {
                            expenseDataList.forEach {
                                it.budgets.forEach { s, expense ->
                                    newList.add(expense)
                                }
                            }
                            pieData = getPieData(expenseDataList)
                        }
                        tempData = newList
                        if (newList.isNotEmpty()) {
                            highestExpense = newList.maxBy { max -> max.amount }
                            averageExpense =
                                newList.map { map -> map.amount }.average().roundToInt()
                        }
                    }
                }
            }

        }
        if (tempData.isNotEmpty()) {
            budgets = getFilterExpenses(tempData, selectedDate)
        }
        lifecycleScope.launch {
            mainViewModel.selectedDate.collectLatest {
                selectedDate = it
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = expenseBgColor)
                .verticalScroll(state = rememberScrollState(), enabled = true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            addVerticalSpace(space = 15.dp)
            Text(
                text = "Hello ${userDataHolder.currentUser.name}, please select the month to view\nyour expenses",
                style = textStyle(textSize = 18f),
                textAlign = TextAlign.Center
            )
            addVerticalSpace(space = 10.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(enabled = true, onClick = {
                            mainViewModel.moveBackwardMonth()
                        })
                )
                Text(
                    text = selectedDate,
                    style = textStyle(textSize = 16f),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = "Next",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(enabled = true, onClick = {
                            mainViewModel.moveForwardMonth()
                        })
                )
            }

            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        data = pieData
                        description.isEnabled = false
                        setUsePercentValues(true)
                        legend.isEnabled = false
                        isDrawHoleEnabled = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = AbsoluteRoundedCornerShape(70f),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    ),
                    colors = CardDefaults.cardColors(
                        expenseCardColor
                    )

                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "RM $averageExpense",
                            style = textStyle(textSize = 16f, Color.Red), modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(), textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Average Expenses",
                            style = textStyle(textSize = 16f), modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(), textAlign = TextAlign.Center
                        )
                    }
                }
                addHorizontalSpace(5.dp)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = AbsoluteRoundedCornerShape(70f),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    ),
                    colors = CardDefaults.cardColors(
                        expenseCardColor
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "${highestExpense.title} RM ${highestExpense.amount}",
                            style = textStyle(textSize = 16f, color = Color.Red),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(), textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Highest Expenses",
                            style = textStyle(textSize = 16f),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(), textAlign = TextAlign.Center
                        )

                    }
                }
            }
            addVerticalSpace(space = 10.dp)

            Text(
                text = "Latest Expenses",
                style = textStyle(textSize = 16f)
            )
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(all = 10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = true),
            ) {
                this.items(
                    budgets
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        addVerticalSpace(space = 7.dp)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = CardDefaults.cardColors(containerColor = expenseItemBgColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                addHorizontalSpace(space = 10.dp)
                                Image(
                                    painter = painterResource(id = getImage(it.title ?: "")),
                                    contentDescription = it.title,
                                    modifier = Modifier.size(30.dp)
                                )
                                addHorizontalSpace(space = 10.dp)

                                Text(
                                    text = it.title ?: "",
                                    style = textStyle(textSize = 14f)
                                )
                                Text(
                                    text = "RM ${it.amount}",
                                    style = textStyle(textSize = 14f, color = Color.Red),
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                        .fillMaxWidth(), textAlign = TextAlign.End
                                )
                            }

                        }
                    }

                }
            }
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = expenseButtonColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp)),
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        dataHolder.data = budgets
                        startNewActivity(ExpensesActivity::class.java, false)
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "View List of All Expenses", style = textStyle(textSize = 14f))
                    }
                }
            }
        }

    }

    private fun getFilterExpenses(
        tempData: List<ExpenseDataClass.Expense>,
        selectedDate: String
    ): List<ExpenseDataClass.Expense> {
        return tempData.filter { it.time.dateTimeFormat("MMM yyyy").equals(selectedDate, true) }
    }

    private fun calculatePercentage(value: Int, total: Int): Float {
        val percent = (value.times(100).div(total)).toFloat()
        val dec = percent / 100
        return dec
    }
}


