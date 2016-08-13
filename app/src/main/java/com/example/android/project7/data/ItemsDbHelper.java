package com.example.android.project7.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ItemsDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "items.db";

    public ItemsDbHelper(Context context)
    {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CARDS_TABLE = "CREATE TABLE " + ItemsContract.CardsEntry.TABLE_NAME + " (" +
                ItemsContract.CardsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemsContract.CardsEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL + " TEXT, " +
                ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION + " TEXT, " +
                ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO + " TEXT, " +
                ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LOCATION + " TEXT " +
                ");";

        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemsContract.ItemsEntry.TABLE_NAME + " (" +
                ItemsContract.ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemsContract.ItemsEntry.COLUMN_NAME + " TEXT, " +
                ItemsContract.ItemsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID + " INTEGER , " +
                ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1 + " TEXT " +
                ");";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        db.execSQL(SQL_CREATE_CARDS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.ItemsEntry.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.CardsEntry.TABLE_NAME);
        //onCreate(db);

        switch(oldVersion){
            case 1:
            case 2:
                break;
            default:
        }
    }
}
