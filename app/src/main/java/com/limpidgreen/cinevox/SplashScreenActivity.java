package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.limpidgreen.cinevox.util.Constants;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {
    /** Application */
   // private CineVoxApplication mApplication;
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private CallbackManager callbackManager;
    private AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        // Show Hash Key
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo("com.limpidgreen.cinevox", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("CineVoxy", something);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        mAccountManager = AccountManager.get(this);
        Account[] userAccounts = mAccountManager
                .getAccountsByType(Constants.ACCOUNT_TYPE);
        Log.i(Constants.TAG, "Saccount length" + userAccounts.length);

        // Check how many accounts we have for the app
        if (userAccounts.length == 1) {
            mAccount = userAccounts[0];
            // check FB access key
            AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();

            if (fbAccessToken.getToken() != null) {
                Log.i(Constants.TAG, "SESSION OPEN");
                startAuthTokenFetch();
            } else {
                // FB not Valid, delete account, go to login activity
                for (Account userAccount : userAccounts) {
                    mAccountManager.removeAccount(userAccount, this, null, null);
                } // end for
                mAccount = null;
                mAccountManager.addAccount(
                        Constants.ACCOUNT_TYPE,
                        Constants.API_TOKEN_TYPE,
                        null,
                        new Bundle(),
                        this,
                        new OnAccountAddComplete(),
                        null);
            } // end if-else
        } else {
            // too many account, remove all and go to login activity
            if (userAccounts.length > 1) {
                for (Account userAccount : userAccounts) {
                    mAccountManager.removeAccount(userAccount, this, null, null);
                } // end for
            } // end if
            mAccount = null;
            mAccountManager.addAccount(
                    Constants.ACCOUNT_TYPE,
                    Constants.API_TOKEN_TYPE,
                    null,
                    new Bundle(),
                    this,
                    new OnAccountAddComplete(),
                    null);
        } // end if-else
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onFetchAuthTokenResult() {
      //  mApplication.setAPIToken(mAuthToken);
       // mApplication.setmAccount(mAccount);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //ContentResolver.requestSync(mAccount, EventsContentProvider.AUTHORITY, settingsBundle);
        //ContentResolver.requestSync(mAccount, FriendsContentProvider.AUTHORITY, settingsBundle);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAuthTokenFetch() {
        Bundle options = new Bundle();
        mAccountManager.getAuthToken(
                mAccount,
                Constants.API_TOKEN_TYPE,
                options,
                this,
                new OnAccountManagerComplete(),
                new Handler(new OnError())
        );
    }

    private class OnAccountManagerComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(Constants.TAG, "Received authentication token " + mAuthToken);
            onFetchAuthTokenResult();
        }
    }

    private class OnAccountAddComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mAccount = new Account(
                    bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                    bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
            Log.d(Constants.TAG, "Added account " + mAccount.name + ", fetching");
            startAuthTokenFetch();
        }
    }

    public class OnError implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            Log.e("onError","ERROR");
            return false;
        }
    }
}
