package com.codex.budgetexpense.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.ExpenseDataClass
import com.codex.budgetexpense.dataClasses.SavingDataClass
import com.codex.budgetexpense.sealdclasses.ResultStates
import com.codex.budgetexpense.ui.theme.*
import com.codex.budgetexpense.utils.setNewContent
import com.codex.budgetexpense.utils.showToast
import com.codex.budgetexpense.utils.startNewActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.statusBarColor = headerColor.toArgb()
        setContent {
            setNewContent(statusBarColor = headerColor) {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen() {
        val savings by remember { mutableStateOf(arrayListOf<Int>()) }
        val expenses by remember { mutableStateOf(arrayListOf<Int>()) }
        var dataLoading by remember {
            mutableStateOf(false)
        }
        if (mainViewModel.savings.value == ResultStates.Initial) {
            mainViewModel.loadSavings()
        }
        if (mainViewModel.budgets.value == ResultStates.Initial) {
            mainViewModel.loadBudgets()
        }
        if (dataLoading) {
            loadingDialog(message = "Getting Info...")
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
                        savings.clear()
                        (it.result as List<SavingDataClass>).forEach { loop ->
                            savings.add(loop.savings.toList().sumOf { sum -> sum.second.amount })
                        }
                    }
                }
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
                        expenses.clear()
                        (it.result as List<ExpenseDataClass>).forEach { loop ->
                            expenses.add(loop.budgets.toList().sumOf { sum -> sum.second.amount })
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = true
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = headerColor)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            PaddingValues(
                                top = 30.dp,
                                start = 15.dp,
                                end = 15.dp,
                                bottom = 15.dp
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_image),
                            contentDescription = "User Image",
                            modifier = Modifier.size(60.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.CenterVertically),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { logOutUser() },
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text(
                                    text = "LOG OUT", style = TextStyle(
                                        color = Color.White, fontSize = TextUnit(
                                            16f,
                                            TextUnitType.Sp
                                        ), fontWeight = FontWeight.Bold,fontFamily = customFont
                                    )
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Welcome Back to QUID,\n${userDataHolder.currentUser.name}",
                        style = TextStyle(
                            fontSize = TextUnit(
                                24f,
                                TextUnitType.Sp
                            ), fontWeight = FontWeight.Bold, color = Color.Black,fontFamily = customFont
                        )
                    )

                }

            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = homeBgColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello ${userDataHolder.currentUser.name}, here is a brief overview on your finances",
                        style = textStyle(textSize = 18f), textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = AbsoluteRoundedCornerShape(70f),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            ),
                            colors = CardDefaults.cardColors(
                                tealColor
                            )

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.height(30.dp))
                                Text(
                                    text = "Total Savings:",
                                    style = textStyle(textSize = 16f), modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(), textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "RM ${savings.sum()}",
                                    style = textStyle(textSize = 16f), modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(), textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = AbsoluteRoundedCornerShape(70f),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            ),
                            colors = CardDefaults.cardColors(
                                lightRedColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.height(30.dp))
                                Text(
                                    text = "Total Expenses:",
                                    style = textStyle(textSize = 16f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(), textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "RM ${expenses.sum()}",
                                    style = textStyle(textSize = 16f, color = Color.Black),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(), textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    addVerticalSpace(space = 12.dp)

                    Text(
                        text = "Hello ${userDataHolder.currentUser.name}, Please select\nyour bank account",
                        style = textStyle(textSize = 18f),
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    addVerticalSpace(space = 12.dp)
                    //Amex Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp),
                        shape = AbsoluteRoundedCornerShape(70f),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        colors = CardDefaults.cardColors(
                            amexCardColor
                        ), onClick = {
                            startNewActivity(NavigationDetailActivity::class.java, false)
                        }, enabled = true

                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 15.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_amex),
                                    contentDescription = "Amex Icon",
                                    modifier = Modifier.height(50.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = "Saving Account",
                                        style = textStyle(textSize = 16f)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = "BALANCE : RM ${savings.sum()}",
                                    style = textStyle(textSize = 18f)
                                )
                            }


                        }
                    }
                    addVerticalSpace(space = 16.dp)
                    //PayPal Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp),
                        shape = AbsoluteRoundedCornerShape(70f),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        onClick = {
                            startNewActivity(NavigationDetailActivity::class.java, false)
                        }, enabled = true

                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(color = payPalCardColor)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 15.dp)

                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_paypal),
                                        contentDescription = "PayPal E-Wallet",
                                        modifier = Modifier.height(50.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "PayPal E-Wallet",
                                            style = textStyle(textSize = 16f)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        text = "BALANCE : RM XX,XXXX",
                                        style = textStyle(textSize = 18f)
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    private fun logOutUser() {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
            startNewActivity(LoginActivity::class.java, true)
        }
    }


}

