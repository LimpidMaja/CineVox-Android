/**
 * FBLoginActivity.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;
import com.limpidgreen.cinevox.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Facebook Login Activity.
 *
 * @author MajaDobnik
 *
 */
public class FBLoginActivity extends AccountAuthenticatorActivity {
    //private GoogleCloudMessaging mGcmObject;
    private Context mApplicationContext;
    private AccountManager mAccountManager;
    private String mGcmRegId;
    //private String accessToken;
    private String userId;
    private String email;
    private Date expirationDate;

    private LinearLayout mEmailEditLayout;
    private Button mFBSigninButton;
    private Button mEmailSigninButton;
    private LoginButton loginButton;

    private CallbackManager callbackManager;
    private AccessToken accessToken;

    /** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;

    /** POST parameter name for the user's facebook id */
    public static final String PARAM_FACEBOOK_UID = "uid";
    /** POST parameter name for the user's email */
    public static final String PARAM_EMAIL = "email";
    /** POST parameter name for the user's facebook access token */
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    /** POST parameter name for the user's GCM Registration Id */
    public static final String PARAM_GCM_REG_ID = "gcm_reg_id";
    /** POST parameter name for the user's facebook access token expiration date */
    public static final String PARAM_ACCESS_TOKEN_EXPIRES = "expires_at";

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AccountAuthenticatorActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplicationContext = getApplicationContext();

        Log.i(Constants.TAG, "FB LOGIN ACTIVITY");

        setContentView(R.layout.activity_fb_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        mEmailEditLayout = (LinearLayout) findViewById(R.id.email_edit_layout);
        mEmailEditLayout.setVisibility(View.GONE);

        mEmailSigninButton = (Button) findViewById(R.id.email_edit_button);
        mEmailSigninButton.setVisibility(View.GONE);

        mAccountManager = AccountManager.get(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.i(Constants.TAG, "ACCESS TOKEN: " + accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                if (object != null) {

                                    Log.i(Constants.TAG, "USER " + object);
                                    TextView welcome = (TextView) findViewById(R.id.welcome);
                                    try {
                                        welcome.setText("Hello " + object.getString("first_name") + "!");

                                        userId = object.getString("id");
                                        email = object.getString("email");
                                        Log.i(Constants.TAG, "USER ID: " + userId);


                                        if (email != null && !email.isEmpty()) {
                                            showProgress();
                                            if (Utility.checkPlayServices(FBLoginActivity.this, mApplicationContext)) {

                                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                                Log.i(Constants.TAG, "refreshedToken: " + refreshedToken);
                                                if (refreshedToken != null) {

                                                    authenticateOnServer(userId, accessToken.getToken(), refreshedToken, accessToken.getExpires(), email);

                                                } else {
                                                    hideProgress();
                                                    Toast toast = Toast.makeText(FBLoginActivity.this,
                                                            getText(R.string.register_gcm_error),
                                                            Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            } else {
                                                hideProgress();
                                                Toast.makeText(
                                                        FBLoginActivity.this,
                                                        "This device doesn't support Play services, App will not work normally",
                                                        Toast.LENGTH_LONG).show();
                                            } // end if-else

                                        } else {
                                            hideProgress();

                                            mEmailEditLayout = (LinearLayout) findViewById(R.id.email_edit_layout);
                                            mEmailEditLayout.setVisibility(View.VISIBLE);

                                            mEmailSigninButton = (Button) findViewById(R.id.email_edit_button);
                                            mEmailSigninButton.setVisibility(View.VISIBLE);

                                            mFBSigninButton = (Button) findViewById(R.id.login_button);
                                            mFBSigninButton.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        /*accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();*/

    } // end onCreate()

    @Override
    public void onDestroy() {
        super.onDestroy();
        // accessTokenTracker.stopTracking();
    }

    /**
     * Handle Email Submit button click.
     *
     * @param v view
     */
    public void handleEmailSubmit(View v) {
        EditText emailEdit = (EditText) findViewById(R.id.email_edit);

        Log.i(Constants.TAG, "EMAIL text: " + emailEdit );
        Log.i(Constants.TAG, "EMAIL 2: " + emailEdit.getText().toString() );

        if (emailEdit != null && !emailEdit.getText().toString().trim().isEmpty()
                && com.limpidgreen.cinevox.util.Utility.validate(emailEdit.getText().toString().trim())) {
            email = emailEdit.getText().toString();
            Log.i(Constants.TAG, "EMAIL: " + email);

            showProgress();
            if (Utility.checkPlayServices(FBLoginActivity.this, mApplicationContext)) {

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Log.i(Constants.TAG, "refreshedToken: " + refreshedToken);
                if (refreshedToken != null) {

                    authenticateOnServer(userId, accessToken.getToken(), refreshedToken, accessToken.getExpires(), email);

                } else {
                    hideProgress();
                    Toast toast = Toast.makeText(FBLoginActivity.this,
                            getText(R.string.register_gcm_error),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                hideProgress();
                Toast.makeText(
                        FBLoginActivity.this,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
            } // end if-else
        } else {
            Toast toast = Toast.makeText(this,
                    getText(R.string.email_empty),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress() {
        mProgressDialog = ProgressDialog.show(this, null,
                getText(R.string.ui_activity_authenticating), true, true,
                null);
    } // end showProgress()

    /**
     * Hides the progress UI for a lengthy operation.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        } // end if
    } // end hideProgress()

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param userId
     *            the authentication token returned by the server, or NULL if
     *            authentication failed.
     */
    public void authenticateOnServer(String userId, String accessToken, String gcmRegId,
                                       Date expirationDate, final String email) {
        JSONObject params = new JSONObject();
        try {
            params.put(PARAM_FACEBOOK_UID, userId);
            params.put(PARAM_ACCESS_TOKEN, accessToken);
            params.put(PARAM_GCM_REG_ID, gcmRegId);
            params.put(PARAM_ACCESS_TOKEN_EXPIRES, String.valueOf(expirationDate.getTime()));
            params.put(PARAM_EMAIL, email);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, NetworkUtil.AUTH_URI, params, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgress();
                            String authToken = null;
                            try {
                                authToken = response.getString(PARAM_ACCESS_TOKEN);
                                if (authToken != null && authToken.length() > 0) {
                                    Log.i(Constants.TAG, "API KEY: " + authToken);
                                    final Account account = new Account(email, Constants.ACCOUNT_TYPE);

                                    Account[] userAccounts = mAccountManager
                                            .getAccountsByType(Constants.ACCOUNT_TYPE);
                                    if (userAccounts.length < 1) {
                                        Bundle data = new Bundle();
                                        Bundle userData = new Bundle();
                                        userData.putString(Constants.USER_DATA_GCM_REG_ID, mGcmRegId);
                                        data.putBundle(AccountManager.KEY_USERDATA, userData);

                                        mAccountManager.addAccountExplicitly(account, null, data);
                                        // Set events sync for this account.
                                        //ContentResolver.setSyncAutomatically(account,
                                        //        EventsContentProvider.AUTHORITY, true);
                                        // Set friends sync for this account.
                                        //ContentResolver.setSyncAutomatically(account,
                                        //        FriendsContentProvider.AUTHORITY, true);

                                    } else {
                                        mAccountManager.setPassword(account, null);
                                    } // end if-else
                                    mAccountManager.setAuthToken(account, Constants.API_TOKEN_TYPE, authToken);

                                    final Intent intent = new Intent();
                                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
                                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                                    intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

                                    Toast toast = Toast.makeText(FBLoginActivity.this,
                                            getText(R.string.activity_login_success), Toast.LENGTH_SHORT);
                                    toast.show();

                                    setAccountAuthenticatorResult(intent.getExtras());
                                    setResult(RESULT_OK, intent);
                                    finish();

                                } else {
                                    Toast toast = Toast.makeText(FBLoginActivity.this,
                                            getText(R.string.login_activity_error),
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                } // end if-else
                            } catch (JSONException e) {

                                Toast toast = Toast.makeText(FBLoginActivity.this,
                                        getText(R.string.login_activity_error),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            Toast toast = Toast.makeText(FBLoginActivity.this,
                                    getText(R.string.login_activity_error),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    //mStatusCode = response.statusCode;
                    return super.parseNetworkResponse(response);
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");

                    return params;
                }
            };
            NetworkUtil.getInstance(this).addToRequestQueue(jsObjRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            hideProgress();
            Toast toast = Toast.makeText(FBLoginActivity.this,
                    getText(R.string.login_activity_error),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
