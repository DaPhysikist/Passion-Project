package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.content.res.AppCompatResources
import com.amplifyframework.core.Amplify

class LoginActivity : AppCompatActivity() {
    lateinit var logUsername: EditText
    lateinit var logPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setListeners()
    }

    private fun setListeners(){
        logUsername = findViewById(R.id.log_username)
        logPassword = findViewById(R.id.log_password)

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener{
            validateEmptyForm()
        }
    }

    private fun validateEmptyForm(){
        val icon = AppCompatResources.getDrawable(applicationContext, R.drawable.error_icon)

        icon?.setBounds(0,0,icon.minimumWidth,icon.minimumHeight)
        when
        {
            TextUtils.isEmpty(logUsername.text.toString().trim())->{
                logUsername.setError("Please Enter Username",icon)
            }
            TextUtils.isEmpty(logPassword.text.toString().trim())->{
                logPassword.setError("Please Enter Password",icon)
            }

            logUsername.text.toString().isNotEmpty() &&
                    logPassword.text.toString().isNotEmpty() ->
            {
                Amplify.Auth.signIn(logUsername.text.toString(), logPassword.text.toString(),
                    { result ->
                        if (result.isSignInComplete) {
                            openMainAppScreen()
                        } else {
                            showMessage("Sign in not complete")
                        }
                    },
                    { error -> showMessage("Failed to sign in") }
                )
            }
        }
    }

    private fun openMainAppScreen() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                .show()
        }
    }
}