package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import java.util.*

const val EXTRA_USERNAME = "Pseudonym"

class RegisterActivity : AppCompatActivity() {
    /*
    var fullName: EditText = findViewById(R.id.reg_full_name)
    var email: EditText = findViewById(R.id.reg_email)
    var datePicker: DatePicker = findViewById(R.id.reg_date_picker)
    var regUsername: EditText = findViewById(R.id.reg_username)
    var regPassword: EditText = findViewById(R.id.reg_password)
    var confPassword: EditText = findViewById(R.id.reg_confirm_password)
*/
    var dd:String = ""
    var mm:String = ""
    var yyyy:String = ""
    var birthDate:String = ""
    var datePicked:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        setListeners()
    }

    private fun setListeners(){
        var fullName: EditText = findViewById(R.id.reg_full_name)
        var regEmail: EditText = findViewById(R.id.reg_email)
        var datePicker: DatePicker = findViewById(R.id.reg_date_picker)
        var regUsername: EditText = findViewById(R.id.reg_username)
        var regPassword: EditText = findViewById(R.id.reg_password)
        var confPassword: EditText = findViewById(R.id.reg_confirm_password)

        //birth date picker
        val dp = Calendar.getInstance()

        datePicker.init(dp.get(Calendar.YEAR), dp.get(Calendar.MONTH), dp.get(Calendar.DAY_OF_MONTH), DatePicker.OnDateChangedListener(){
                view, year, month, day ->
            val month = month + 1
            datePicked = true
            dd = day.toString()
            mm = month.toString()
            yyyy = year.toString()
        })

        birthDate = yyyy + "-" + mm + "-" + dd

        //Sign Up button click
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {
            validateEmptyForm(fullName, regEmail, regUsername, regPassword, confPassword)
        }
    }

    private fun validateEmptyForm(fullName: EditText, regEmail: EditText, regUsername: EditText, regPassword: EditText, confPassword: EditText){
        val email:String = regEmail.text.toString()
        val username: String = regUsername.text.toString()
        val password: String = regPassword.text.toString()

        val icon = AppCompatResources.getDrawable(applicationContext, R.drawable.error_icon)

        icon?.setBounds(0,0,icon.minimumWidth,icon.minimumHeight)
        when{
            TextUtils.isEmpty(fullName.text.toString().trim())->{
                fullName.setError("Please Enter Full Name",icon)
            }
            TextUtils.isEmpty(regEmail.text.toString().trim())->{
                regEmail.setError("Please Enter Email",icon)
            }
            TextUtils.isEmpty(regUsername.text.toString().trim())->{
                regUsername.setError("Please Enter Username",icon)
            }
            TextUtils.isEmpty(regPassword.text.toString().trim())->{
                regPassword.setError("Please Enter Password",icon)
            }
            TextUtils.isEmpty(confPassword.text.toString().trim())->{
                confPassword.setError("Please Enter Password Again",icon)
            }

            fullName.text.toString().isNotEmpty() &&
                    regEmail.text.toString().isNotEmpty() &&
                    datePicked == true &&
                    regUsername.text.toString().isNotEmpty() &&
                    regPassword.text.toString().isNotEmpty() &&
                    confPassword.text.toString().isNotEmpty() ->
            {
                if (regEmail.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                    if(regPassword.text.toString().matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?%^&+=])(?=\\S+$).{10,}$"))){
                        if (regPassword.text.toString().equals(confPassword.text.toString())){
                            val options = AuthSignUpOptions.builder()
                                .userAttribute(AuthUserAttributeKey.email(), email)
                                .build()
                            Amplify.Auth.signUp(username, password, options,
                                { goToSignUpConfirmation(username) },
                                { showMessage("Sign up failed") }
                            )
                        }
                        else {
                            confPassword.setError("Passwords does not match", icon)
                        }
                    }
                    else {
                        regPassword.setError("Please Enter a Valid Password (At least 10 characters, one number, one uppercase letter, one lowercase letter, one special character)",icon)
                    }
                }
                else {
                    regEmail.setError("Please Enter a Valid Email",icon)
                }
            }
        }
    }
    private fun goToSignUpConfirmation(username: String){
        val intent = Intent(this@RegisterActivity, VerifyActivity::class.java).apply {
            putExtra(EXTRA_USERNAME, username)
        }
        startActivity(intent)
    }

    private fun showMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                .show()
        }
    }
}