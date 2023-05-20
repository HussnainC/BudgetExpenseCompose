package com.codex.budgetexpense.activities

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setNewContent {
                LoginScreen()
            }
        }
    }


    @Composable
    fun LoginScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        if (isLoading.value) {
            loadingDialog("Logging...")
        }

        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .background(color = Color.Black)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_ic),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 30.dp),
                contentDescription = "Login Image"
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    text = "Email:",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
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
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(20.dp)

                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    text = "Password:",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
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
                    textStyle = TextStyle(color = Color.White),
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
                            if (email.isEmpty() || password.isEmpty()) {
                                showToast("All fields are required...")
                            } else {
                                isLoading.value = true
                                startLogin(email, password)
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
                            text = "Login",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = TextUnit(value = 18f, type = TextUnitType.Sp)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 5.dp))

                    Button(
                        onClick = {
                            startNewActivity(SignupActivity::class.java, false)
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
                            text = "Sign Up",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = TextUnit(value = 18f, type = TextUnitType.Sp)
                            )
                        )
                    }
                }

            }
        }

    }

    private fun startLogin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                mainViewModel.loadUserInfo(resultCallback)
            } else {
                isLoading.value = false
                showToast("username or password is incorrect")
            }
        }.addOnFailureListener {
            isLoading.value = false
            showToast("Fail to login")
        }
    }

    private val resultCallback = object : ResultCallBack<UserDataClass> {
        override fun onSuccess(result: UserDataClass) {
            userDataHolder.currentUser = result
            isLoading.value = false
            startNewActivity(MainActivity::class.java, true)
        }

        override fun onFail(message: Exception) {
            showToast(message.message)
            finishAffinity()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        BudgetExpenseTheme {
            LoginScreen()
        }
    }
}