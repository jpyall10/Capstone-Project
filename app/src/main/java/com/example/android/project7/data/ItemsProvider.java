package com.example.android.project7.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by jonathanporter on 9/15/15.
 */
public class ItemsProvider extends ContentProvider {

    private static final String LOG_TAG = ItemsProvider.class.getSimpleName();
    private ItemsDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int ITEMS = 100;
    static final int ITEM_WITH_ID = 101;
    static final int CARDS = 102;
    static final int CARD_WITH_ID = 103;

    private static final SQLiteQueryBuilder sItemByIdQueryBuilder;

    static{
        sItemByIdQueryBuilder = new SQLiteQueryBuilder();
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.


        // 3) Return the new matcher!
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ItemsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ItemsContract.PATH_ITEMS, ITEMS);
        matcher.addURI(authority, ItemsContract.PATH_ITEMS + "/#", ITEM_WITH_ID);
        matcher.addURI(authority, ItemsContract.PATH_ITEMS + "/#/" + ItemsContract.PATH_CARDS, CARDS);
        matcher.addURI(authority, ItemsContract.PATH_ITEMS + "/#/" + ItemsContract.PATH_CARDS + "/#", CARD_WITH_ID);

        //matcher.addURI(authority, ItemsContract.PATH_ITEMS + "/*", ITEMS_WITH_ID);

        //matcher.addURI(authority, matcher.PATH_LOCATION, LOCATION);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new ItemsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case ITEMS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ItemsContract.ItemsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
                //break;
            }
            case ITEM_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ItemsContract.ItemsEntry.TABLE_NAME,
                        projection,
                        ItemsContract.ItemsEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
                //break;
            }
            case CARDS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ItemsContract.CardsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
//                        ItemsContract.CardsEntry.COLUMN_ITEM_KEY + " = ?",
//                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            //Student: Uncomment and fill out these two cases
            case ITEMS:
                return ItemsContract.ItemsEntry.CONTENT_TYPE;
            case ITEM_WITH_ID:
                return ItemsContract.ItemsEntry.CONTENT_ITEM_TYPE;
            case CARDS:
                return ItemsContract.CardsEntry.CONTENT_TYPE;
            case CARD_WITH_ID:
                return ItemsContract.CardsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ITEMS: {
                long _id = db.insert(ItemsContract.ItemsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ItemsContract.ItemsEntry.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CARDS:
                long _id = db.insert(ItemsContract.CardsEntry.TABLE_NAME, null, values);
                if ( _id >0)
                    returnUri = ItemsContract.CardsEntry.buildCardUri(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //db.close();
        Log.d(LOG_TAG, "This ran");
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        long id = ContentUris.parseId(uri);

        if (null == selection)
        {
            selection = "1";
        }
        switch(match)
        {
            case ITEMS:
                rowsDeleted = db.delete(ItemsContract.ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_WITH_ID:
                selection = ItemsContract.ItemsEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(id)};
                rowsDeleted = db.delete(ItemsContract.ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CARDS:
                rowsDeleted = db.delete(ItemsContract.CardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CARD_WITH_ID:
                selection = ItemsContract.CardsEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(id)};
                rowsDeleted = db.delete(ItemsContract.CardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection)
        {
            selection = "1";
        }
        switch(match)
        {
            case ITEMS:
                rowsUpdated = db.update(ItemsContract.ItemsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_WITH_ID:
                Log.d(LOG_TAG, "item_with_id ran in update");
                rowsUpdated = db.update(ItemsContract.ItemsEntry.TABLE_NAME, values,
                        //selection, selectionArgs);
                        ItemsContract.ItemsEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case CARDS:
                rowsUpdated = db.update(ItemsContract.CardsEntry.TABLE_NAME, values,selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ItemsContract.ItemsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
