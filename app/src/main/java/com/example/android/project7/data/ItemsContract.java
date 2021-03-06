package com.example.android.project7.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemsContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.project7";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "items";
    public static final String PATH_CARDS = "cards";

    public static final class ItemsEntry implements BaseColumns {

        public static final String TABLE_NAME = "items";

        public static final String COLUMN_NAME = "item_name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PHOTO_RES_ID = "photo_res_id";
        public static final String COLUMN_PHOTO_EXTRA_1 = "photo_extra_1";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS + "/#";



        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri contentUri){
            return ContentUris.parseId(contentUri);
        }


    }

    public static final class CardsEntry implements BaseColumns {

        public static final String TABLE_NAME = "cards";

        public static final String COLUMN_ITEM_KEY = "item_key";

        public static final String COLUMN_EXTRA_CARD_LABEL = "card_label";
        public static final String COLUMN_EXTRA_CARD_DESCRIPTION = "card_description";
        public static final String COLUMN_EXTRA_CARD_PHOTO = "card_photo";
        public static final String COLUMN_EXTRA_CARD_LOCATION = "card_location";

        public static final String CONTENT_TYPE =
                ItemsEntry.CONTENT_TYPE + "/" + PATH_CARDS;
        public static final String CONTENT_ITEM_TYPE =
                ItemsEntry.CONTENT_ITEM_TYPE + "/" + PATH_CARDS + "/#";



        public static Uri buildCardsByItemUri(Uri itemUri) {
            return itemUri.buildUpon().appendPath(PATH_CARDS).build();
        }

        public static Uri buildCardUri(Uri itemUri, long cardId){
            Uri returnUri = ContentUris.withAppendedId(buildCardsByItemUri(itemUri),cardId);
            return returnUri;
        }

    }
}