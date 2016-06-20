package com.example.android.project7.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jonathanporter on 9/3/15.
 */
public class ItemsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ItemsDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 16;

    static final String DATABASE_NAME = "items.db";

    public ItemsDbHelper(Context context)
    {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemsContract.ItemsEntry.TABLE_NAME + " (" +
                ItemsContract.ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemsContract.ItemsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemsContract.ItemsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ItemsContract.ItemsEntry.COLUMN_DETAIL_1 + " TEXT, " +
                ItemsContract.ItemsEntry.COLUMN_FAVORITES + " TEXT " +
                ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID + " INTEGER " +
                ");";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.ItemsEntry.TABLE_NAME);
        onCreate(db);
    }
}
