package com.codex.budgetexpense.baseClass

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.DialogProperties
import com.codex.budgetexpense.R
import com.codex.budgetexpense.utils.DataHolderClass
import com.codex.budgetexpense.utils.FireBaseRefrences

import com.codex.budgetexpense.utils.UserDataHolder
import com.codex.budgetexpense.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : ComponentActivity() {
    protected val firebaseAuth: FirebaseAuth by inject()
    protected val mainViewModel: MainViewModel by viewModel()
    protected val userDataHolder: UserDataHolder by inject()
    protected val fireBaseRefrences: FireBaseRefrences by inject()
    protected val dataHolder: DataHolderClass by inject()

    protected var isLoading: MutableState<Boolean> = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun loadingDialog(message: String) {
        AlertDialog(
            onDismissRequest = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(vertical = 10.dp)
                .background(color = Color.DarkGray),
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = message, style = TextStyle(
                            color = Color.White, fontSize = TextUnit(
                                18f,
                                TextUnitType.Sp
                            ), fontFamily = customFont
                        ), modifier = Modifier.padding(start = 10.dp)
                    )
                }

            }
        }
    }


    protected val customFont: FontFamily
        get() {
            return FontFamily(
                listOf(
                    Font(resId = R.font.raleway),
                    Font(resId = R.font.raleway_medium),
                    Font(resId = R.font.raleway_semibold),
                    Font(resId = R.font.raleway_bold)
                )
            )
        }

    @Composable
    fun textStyle(
        textSize: Float,
        color: Color = Color.Black,
        bold: FontWeight = FontWeight.Bold
    ): TextStyle {
        return TextStyle(
            fontSize = TextUnit(
                textSize,
                TextUnitType.Sp
            ), fontWeight = bold, color = color, fontFamily = customFont
        )
    }

    @Composable
    fun addVerticalSpace(space: Dp) {
        Spacer(modifier = Modifier.height(space))
    }

    @Composable
    fun addHorizontalSpace(space: Dp) {
        Spacer(modifier = Modifier.width(space))
    }
}