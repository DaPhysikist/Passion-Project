package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.amplifyframework.core.Amplify
import java.util.*

class VerifyActivity : AppCompatActivity() {
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        supportActionBar?.hide()

        username = intent.getStringExtra(EXTRA_USERNAME)

        setListeners(username)
    }

    private fun setListeners(username:  String?){
        var confCode = findViewById<EditText>(R.id.conf_code)

        findViewById<Button>(R.id.btn_verify).setOnClickListener {
            validateEmptyForm(confCode, username)
        }
    }

    private fun validateEmptyForm(confCode: EditText, username: String?) {
        val icon = AppCompatResources.getDrawable(applicationContext, R.drawable.error_icon)

        icon?.setBounds(0, 0, icon.minimumWidth, icon.minimumHeight)
        when {
            TextUtils.isEmpty(confCode.text.toString().trim()) -> {
                confCode.setError("Please Enter Confirmation Code", icon)
            }

            confCode.text.toString().isNotEmpty() && username != null -> {
                if (confCode.text.toString().matches(Regex("^[0-9]{6,6}\$"))) {
                    signUpConfirmation(confCode.text.toString(), username)
                } else {
                    confCode.setError("Please Enter a Valid Confirmation Code", icon)
                }
            }
        }
    }

    private fun signUpConfirmation(code: String, username: String?) {
            username?.let{conf_username -> Amplify.Auth.confirmSignUp(
                conf_username,
                code,
                { openSignInScreen() },
                { showMessage("Confirmation Failure") }
            )
        }
    }

    private fun openSignInScreen() {
        val intent = Intent(this@VerifyActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                .show()
        }
    }
}