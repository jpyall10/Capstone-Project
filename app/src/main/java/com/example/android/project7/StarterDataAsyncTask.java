package com.example.android.project7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;

/**
 * Created by Jon on 8/11/2016.
 */
public class StarterDataAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = StarterDataAsyncTask.class.getSimpleName();
    private Context mContext;

    public StarterDataAsyncTask(Context context){
        mContext = context;
    }

    private ArrayList<Item> generateStarterItems(){
        ArrayList<Item> starterItems = new ArrayList<Item>(){};

        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_cat),mContext.getString(R.string.android_resource_uri_base) + R.drawable.cat_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_cow), mContext.getString(R.string.android_resource_uri_base) + R.drawable.cow_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_dog), mContext.getString(R.string.android_resource_uri_base) + R.drawable.dog_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_owl), mContext.getString(R.string.android_resource_uri_base) + R.drawable.owl_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_elephant), mContext.getString(R.string.android_resource_uri_base) + R.drawable.elephant_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_lion), mContext.getString(R.string.android_resource_uri_base) + R.drawable.lion_1, mContext.getString(R.string.category_animals)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.animal_label_squirrel), mContext.getString(R.string.android_resource_uri_base) + R.drawable.squirrel_1, mContext.getString(R.string.category_animals)));

        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_mom), mContext.getString(R.string.android_resource_uri_base) + R.drawable.mom_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_dad), mContext.getString(R.string.android_resource_uri_base) + R.drawable.dad_1, mContext.getString(R.string.category_people)));

        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_grandpa_bones), mContext.getString(R.string.android_resource_uri_base) + R.drawable.grandpa_bones_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_grandma_amy), mContext.getString(R.string.android_resource_uri_base) + R.drawable.grandma_amy_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_grandpa_porter), mContext.getString(R.string.android_resource_uri_base) + R.drawable.grandpa_porter_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_grandma_carmen), mContext.getString(R.string.android_resource_uri_base) + R.drawable.grandma_carmen_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_uncle_tone), mContext.getString(R.string.android_resource_uri_base) + R.drawable.uncle_tone_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_uncle_vinnie), mContext.getString(R.string.android_resource_uri_base) + R.drawable.uncle_vinnie_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_aunt_mel), mContext.getString(R.string.android_resource_uri_base) + R.drawable.aunt_mel_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_cousin_rachel), mContext.getString(R.string.android_resource_uri_base) + R.drawable.cousin_rachel_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_cousin_porter), mContext.getString(R.string.android_resource_uri_base) + R.drawable.cousin_porter_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_cousin_sophia), mContext.getString(R.string.android_resource_uri_base) + R.drawable.cousin_sophia_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_aunt_pam), mContext.getString(R.string.android_resource_uri_base) + R.drawable.aunt_pam_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_aunt_debi), mContext.getString(R.string.android_resource_uri_base) + R.drawable.aunt_debi_1, mContext.getString(R.string.category_people)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.person_name_aunt_punkin), mContext.getString(R.string.android_resource_uri_base) + R.drawable.aunt_punkin_1, mContext.getString(R.string.category_people)));


        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_apple), mContext.getString(R.string.android_resource_uri_base) + R.drawable.apple_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_banana), mContext.getString(R.string.android_resource_uri_base) + R.drawable.banana_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_brocolli), mContext.getString(R.string.android_resource_uri_base) + R.drawable.broccoli_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_grapes), mContext.getString(R.string.android_resource_uri_base) + R.drawable.grapes_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_oranges), mContext.getString(R.string.android_resource_uri_base) + R.drawable.oranges_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_peas), mContext.getString(R.string.android_resource_uri_base) + R.drawable.peas_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_strawberries), mContext.getString(R.string.android_resource_uri_base) + R.drawable.strawberries_1, mContext.getString(R.string.category_food)));
        starterItems.add(new Item(mContext, mContext.getString(R.string.food_name_tomatoes), mContext.getString(R.string.android_resource_uri_base) + R.drawable.tomatoes_1, mContext.getString(R.string.category_food)));

        return starterItems;
    }

    private void insertStarterData() {
        ContentValues cv = new ContentValues();

        for (Item item : generateStarterItems()) {
            cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, item.getName().toLowerCase());
            cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, item.getCategory().toLowerCase());
            cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, item.getPhotoUriString());
            Uri itemUri = mContext.getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);

            Log.d(LOG_TAG, "Added item with name " + item.getName() + " uri = " + itemUri.toString());

        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        insertStarterData();
        return null;
    }
}
