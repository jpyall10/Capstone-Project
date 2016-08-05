package com.example.android.project7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ItemsGridFragment.Callback{

    private static DrawerLayout mDrawerLayout;
    private static ViewPager mViewPager;
    private static Adapter mAdapter;
    private static Bundle mArgs;
    private static TabLayout mTabLayout;
    private static TextToSpeech myTTS;
    private static List<String> mCategories;


    private int MY_DATA_CHECK_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null){
            setupDrawerContent(navigationView);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if(mViewPager != null){
            setupViewPager();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put code here to add a member
            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public static void openDrawerLayout(){
//        mDrawerLayout.openDrawer(Gravity.LEFT);
        Log.d("MA", "openDrawer ran");
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void setupViewPager(){
        mArgs = new Bundle();
        mAdapter = new Adapter(getSupportFragmentManager());
        Cursor c = getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI,null,null,null,null);
        Log.d("TAG", "the size of the cursor is " + c.getCount());
        mCategories = new ArrayList<>();
        if (c.getCount()<=0){
            String animals = getString(R.string.category_animals);
            String food = getString(R.string.category_food);
            String people = getString(R.string.category_people);
            mCategories.add(animals);
            mCategories.add(food);
            mCategories.add(people);

//            mCategories.add(animals.substring(0,1).toUpperCase() + animals.substring(1,animals.length()));
//            mCategories.add(food.substring(0,1).toUpperCase() + food.substring(1,food.length()));
//            mCategories.add(people.substring(0,1).toUpperCase() + people.substring(1,people.length()));
        }else{
            mCategories = getCategories(c);
        }

        for (String category : mCategories){
            //category = category.substring(0,1).toUpperCase() + category.substring(1);
            addCategory(ItemsGridFragment.newInstance(category.toLowerCase()), category);
        }
        mViewPager.setAdapter(mAdapter);
        c.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        Log.d("MA", "item ID = " + item.getItemId() + " string is " + item.toString());
        switch (id){
            case android.R.id.home:
                openDrawerLayout();
                break;
            default:
        }
        return false;
    }
    private static void addCategory(Fragment fragment, String category){
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        Bundle args = new Bundle();
        mArgs.putString("category", category);
        mAdapter.addFragment(fragment, category);
        mViewPager.setAdapter(mAdapter);
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.add_category:
                                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                                b.setTitle("Please enter a category");
                                final EditText input = new EditText(MainActivity.this);
                                b.setView(input);
                                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // SHOULD NOW WORK
                                        String category = input.getText().toString();
//                                        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                                        if (mViewPager != null) {

                                            addCategory(ItemsGridFragment.newInstance(category), category);
                                            mArgs.putString("category", category);
                                            mViewPager.setAdapter(mAdapter);
                                            mTabLayout.setupWithViewPager(mViewPager);
                                        }
                                    }
                                });
                                b.setNegativeButton("CANCEL", null);
                                b.create().show();
                                break;
                            default:
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public static void addTab(String category) {
        if (mViewPager != null) {
            category = category.substring(0,1).toUpperCase() + category.substring(1);
            if(!mCategories.contains(category)) {
                addCategory(ItemsGridFragment.newInstance(category), category);
                mCategories.add(category);
                mArgs.putString("category", category);
                mAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                int itemIndex = mTabLayout.getTabCount() - 1;
                mViewPager.setCurrentItem(itemIndex);
            }
        }else{
            return;
        }

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm){super(fm);}

        public void addFragment(Fragment fragment, String category){
            mFragments.add(fragment);
            mFragmentTitles.add(category.substring(0,1).toUpperCase() + category.substring(1));
        }

        @Override
        public Fragment getItem(int position){
            return mFragments.get(position);
        }

        @Override
        public int getCount(){
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onItemSelected(Uri contentUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh){
        Intent intent = new Intent(this, ItemDetailActivity.class)
                .setData(contentUri);
        startActivity(intent);
    }
    @Override
    public void onItemLongSelected(Long id){
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
//        b.setTitle("Please enter a category");
        b.setMessage("Do you want to delete this item?");
        final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.ItemsEntry.CONTENT_URI,id);
        Log.d("TAG", "myContentUri = " + myContentUri.toString());
        b.setPositiveButton(R.string.delete_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // SHOULD NOW WORK
                int rowsDeleted = getContentResolver().delete(myContentUri, null,null);


                Log.d("TAG", "rows deleted = " + rowsDeleted);

            }
        });
        b.setNegativeButton("CANCEL", null);
        b.create().show();
    }

    private static List<String> getCategories(Cursor c){
        List<String> categories = new ArrayList<String>();
        c.moveToFirst();
        do{
            String category = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
            category = category.substring(0,1).toUpperCase() + category.substring(1);
            if(!categories.contains(category)){
                categories.add(category);
            }
        }while(c.moveToNext());
        return categories;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    protected void onDestroy(){
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            myTTS.setLanguage(Locale.US);
        }else if(status == TextToSpeech.ERROR){
            Toast.makeText(this, "Sorry! Text to Speech failed", Toast.LENGTH_LONG).show();
        }
    }
    public static void readText(String text){
        myTTS.setSpeechRate(0.75f);
        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}