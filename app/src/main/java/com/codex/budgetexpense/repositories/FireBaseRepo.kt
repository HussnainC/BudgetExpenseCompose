package com.codex.budgetexpense.repositories

import android.content.Context
import com.codex.budgetexpense.dataClasses.ExpenseDataClass
import com.codex.budgetexpense.dataClasses.SavingDataClass
import com.codex.budgetexpense.dataClasses.UserDataClass
import com.codex.budgetexpense.interfaces.ResultCallBack
import com.codex.budgetexpense.utils.Constants
import com.codex.budgetexpense.utils.FireBaseRefrences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.HashMap

class FireBaseRepo(
    val context: Context,
    val fireBaseRefrences: FireBaseRefrences,
    val firebaseAuth: FirebaseAuth
) {
    fun loadUserData(resultCallBack: ResultCallBack<UserDataClass>) {
        fireBaseRefrences.reference.child(firebaseAuth.uid!!).child(Constants.Account)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        resultCallBack.onSuccess(snapshot.getValue(UserDataClass::class.java) as UserDataClass)
                    } catch (ex: java.lang.Exception) {
                        resultCallBack.onFail(ex)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun signUpUser(userData: UserDataClass, resultCallBack: ResultCallBack<UserDataClass>) {
        firebaseAuth.createUserWithEmailAndPassword(userData.email!!, userData.password!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    userData.uId = it.result.user?.uid
                    fireBaseRefrences.reference.child(userData.uId!!).child(Constants.Account)
                        .setValue(userData).addOnCompleteListener {
                            if (it.isSuccessful) {
                                resultCallBack.onSuccess(userData)
                            } else {
                                resultCallBack.onFail(Exception("Fail to create account"))
                            }
                        }
                } else {
                    resultCallBack.onFail(Exception("Fail to create account"))
                }
            }.addOnFailureListener {
                resultCallBack.onFail(it)
            }
    }

    fun createSavingGoal(data: SavingDataClass, resultCallBack: ResultCallBack<SavingDataClass>) {
        data.pushId = fireBaseRefrences.reference.push().key
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Savings)
            .child(data.pushId!!)
            .setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBack.onSuccess(data)
                }
            }.addOnFailureListener {
                resultCallBack.onFail(it)
            }
    }

    fun loadSavings(resultCallBack: ResultCallBack<List<SavingDataClass>>) {
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Savings)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data: ArrayList<SavingDataClass> = arrayListOf()
                    snapshot.children.forEach {
                        data.add(it.getValue(SavingDataClass::class.java) as SavingDataClass)
                    }
                    resultCallBack.onSuccess(data.reversed())
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun addDepositIntoSavings(
        map: HashMap<String, SavingDataClass.Savings>,
        data: SavingDataClass,
        resultCallBack: ResultCallBack<HashMap<String, SavingDataClass.Savings>>
    ) {
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Savings)
            .child(data.pushId!!).child(Constants.Savings.lowercase()).setValue(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBack.onSuccess(map)
                }
            }
    }

    fun loadBudgets(resultCallBack: ResultCallBack<List<ExpenseDataClass>>) {
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Budgets)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data: ArrayList<ExpenseDataClass> = arrayListOf()
                    snapshot.children.forEach {
                        data.add(it.getValue(ExpenseDataClass::class.java) as ExpenseDataClass)
                    }
                    if (data.isNotEmpty()) {
                        resultCallBack.onSuccess(data.reversed())
                    } else {
                        resultCallBack.onSuccess(data)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun createBudget(data: ExpenseDataClass, resultCallBack: ResultCallBack<ExpenseDataClass>) {
        data.pushId = fireBaseRefrences.reference.push().key
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Budgets)
            .child(data.pushId!!)
            .setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBack.onSuccess(data)
                }
            }.addOnFailureListener {
                resultCallBack.onFail(it)
            }
    }

    fun addExpenseIntoBudget(
        map: HashMap<String, ExpenseDataClass.Expense>,
        selectedData: ExpenseDataClass,
        resultCallBack: ResultCallBack<HashMap<String, ExpenseDataClass.Expense>>
    ) {
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Budgets)
            .child(selectedData.pushId!!).child(Constants.Budgets.lowercase()).setValue(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBack.onSuccess(map)
                }
            }
    }

    fun updateBudget(amount: Int, pushId: String?) {
        fireBaseRefrences.reference.child(firebaseAuth.currentUser?.uid!!).child(Constants.Budgets)
            .child(pushId!!).child("budget").setValue(amount)
    }

}