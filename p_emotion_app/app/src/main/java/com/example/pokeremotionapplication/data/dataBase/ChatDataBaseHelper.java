package com.example.pokeremotionapplication.data.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ChatDataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pk.db";                  // 数据库名字
    public static final int DATABASE_VERSION = 1;                        // 数据库版本
    public static final String TABLE_CHAT = "chat";                      // 表名
    public static final String COLUMN_ID = "id";                         // 会话顺序
    public static final String COLUMN_USER_NAME = "user_name";           // 用户名字
    public static final String COLUMN_USER_NUMBER = "user_number";       // 用户账号
    public static final String COLUMN_MESSAGE = "message";               // 会话具体消息
    public static final String COLUMN_TIMESTAMP = "timestamp";           // 会话时间
    public static final String CREATE_CHAT_TABLE =
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
        db.execSQL(CREATE_CHAT_TABLE);
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
