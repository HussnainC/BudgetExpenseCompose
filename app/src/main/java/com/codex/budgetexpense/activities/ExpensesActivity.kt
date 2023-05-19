package com.codex.budgetexpense.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.ExpenseDataClass
import com.codex.budgetexpense.ui.theme.*
import com.codex.budgetexpense.utils.getImage
import com.codex.budgetexpense.utils.setNewContent

class ExpensesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            setNewContent(statusBarColor = positiveButtonColor) {
                MainScreen(dataHolder.data as List<ExpenseDataClass.Expense>)
            }
        }
    }

    @Composable
    fun MainScreen(data: List<ExpenseDataClass.Expense>) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .verticalScroll(state = rememberScrollState(), enabled = true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .background(color = positiveButtonColor)
            ) {
                Text(
                    text = "List of All Expenses",
                    style = textStyle(textSize = 22f),
                )

            }

            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(all = 10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = true)
            ) {
                this.items(
                    data
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
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.Red
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                shape = AbsoluteRoundedCornerShape(corner = CornerSize(25.dp)),
                onClick = {
                    finish()
                }
            ) {
                Text(
                    text = "Back to Expenses Page",
                    style = textStyle(textSize = 18f, Color.Red),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                )
            }
            addVerticalSpace(space = 15.dp)

        }
    }

}

