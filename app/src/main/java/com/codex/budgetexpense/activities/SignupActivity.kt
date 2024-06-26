package com.codex.budgetexpense.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.UserDataClass
import com.codex.budgetexpense.interfaces.ResultCallBack
import com.codex.budgetexpense.ui.theme.BudgetExpenseTheme
import com.codex.budgetexpense.ui.theme.positiveButtonColor
import com.codex.budgetexpense.ui.theme.textInputColor
import com.codex.budgetexpense.utils.setNewContent
import com.codex.budgetexpense.utils.showToast
import com.codex.budgetexpense.utils.startNewActivity
import java.lang.Exception

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setNewContent {
                SignUpScreen()
            }
        }
    }


    @Composable
    fun SignUpScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }

        if (isLoading.value) {
            loadingDialog("Signing...")
        }

        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .background(color = Color.Black)
                .fillMaxSize()
        ) {
            Text(
                text = "WELCOME TO \n" +
                        "QUID APPLICATION!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFont,
                    fontSize = TextUnit(24f, type = TextUnitType.Sp)
                )

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    text = "Name:",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFont,
                    )
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier
                        .background(
                            color = textInputColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxSize(),
                    textStyle = TextStyle(color = Color.White, fontFamily = customFont),
                    shape = RoundedCornerShape(20.dp)

                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    text = "Email Address:",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFont
                    )
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    modifier = Modifier
                        .background(
                            color = textInputColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxSize(),
                    textStyle = TextStyle(color = Color.White, fontFamily = customFont),
                    shape = RoundedCornerShape(20.dp)

                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    text = "Password:",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFont
                    )
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisibility = !passwordVisibility },
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(30.dp),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            val passwordVisibilityIcon = if (passwordVisibility) {
                                painterResource(id = R.drawable.hide)
                            } else {
                                painterResource(id = R.drawable.view)
                            }
                            Icon(
                                painter = passwordVisibilityIcon,
                                contentDescription = "Password Visibility Toggle"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .background(
                            color = textInputColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxSize(),
                    textStyle = TextStyle(color = Color.White, fontFamily = customFont),
                    shape = RoundedCornerShape(20.dp)

                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                                showToast("All fields are required...")
                            } else {
                                isLoading.value = true
                                startSignUp(name, email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp)
                            .background(color = positiveButtonColor),
                        shape = RectangleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = positiveButtonColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Register",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = TextUnit(value = 18f, type = TextUnitType.Sp),
                                fontFamily = customFont
                            )
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 5.dp))

                    Button(
                        onClick = {
                            finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp)
                            .background(color = Color.Red),
                        shape = RectangleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Back",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFont,
                                fontSize = TextUnit(value = 18f, type = TextUnitType.Sp)
                            )
                        )
                    }
                }

            }
        }

    }

    private fun startSignUp(name: String, email: String, password: String) {
        val userData = UserDataClass()
        userData.apply {
            this.name = name
            this.email = email
            this.password = password
        }
        mainViewModel.startSignUp(userData, object : ResultCallBack<UserDataClass> {
            override fun onSuccess(result: UserDataClass) {
                isLoading.value = false
                userDataHolder.currentUser = result
                showToast("Account create successful")
                finish()
            }

            override fun onFail(message: Exception) {
                isLoading.value = false
                showToast("Fail to Signup")
            }

        })

    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        BudgetExpenseTheme {
            SignUpScreen()
        }
    }
}