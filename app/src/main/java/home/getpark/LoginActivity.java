package home.getpark;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginActivity extends ActionBarActivity {

    static final String activityName = MainActivity.class.getSimpleName();
    static final String TAG = "GetPark:" + activityName + "==>>";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static int appVersion = 1;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Firebase mFirebaseRef;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    TextView signUpText; // redirect to signUp page
    EditText password, email;
    Button login;
    LoginButton fbLoginButton; //FB button
    String SENDER_ID = "672402448478"; // project number
    String regid; //RegistrationId from GCM server
    CallbackManager callbackManager;
    AccessToken accessToken;
    private AccessTokenTracker mFacebookAccsesTokenTracker;
   // private ProgressDialog mAuthProgressDialog;
    private Firebase.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        // FIREBASE instance Authentication
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_USERS);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        // Callback registration
        //To respond to a login result, you need to register a callback
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //The LoginResult parameter has the new AccessToken, (and the most recently granted or declined permissions).
                accessToken = AccessToken.getCurrentAccessToken();
                Profile profile = Profile.getCurrentProfile();
                Log.d(TAG, "User token = " + accessToken + " User profile = " + profile);
                LoginActivity.this.onFacebookAccessTokenChange(accessToken);
               // SearchforParkActivity();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Callback registration cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        mFacebookAccsesTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(TAG, "+OnCurrentAccessTokenChanged()");
                //LoginActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };

//        mAuthProgressDialog = new ProgressDialog(this);
//        mAuthProgressDialog.setTitle("Loading");
//        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
//        mAuthProgressDialog.setCancelable(false);


//        mAuthStateListener = new Firebase.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(AuthData authData) {
//                mAuthProgressDialog.hide();
//                setAuthenticatedUser(authData);
//            }
//        };

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.i(TAG, "registration id = " + regid);
                setRegid(regid);
            }

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        password = (EditText) findViewById(R.id.passwordField);
        email = (EditText) findViewById(R.id.emailField);
        User user = new User("name", email.toString().trim(), password.toString().trim(), "address", "apartment", "parkingNum");

        login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    final String emailAddress = emailText;

                    mFirebaseRef.authWithPassword(emailText, passwordText, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // Authenticated successfully with payload authData
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("email", emailAddress);
                            map.put("regId", getRegid());
                            mFirebaseRef.child("users").child(authData.getUid()).setValue(map);//AuthData object contains unique information about the logged in user.
                            SearchforParkActivity();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // Authenticated failed with error firebaseError
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(firebaseError.getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        });
        signUpText = (TextView) findViewById(R.id.signUpText);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
            }
        });
        /*
        reset password:
        mFirebaseRef.resetPassword({
          email : "bobtony@firebase.com"
        }, function(error) {
          if (error === null) {
            console.log("Password reset email sent successfully");
          } else {
            console.log("Error sending password reset email:", error);
          }
        });
         */
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        Log.i(TAG, "+onFacebookAccessTokenChange() token = " + token);
        if (token != null) {
            //mAuthProgressDialog.show();
            mFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.i(TAG, "The Facebook user is now authenticated with your Firebase app");
                    //mAuthProgressDialog.hide();
                    mFirebaseRef.child("users").setValue(authData.getAuth());
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.i(TAG, "onAuthenticationError occurred");
                    //mAuthProgressDialog.hide();
                }
            });
        } else {
            Log.i(TAG, " Logged out of Facebook so do a logout from the Firebase app ");
            mFirebaseRef.unauth();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void SearchforParkActivity() {
        Intent intent = new Intent(this, SearchParkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        return registrationId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        Log.d(TAG, "+sendRegistrationIdToBackend()");

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", "Hello World");
                    data.putString("my_action",
                            "com.google.android.gcm.demo.app.ECHO_NOW");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Message from " + SENDER_ID + " sent to server";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.d(TAG, "sendRegistrationIdToBackend(), msg = " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                signUpText.append("\n" + msg + "\n");
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String registrationId) {
        registrationId = regid.toString().trim();
    }
}