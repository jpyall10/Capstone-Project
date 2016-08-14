package com.example.android.project7.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by jonathanporter on 1/31/16.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return remote View Factor here
        return new WidgetDataProvider(this, intent);
    }
}
