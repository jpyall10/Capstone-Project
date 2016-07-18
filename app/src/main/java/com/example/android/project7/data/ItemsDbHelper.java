package com.example.android.project7.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jonathanporter on 9/3/15.
 */
public class ItemsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ItemsDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 7;

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
                ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO + " TEXT " +
//                ItemsContract.CardsEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL, " +
//                ItemsContract.CardsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
//                " FOREIGN KEY (" + ItemsContract.CardsEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
//                ItemsContract.ItemsEntry.TABLE_NAME + " (" + ItemsContract.ItemsEntry._ID + ") " +
//                ItemsContract.CardsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ");";

        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemsContract.ItemsEntry.TABLE_NAME + " (" +
                ItemsContract.ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemsContract.ItemsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemsContract.ItemsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                //ItemsContract.ItemsEntry.COLUMN_DETAIL_1 + " TEXT, " +
                //ItemsContract.ItemsEntry.COLUMN_FAVORITES + " TEXT " +
//                ItemsContract.ItemsEntry.COLUMN_CARD_KEY + " INTEGER NOT NULL, " +
                ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID + " INTEGER , " +
                ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1 + " TEXT " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_1 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_PHOTO_1 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_2 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_2 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_PHOTO_2 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_3 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_3 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_PHOTO_3 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_4 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_4 + " TEXT, " +
//                ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_PHOTO_4 + " TEXT " +
                ");";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        db.execSQL(SQL_CREATE_CARDS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.ItemsEntry.TABLE_NAME);
        onCreate(db);
    }
}
