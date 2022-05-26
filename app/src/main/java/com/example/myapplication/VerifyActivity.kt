package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.example.myapplication.data.Database
import kotlinx.coroutines.runBlocking
import java.util.*

class VerifyActivity : AppCompatActivity() {
    private var username: String? = null
    private var password: String? = null
    private var name: String? = null
    private var email: String? = null
    private var birthday: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        supportActionBar?.hide()

        username = intent.getStringExtra("Username")
        password = intent.getStringExtra("Password")
        name = intent.getStringExtra("Name")
        email = intent.getStringExtra("Email")
        birthday = intent.getStringExtra("Birthday")

        setListeners(username, password, name, email, birthday)
    }

    private fun setListeners(username: String?, password: String?, name: String?, email: String?, birthday: String?){
        val confCode = findViewById<EditText>(R.id.conf_code)

        findViewById<Button>(R.id.btn_verify).setOnClickListener {
            validateEmptyForm(confCode, username, password, name, email, birthday)
        }
    }

    private fun validateEmptyForm(confCode: EditText, username: String?, password: String?, name: String?, email: String?, birthday: String?) {
        val icon = AppCompatResources.getDrawable(applicationContext, android.R.drawable.ic_dialog_alert)

        icon?.setBounds(0, 0, icon.minimumWidth, icon.minimumHeight)
        when {
            TextUtils.isEmpty(confCode.text.toString().trim()) -> {
                confCode.setError("Please Enter Confirmation Code", icon)
            }

            confCode.text.toString().isNotEmpty() && username != null && password != null && name != null && email != null && birthday != null-> {
                if (confCode.text.toString().matches(Regex("^[0-9]{6,6}\$"))) {
                    signUpConfirmation(confCode.text.toString(), username, password, name, email, birthday)
                }
                else {
                    confCode.setError("Please Enter a Valid Confirmation Code", icon)
                }
            }
        }
    }

    private fun signUpConfirmation(code: String, username: String, password: String, name: String, email: String, birthday: String) {
            username.let{conf_username -> Amplify.Auth.confirmSignUp(
                conf_username,
                code,
                { signIn(username, password, name, email, birthday) },
                { showMessage("Confirmation Failure") }
            )
        }
    }

    private fun signIn (username: String, password: String, name: String, email: String, birthday: String){
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignInComplete) {
                    val database = DatabaseAccess()
                    database.createUser(name, email, birthday)
                    openMainScreen()
                } else {
                    showMessage("Sign in not complete")
                }
            },
            { showMessage("Failed to sign in") }
        )
    }

    fun openMainScreen(){
        val intent = Intent(this@VerifyActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                .show()
        }
    }
}