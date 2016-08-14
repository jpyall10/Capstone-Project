package com.example.android.project7.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.project7.R;
import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = "WidgetDataProvider";

    private static final String[] ITEM_COLUMNS = {
            ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID,
            ItemsContract.ItemsEntry.COLUMN_NAME,
            ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1
    };

    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_PHOTO = 2;

    List<String> names = new ArrayList<>();
    List<String> photos = new ArrayList<>();
    List<Long> ids = new ArrayList<>();

    private Context mContext = null;
    private Intent mIntent = null;
    private Cursor c;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view;
        if(mContext.getString(R.string.no_names_text).equals(names.get(position))){
            view = new RemoteViews(mContext.getPackageName(),
                    R.layout.no_scores_list_item);
            view.setTextViewText(R.id.no_data_textview, names.get(position));
            view.setTextColor(R.id.no_data_textview, Color.BLACK);
        }
        else{
            view = new RemoteViews(mContext.getPackageName(),
                    R.layout.list_item);
            view.setImageViewUri(R.id.avatar, Uri.parse(photos.get(position)));
            view.setTextViewText(R.id.text1, names.get(position));
            view.setTextColor(R.id.text1, Color.WHITE);
        }
        return view;    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return ids.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void initData() {
        c = mContext.getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI, ITEM_COLUMNS, ItemsContract.ItemsEntry.COLUMN_CATEGORY + " = ?",
               new String[]{mContext.getString(R.string.category_people)} , ItemsContract.ItemsEntry.COLUMN_NAME + " ASC");
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            names.clear();

            String name, photo;
            long id;
            do {
                name = c.getString(INDEX_NAME);
                photo = c.getString(INDEX_PHOTO);
                id = c.getLong(INDEX_ID);
                names.add(name);
                photos.add(photo);
                ids.add(id);
            }while (c.moveToNext());
        }else{
            names.clear();
            names.add(mContext.getString(R.string.no_names_text));
        }
    }


//    @Override
//    public int getCount() {
//        return times.size();
//    }

//    @Override
//    public RemoteViews getViewAt(int position) {
//        RemoteViews view;
//        if(mContext.getString(R.string.no_scores_text).equals(times.get(position))){
//            view = new RemoteViews(mContext.getPackageName(),
//                    R.layout.no_scores_list_item);
//            view.setTextViewText(R.id.no_data_textview, times.get(position));
//            view.setTextColor(R.id.no_data_textview, Color.BLACK);
//            Log.d(LOG_TAG, "times.size() = " + times.size());
//        }
//        else{
//            view = new RemoteViews(mContext.getPackageName(),
//                    R.layout.scores_list_item);
//            view.setTextViewText(R.id.home_name, homeTeams.get(position));
//            view.setTextColor(R.id.home_name, Color.BLACK);
//
//            view.setTextViewText(R.id.away_name, awayTeams.get(position));
//            view.setTextColor(R.id.away_name, Color.BLACK);
//
//            view.setTextViewText(R.id.score_textview, scores.get(position));
//            view.setTextColor(R.id.score_textview, Color.RED);
//
//            view.setTextViewText(R.id.data_textview, times.get(position));
//            view.setTextColor(R.id.data_textview, Color.BLACK);
//
//
//        }
//        return view;
//
//
//
////        return view;
//}

//    @Override
//    public RemoteViews getLoadingView() {
//        return null;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }

//
//
}