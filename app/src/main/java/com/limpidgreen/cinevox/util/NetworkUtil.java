package com.limpidgreen.cinevox.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.limpidgreen.cinevox.exeption.APICallException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for API communication.
 *
 * @author MajaDobnik
 *
 * Created on 27/06/16.
 */
public class NetworkUtil {
    /** Request timeout */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** Base URL for the API Service */
    public static final String BASE_URL = "http://192.168.1.11:3000";
    //public static final String BASE_URL = "http://cinevox.herokuapp.com";

    /** URI for authentication service */
    public static final String AUTH_URI = BASE_URL + "/api/auth/facebook/callback";
    private static NetworkUtil mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

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
    /** POST parameter name for result's info data */
    public static final String PARAM_INFO = "info";
    /** GET parameter name for search term */
    public static final String PARAM_TERM = "term";
    /** POST parameter name for trakt username */
    public static final String PARAM_TRAKT_USERNAME = "trakt_username";
    /** POST parameter name for trakt password */
    public static final String PARAM_TRAKT_PASSWORD = "trakt_password";

    /** URI for events */
    public static final String EVENTS_URI = BASE_URL + "/api/events";
    /** URI for events confirm */
    public static final String EVENTS_CONFIRM_URI = "/confirm";
    /** URI for events cancel */
    public static final String EVENTS_CANCEL_URI = "/cancel";
    /** URI for events continue */
    public static final String EVENTS_CONTINUE_URI = "/continue";
    /** URI for events increase time limit */
    public static final String EVENTS_TIME_LIMIT_URI = "/time_limit";
    /** URI for events start */
    public static final String EVENTS_START_URI = "/start";
    /** URI for events movies vote */
    public static final String EVENTS_VOTE_URI = "/vote";
    /** URI for events movies knockout */
    public static final String EVENTS_KNOCKOUT_URI = "/knockout_vote";
    /** URI for friends */
    public static final String FRIENDS_URI = BASE_URL + "/api/friends";
    /** URI for friend request confirm */
    public static final String FRIENDS_CONFIRM_REQUEST_URI = "/confirm_friend_request";
    /** URI for friend request send */
    public static final String FRIENDS_SEND_REQUEST_URI = "/send_friend_request";
    /** URI for movies */
    public static final String MOVIES_URI = BASE_URL + "/api/movies";
    /** URI for movies search */
    public static final String MOVIES_SEARCH_URI = MOVIES_URI + "/autocomplete";
    /** URI for movie list search */
    public static final String MOVIES_SEARCH_LISTS_URI = MOVIES_URI + "/search_lists";
    /** URI for movies collection search */
    public static final String MOVIES_SEARCH_COLLECTION_URI = MOVIES_URI + "/collection";
    /** URI for trakt account save */
    public static final String TRAKT_URI = "/trakt";
    /** URI for trakt import */
    public static final String TRAKT_IMPORT_URI = MOVIES_URI + "/import_trakt";
    /** URI for friends search */
    public static final String FRIENDS_SEARCH_URI = FRIENDS_URI + "/autocomplete";


    private NetworkUtil(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkUtil(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Returns true if the Internet connection is available.
     *
     * @return true if the Internet connection is available
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } // end isNetworkAvailable()

    /**
     * Connects to server, authenticates the provided user data and returns an API access key.
     *
     * @param userId
     *            The facebook user id
     * @param accessToken
     *            The facebook access token
     * @param expirationDate
     *            The facebook access token expiration date
     * @return API Access key returned by the server (or null)
     */
    public static String authenticate(Context contexr, String userId, String accessToken, String gcmRegId,
                                      Date expirationDate, String email) {
        try {
            JSONObject params = new JSONObject();
            params.put(PARAM_FACEBOOK_UID, userId);
            params.put(PARAM_ACCESS_TOKEN, accessToken);
            params.put(PARAM_GCM_REG_ID, gcmRegId);
            params.put(PARAM_ACCESS_TOKEN_EXPIRES, String.valueOf(expirationDate.getTime()));
            params.put(PARAM_EMAIL, email);

            JSONObject obj = postWebService(contexr, params, AUTH_URI, null);

            String APIToken = obj.getString(PARAM_ACCESS_TOKEN);
            if (APIToken != null && APIToken.length() > 0) {
                Log.i(Constants.TAG, "API KEY: " + APIToken);
                return APIToken;
            } else {
                Log.e(Constants.TAG,
                        "Error authenticating" + obj.getString(PARAM_INFO));
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when getting API token - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when getting API token", e);
            return null;
        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSONException when getting API token", e);
            return null;
        } // end try-catch
    } // end authenticate()

    /**
     * Connects to API server, sends json post service.
     *
     * @param jsonObject
     * @param url
     * @return server result
     */
    public static JSONObject postWebService(Context context, JSONObject jsonObject,
                                            String url, final String accessToken)
            throws APICallException, IOException {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //throw new APICallException(String.valueOf(error.getMessage());
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
                if (accessToken != null) {
                    Log.i(Constants.TAG, "ADD TOKEN" + accessToken);
                    params.put("Authorization", "Token token=" + accessToken );
                } // end if
                return params;
            }
        };
        getInstance(context).addToRequestQueue(jsObjRequest);
        return null;
    } // end postWebService()
}
