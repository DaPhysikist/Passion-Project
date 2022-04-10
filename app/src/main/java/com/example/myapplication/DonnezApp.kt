package com.example.myapplication

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import kotlin.isInitialized as isInitialized

class DonnezApp : Application() {

    override fun onCreate(){
        super.onCreate()

        try {
            // Add this line, to include the Auth plugin
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("DonnezApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("DonnezApp", "Could not initialize Amplify", error)
        }
    }
}