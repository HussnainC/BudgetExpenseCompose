package com.codex.budgetexpense.activities

import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codex.budgetexpense.R
import com.codex.budgetexpense.baseClass.BaseActivity
import com.codex.budgetexpense.dataClasses.UserDataClass
import com.codex.budgetexpense.interfaces.ResultCallBack
import com.codex.budgetexpense.ui.theme.BudgetExpenseTheme
import com.codex.budgetexpense.utils.setNewContent
import com.codex.budgetexpense.utils.showToast
import com.codex.budgetexpense.utils.startNewActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setNewContent {
                SplashScreen()
            }
        }
        Handler(mainLooper).postDelayed({
            if (firebaseAuth.currentUser != null) {
                mainViewModel.loadUserInfo(resultCallback)
            } else {
                launchNextActivity(LoginActivity::class.java)
            }
        }, 3000L)

    }

    private val resultCallback = object : ResultCallBack<UserDataClass> {
        override fun onSuccess(result: UserDataClass) {
            userDataHolder.currentUser = result
            launchNextActivity(MainActivity::class.java)
        }

        override fun onFail(message: Exception) {
            showToast(message.message)
            finishAffinity()
        }
    }

    private fun launchNextActivity(newClass: Class<*>) {
        startNewActivity(newClass, true)
    }


    @Composable
    fun SplashScreen() {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
            Image(
                painter = painterResource(id = R.drawable.main_ic),
                contentDescription = "Splash icon",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 20.dp).align(Alignment.Center)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        BudgetExpenseTheme {
            SplashScreen()
        }
    }
}