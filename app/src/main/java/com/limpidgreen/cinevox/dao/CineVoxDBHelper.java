/**
 * CineVoxDBHelper.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Events Database Helper.
 *
 * @author MajaDobnik
 *
 */
public class CineVoxDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cinevox.db";
    private static final int DATABASE_VERSION = 1;

    // User DB Table constants
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COL_ID = "_id";
    public static final String USER_COL_USERNAME = "username";
    public static final String USER_COL_NAME = "name";
    public static final String USER_COL_TRAKT_USERNAME = "trakt_username";
    public static final String USER_COL_TRAKT_PASSWORD = "trakt_password";

    // Event DB Table constants
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String EVENTS_COL_ID = "_id";
    public static final String EVENTS_COL_NAME = "name";
    public static final String EVENTS_COL_DESCRIPTION = "description";
    public static final String EVENTS_COL_DATE = "event_date";
    public static final String EVENTS_COL_TIME = "event_time";
    public static final String EVENTS_COL_TIME_LIMIT = "time_limit";
    public static final String EVENTS_COL_PLACE = "place";
    public static final String EVENTS_COL_MINIMUM_VOTING_PERCENT = "minimum_voting_percent";
    public static final String EVENTS_COL_USER_ID = "user_id";
    public static final String EVENTS_COL_FINISHED = "finished";
    public static final String EVENTS_COL_USERS_CAN_ADD_MOVIES = "users_can_add_movies";
    public static final String EVENTS_COL_NUM_ADD_MOVIES_BY_USER = "num_add_movies_by_user";
    public static final String EVENTS_COL_RATING_SYSTEM = "rating_system";
    public static final String EVENTS_COL_NUM_VOTES_PER_USER = "num_votes_per_user";
    public static final String EVENTS_COL_VOTING_RANGE = "voting_range";
    public static final String EVENTS_COL_TIE_KNOCKOUT = "tie_knockout";
    public static final String EVENTS_COL_KNOCKOUT_ROUNDS = "knockout_rounds";
    public static final String EVENTS_COL_KNOCKOUT_TIME_LIMIT = "knockout_time_limit";
    public static final String EVENTS_COL_WAIT_TIME_LIMIT = "wait_time_limit";
    public static final String EVENTS_COL_UPDATED_AT = "updated_at";
    public static final String EVENTS_COL_CRATED_AT = "created_at";
    public static final String EVENTS_COL_RATING_PHASE = "rating_phase";
    public static final String EVENTS_COL_KNOCKOUT_PHASE = "knockout_phase";
    public static final String EVENTS_COL_EVENT_STATUS = "event_status";

    // Friend DB Table constants
    public static final String FRIENDS_TABLE_NAME = "friends";
    public static final String FRIENDS_COL_ID = "_id";
    public static final String FRIENDS_COL_NAME = "name";
    public static final String FRIENDS_COL_USERNAME = "username";
    public static final String FRIENDS_COL_FACEBOOK_UID = "facebook_uid";
    public static final String FRIENDS_COL_CONFIRMED = "confirmed";
    public static final String FRIENDS_COL_REQUEST = "request";

    // Event-Friends DB Table constants
    public static final String EVENT_FRIENDS_TABLE_NAME = "event_friends";
    public static final String EVENT_FRIENDS_COL_ID = "_id";
    public static final String EVENT_FRIENDS_COL_EVENT_ID = "event_id";
    public static final String EVENT_FRIENDS_COL_FRIEND_ID = "friend_id";
    public static final String EVENT_FRIENDS_COL_ACCEPT = "accept";

    // Movie DB Table constants
    public static final String MOVIES_TABLE_NAME = "movies";
    public static final String MOVIES_COL_ID = "_id";
    public static final String MOVIES_COL_TITLE = "title";
    public static final String MOVIES_COL_POSTER = "poster";
    public static final String MOVIES_COL_YEAR = "year";

    // Event-Movies DB Table constants
    public static final String EVENT_MOVIES_TABLE_NAME = "event_movies";
    public static final String EVENT_MOVIES_COL_ID = "_id";
    public static final String EVENT_MOVIES_COL_EVENT_ID = "event_id";
    public static final String EVENT_MOVIES_COL_MOVIE_ID = "movie_id";
    public static final String EVENT_MOVIES_COL_WINNER = "winner";

    // Event-Knockout DB Table constants
    public static final String EVENT_KNOCKOUT_TABLE_NAME = "event_knockouts";
    public static final String EVENT_KNOCKOUT_COL_ID = "_id";
    public static final String EVENT_KNOCKOUT_COL_EVENT_ID = "event_id";
    public static final String EVENT_KNOCKOUT_COL_MOVIE_ID_1 = "movie_id_1";
    public static final String EVENT_KNOCKOUT_COL_MOVIE_ID_2 = "movie_id_2";
    public static final String EVENT_KNOCKOUT_COL_ROUND = "round";

    // User Database creation sql statement
    public static final String DATABASE_CREATE_USER = "create table "
            + USER_TABLE_NAME + "(" +
            USER_COL_ID + " integer primary key, " +
            USER_COL_USERNAME + " text not null, " +
            USER_COL_NAME + " text not null, " +
            USER_COL_TRAKT_USERNAME + " text, " +
            USER_COL_TRAKT_PASSWORD + " text, " +
            "UNIQUE (" + USER_COL_ID + ") ON CONFLICT REPLACE);";

    // Event Database creation sql statement
    public static final String DATABASE_CREATE_EVENTS = "create table "
            + EVENTS_TABLE_NAME + "(" +
            EVENTS_COL_ID + " integer primary key, " +
            EVENTS_COL_NAME + " text not null, " +
            EVENTS_COL_DESCRIPTION + " text not null, " +
            EVENTS_COL_DATE + " date not null, " +
            EVENTS_COL_TIME + " time not null, " +
            EVENTS_COL_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_PLACE + " text not null, " +
            EVENTS_COL_MINIMUM_VOTING_PERCENT + " integer not null, " +
            EVENTS_COL_USER_ID + " integer not null, " +
            EVENTS_COL_FINISHED + " integer not null, " +
            EVENTS_COL_USERS_CAN_ADD_MOVIES + " integer not null, " +
            EVENTS_COL_NUM_ADD_MOVIES_BY_USER + " integer not null, " +
            EVENTS_COL_RATING_SYSTEM + " integer not null, " +
            EVENTS_COL_NUM_VOTES_PER_USER + " integer not null, " +
            EVENTS_COL_VOTING_RANGE + " integer not null, " +
            EVENTS_COL_TIE_KNOCKOUT + " integer not null, " +
            EVENTS_COL_KNOCKOUT_ROUNDS + " integer not null, " +
            EVENTS_COL_KNOCKOUT_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_WAIT_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_UPDATED_AT + " datetime not null, " +
            EVENTS_COL_CRATED_AT + " datetime not null, " +
            EVENTS_COL_RATING_PHASE + " integer not null, " +
            EVENTS_COL_KNOCKOUT_PHASE + " integer not null," +
            EVENTS_COL_EVENT_STATUS + " integer not null, " +
            "UNIQUE (" + EVENTS_COL_ID + ") ON CONFLICT REPLACE);";

    // Friend Database creation sql statement
    public static final String DATABASE_CREATE_FRIENDS = "create table "
            + FRIENDS_TABLE_NAME + "(" +
            FRIENDS_COL_ID + " integer primary key, " +
            FRIENDS_COL_NAME + " text not null, " +
            FRIENDS_COL_USERNAME + " text not null, " +
            FRIENDS_COL_FACEBOOK_UID + " text not null," +
            FRIENDS_COL_CONFIRMED + " integer DEFAULT 0," +
            FRIENDS_COL_REQUEST + " integer DEFAULT 0," +
            "UNIQUE (" + FRIENDS_COL_ID + ") ON CONFLICT REPLACE);";

    public static final String DATABASE_CREATE_EVENT_FRIENDS = "create table "
            + EVENT_FRIENDS_TABLE_NAME + "(" +
            EVENT_FRIENDS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_FRIENDS_COL_EVENT_ID + " integer not null REFERENCES " + EVENTS_TABLE_NAME + "(" + EVENTS_COL_ID + ")," +
            EVENT_FRIENDS_COL_FRIEND_ID + " integer not null REFERENCES " + FRIENDS_TABLE_NAME + "(" + FRIENDS_COL_ID + ")," +
            EVENT_FRIENDS_COL_ACCEPT + " integer not null," +
            "UNIQUE (" + EVENT_FRIENDS_COL_EVENT_ID + ","
            + EVENT_FRIENDS_COL_FRIEND_ID + ") ON CONFLICT REPLACE);";

    // Movie Database creation sql statement
    public static final String DATABASE_CREATE_MOVIES = "create table "
            + MOVIES_TABLE_NAME + "(" +
            MOVIES_COL_ID + " integer primary key, " +
            MOVIES_COL_TITLE + " text not null, " +
            MOVIES_COL_POSTER + " text not null, " +
            MOVIES_COL_YEAR + " text not null," +
            "UNIQUE (" + MOVIES_COL_ID + ") ON CONFLICT REPLACE);";

    public static final String DATABASE_CREATE_EVENT_MOVIES = "create table "
            + EVENT_MOVIES_TABLE_NAME + "(" +
            EVENT_MOVIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_MOVIES_COL_EVENT_ID + " integer not null REFERENCES " + EVENTS_TABLE_NAME + "(" + EVENTS_COL_ID + ")," +
            EVENT_MOVIES_COL_MOVIE_ID + " integer not null REFERENCES " + MOVIES_TABLE_NAME + "(" + MOVIES_COL_ID + ")," +
            EVENT_MOVIES_COL_WINNER + " integer," +
            "UNIQUE (" + EVENT_MOVIES_COL_EVENT_ID + ","
            + EVENT_MOVIES_COL_MOVIE_ID + ") ON CONFLICT REPLACE);";

    public static final String DATABASE_CREATE_EVENT_KNOCKOUTS = "create table "
            + EVENT_KNOCKOUT_TABLE_NAME + "(" +
            EVENT_KNOCKOUT_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_KNOCKOUT_COL_EVENT_ID + " integer not null REFERENCES " + EVENTS_TABLE_NAME + "(" + EVENTS_COL_ID + ")," +
            EVENT_KNOCKOUT_COL_MOVIE_ID_1 + " integer not null REFERENCES " + MOVIES_TABLE_NAME + "(" + MOVIES_COL_ID + ")," +
            EVENT_KNOCKOUT_COL_MOVIE_ID_2 + " integer not null REFERENCES " + MOVIES_TABLE_NAME + "(" + MOVIES_COL_ID + ")," +
            EVENT_KNOCKOUT_COL_ROUND + " integer not null," +
            "UNIQUE (" + EVENT_KNOCKOUT_COL_EVENT_ID + ","
            + EVENT_KNOCKOUT_COL_MOVIE_ID_1 + ","
            + EVENT_KNOCKOUT_COL_MOVIE_ID_2 + ") ON CONFLICT REPLACE);";

    public CineVoxDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(DATABASE_CREATE_USER);
        database.execSQL(DATABASE_CREATE_EVENTS);
        database.execSQL(DATABASE_CREATE_FRIENDS);
        database.execSQL(DATABASE_CREATE_EVENT_FRIENDS);
        database.execSQL(DATABASE_CREATE_MOVIES);
        database.execSQL(DATABASE_CREATE_EVENT_MOVIES);
        database.execSQL(DATABASE_CREATE_EVENT_KNOCKOUTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CineVoxDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_FRIENDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_MOVIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_KNOCKOUT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FRIENDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MOVIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

}
