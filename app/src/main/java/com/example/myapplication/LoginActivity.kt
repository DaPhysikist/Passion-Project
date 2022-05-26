package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.core.Amplify

class LoginActivity : AppCompatActivity() {
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText

    private var pointsData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val currentUser: AuthUser? = Amplify.Auth.currentUser

        pointsData = intent.getStringExtra("Points Data")

        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setListeners(pointsData)
    }

    private fun setListeners(pointsData: String?){
        usernameInput = findViewById(R.id.log_username)
        passwordInput = findViewById(R.id.log_password)

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener{
            validateEmptyForm(pointsData)
        }
    }

    private fun validateEmptyForm(pointsData: String?){
        val icon = AppCompatResources.getDrawable(applicationContext, android.R.drawable.ic_dialog_alert)

        icon?.setBounds(0,0,icon.minimumWidth,icon.minimumHeight)
        when
        {
            TextUtils.isEmpty(usernameInput.text.toString().trim())->{
                usernameInput.setError("Please Enter Username",icon)
            }
            TextUtils.isEmpty(passwordInput.text.toString().trim())->{
                passwordInput.setError("Please Enter Password",icon)
            }

            usernameInput.text.toString().isNotEmpty() &&
                    passwordInput.text.toString().isNotEmpty() ->
            {
                Amplify.Auth.signIn(usernameInput.text.toString(), passwordInput.text.toString(),
                    { result ->
                        if (result.isSignInComplete) {
                            if (pointsData != null){
                                val database = DatabaseAccess()
                                database.handleReceive(pointsData)
                                openMainAppScreen()
                            }
                            else {
                                openMainAppScreen()
                            }
                        } else {
                            showMessage("Sign in not complete")
                        }
                    },
                    { showMessage("Failed to sign in") }
                )
            }
        }
    }

    private fun openMainAppScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                .show()
        }
    }
}