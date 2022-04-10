package com.example.myapplication

import com.amazonaws.regions.Regions
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import java.lang.Exception

class CognitoSettingsKotlin(private val context: Context) {
    val userPoolId = " us-west-1_mC7L5JDyq"
    val clientId = "5bne5jd13ggbngkpgod6pbfr0b"
    val clientSecret = "97s6h3eg2d20dtbqtrh423cf1sif66v2norrn19djp9goq9l6cg"
    val cognitoRegion = Regions.US_WEST_1
    private var userPassword: String? = null
    val userPool: CognitoUserPool
    private val userAttributes: CognitoUserAttributes
    fun signUpInBackground(userId: String?, password: String?) {
        userPool.signUpInBackground(userId, password, userAttributes, null, signUpCallBack)
    }

    var signUpCallBack: SignUpHandler = object : SignUpHandler {
        fun onSuccess(
            user: CognitoUser?,
            userConfirmed: Boolean,
            cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?
        ) {
            //if sign-up was successful
            Log.d(ContentValues.TAG, "Sign-up success")
            Toast.makeText(context, "Sign-up success", Toast.LENGTH_SHORT).show()

            //checks if user needs to be confirmed
            if (!userConfirmed) {
                //user must be confirmed and code will be sent to confirm user
                //cognitoUserCodeDeliveryDetails will indicate if code was sent
            } else {
                //user already has been confirmed
                Toast.makeText(context, "Error: User Confirmed before", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            TODO("Not yet implemented")
        }

        override fun onFailure(exception: Exception) {
            //if sign-up failed
            Toast.makeText(context, "Sign-up failed", Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, "Sign-up failed: $exception")
        }
    }

    fun confirmUser(userId: String?, code: String?) {
        val cognitoUser = userPool.getUser(userId)
        cognitoUser.confirmSignUpInBackground(code, false, confirmationCallback)
    }

    //Callback handler for confirmSignUp API
    var confirmationCallback: GenericHandler = object : GenericHandler {
        override fun onSuccess() {
            //User successfully confirmed
            Toast.makeText(context, "User Confirmed", Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(exception: Exception) {
            //User confirmation failed, check exception for cause
            Log.i(ContentValues.TAG, "Sign up failure: " + exception.localizedMessage)
        }
    }

    fun addAttribute(key: String?, value: String?) {
        userAttributes.addAttribute(key, value)
    }

    fun userLogin(userId: String?, password: String?) {
        val cognitoUser = userPool.getUser(userId)
        userPassword = password
        cognitoUser.getSessionInBackground(authenticationHandler)
    }

    //Callback handler for sign-in process
    var authenticationHandler: AuthenticationHandler = object : AuthenticationHandler {
        override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice) {
            Toast.makeText(context, "Sign in success", Toast.LENGTH_SHORT).show()
        }

        override fun getAuthenticationDetails(
            authenticationContinuation: AuthenticationContinuation,
            userId: String
        ) {
            // The API needs user sign-in credentials to continue
            val authenticationDetails = AuthenticationDetails(userId, userPassword, null)
            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails)
            // Allow the sign-in to continue
            authenticationContinuation.continueTask()
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
        }

        override fun authenticationChallenge(continuation: ChallengeContinuation) {}
        override fun onFailure(exception: Exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(context, "Sign in Failure", Toast.LENGTH_SHORT).show()
        }
    }

    init {
        userPool = CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion)
        userAttributes = CognitoUserAttributes()
    }
}