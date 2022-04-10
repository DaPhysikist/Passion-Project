package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import static android.content.ContentValues.TAG;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;

public class CognitoSettings {

    private String userPoolId = " us-west-1_mC7L5JDyq";
    private String clientId = "5bne5jd13ggbngkpgod6pbfr0b";
    private String clientSecret = "97s6h3eg2d20dtbqtrh423cf1sif66v2norrn19djp9goq9l6cg";
    private Regions cognitoRegion = Regions.US_WEST_1;

    private Context context;
    private String userPassword;

    private CognitoUserPool userPool;
    private CognitoUserAttributes userAttributes;

 //   public CognitoSettings(Context context) {
 //       this.context = context;
 //       userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
 //       userAttributes = new CognitoUserAttributes();
 //   }

  //  public void signUpInBackground(String userId, String password){
   //     userPool.signUpInBackground(userId, password, this.userAttributes, null, signUpCallBack);
  //  }

    //SignUpHandler signUpCallBack = new SignUpHandler() {
        //@Override
       // public void onSuccess(CognitoUser user, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            //if sign-up was successful
        //    Log.d(TAG, "Sign-up success");
       //     Toast.makeText(context, "Sign-up success", Toast.LENGTH_SHORT).show();

            //checks if user needs to be confirmed
      //      if(!userConfirmed){
                //user must be confirmed and code will be sent to confirm user
                //cognitoUserCodeDeliveryDetails will indicate if code was sent
      //      }
      //      else {
                //user already has been confirmed
       //        Toast.makeText(context, "Error: User Confirmed before", Toast.LENGTH_SHORT).show();
       //     }
 //       }
//
     //   @Override
     //   public void onFailure(Exception exception) {
    //        //if sign-up failed
     //       Toast.makeText(context,"Sign-up failed", Toast.LENGTH_SHORT).show();
     //       Log.d(TAG, "Sign-up failed: " + exception);
   //     }
  //  };


  //  public void confirmUser(String userId, String code)
  //  {
  //      CognitoUser cognitoUser = userPool.getUser(userId);
  //      cognitoUser.confirmSignUpInBackground(code,false, confirmationCallback);
  //  }

    //Callback handler for confirmSignUp API
 //   GenericHandler confirmationCallback = new GenericHandler() {
 //       @Override
 //       public void onSuccess(){
            //User successfully confirmed
  //          Toast.makeText(context, "User Confirmed", Toast.LENGTH_SHORT).show();
 //       }

  //      @Override
 //       public void onFailure(Exception exception){
            //User confirmation failed, check exception for cause
  //          Log.i(TAG, "Sign up failure: " + exception.getLocalizedMessage());
 //       }
   // };

  //  public void addAttribute(String key, String value){
 //       userAttributes.addAttribute(key, value);
  //  }

  //  public void userLogin(String userId, String password){
  //      CognitoUser cognitoUser = userPool.getUser(userId);
  //      this.userPassword = password;
   //     cognitoUser.getSessionInBackground(authenticationHandler);
  //  }

    //Callback handler for sign-in process
 //   AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
 //       @Override
  //      public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
  //          Toast.makeText(context,"Sign in success", Toast.LENGTH_SHORT).show();
  //      }

  //      @Override
 //       public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
  //          // The API needs user sign-in credentials to continue
  //          AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);
            // Pass the user sign-in credentials to the continuation
  //          authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            // Allow the sign-in to continue
  //          authenticationContinuation.continueTask();
  //      }

   //     @Override
  //      public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
  //      }

   //     @Override
   //     public void authenticationChallenge(ChallengeContinuation continuation) {

    //    }

  //      @Override
  //      public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
   //         Toast.makeText(context,"Sign in Failure", Toast.LENGTH_SHORT).show();
  //      }
  //  };

  //  public String getUserPoolId(){
 //       return userPoolId;
  //  }

 //   public String getClientId(){
  //      return clientId;
 //   }

 //   public String getClientSecret (){
 //       return clientSecret;
 //   }

 //   public Regions getCognitoRegion () {
  //      return cognitoRegion;
 //   }

  //  public CognitoUserPool getUserPool() {
  //      return userPool;
  //  }
}
