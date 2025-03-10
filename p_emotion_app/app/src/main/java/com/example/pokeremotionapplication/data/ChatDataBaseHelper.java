package com.example.pokeremotionapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ChatDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CHAT = "chat_message";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_NUMBER = "user_number";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CHAT + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT, " +
                    COLUMN_USER_NUMBER + " TEXT, " +
                    COLUMN_MESSAGE + " TEXT, " +
                    COLUMN_TIMESTAMP + " TEXT" +
                    ");";

    public ChatDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // 只执行一次
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }

    public void insertMessage(String userName , String userNumber , String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME , userName);
        values.put(COLUMN_USER_NUMBER , userNumber);
        values.put(COLUMN_MESSAGE, message);
        db.insert(TABLE_CHAT , null , values);
        db.close();
    }

    public Cursor getAllMessage(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_CHAT, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");
    }
}
