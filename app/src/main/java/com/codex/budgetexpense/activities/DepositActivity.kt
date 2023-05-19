package com.codex.budgetexpense.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.ExpenseDataClass
import com.codex.budgetexpense.dataClasses.SavingDataClass
import com.codex.budgetexpense.ui.theme.PurpleDark
import com.codex.budgetexpense.ui.theme.PurpleGrey80
import com.codex.budgetexpense.ui.theme.expenseItemBgColor
import com.codex.budgetexpense.ui.theme.positiveButtonColor
import com.codex.budgetexpense.utils.dateTimeFormat
import com.codex.budgetexpense.utils.setNewContent

class DepositActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                "collection",
                hashMapOf<String, SavingDataClass.Savings>()::class.java
            )
        } else {
            intent.getSerializableExtra(
                "collection"
            ) as HashMap<String, SavingDataClass.Savings>
        }
        setContent {
            setNewContent(statusBarColor = PurpleDark) {
                MainScreen(data ?: hashMapOf())
            }
        }
    }

    @Composable
    fun MainScreen(data: HashMap<String, SavingDataClass.Savings>) {
        var count: Int = 0
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
                    .background(color = PurpleDark)
            ) {
                Text(
                    text = "List of Deposit",
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
                    data.toList()
                ) {
                    count++
                    Column(modifier = Modifier.fillMaxWidth()) {
                        addVerticalSpace(space = 7.dp)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = CardDefaults.cardColors(containerColor = PurpleGrey80),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = AbsoluteRoundedCornerShape(corner = CornerSize(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                addHorizontalSpace(space = 10.dp)
                                Text(
                                    text = "$count.",
                                    style = textStyle(textSize = 14f)
                                )
                                addHorizontalSpace(space = 15.dp)

                                Text(
                                    text = it.second.time.dateTimeFormat(),
                                    style = textStyle(textSize = 14f, bold = FontWeight.Normal)
                                )
                                Text(
                                    text = "RM ${it.second.amount}",
                                    style = textStyle(textSize = 14f, color = positiveButtonColor),
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
                    text = "Back to Savings Page",
                    style = textStyle(textSize = 18f, Color.Red),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                )
            }
            addVerticalSpace(space = 15.dp)

        }
    }

}

