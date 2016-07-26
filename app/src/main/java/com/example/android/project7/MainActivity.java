package com.example.android.project7;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemsGridFragment.Callback{

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private Adapter mAdapter;
    private Bundle mArgs;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //ab.setDisplayHomeAsUpEnabled(true);


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

    private void setupViewPager(){
        mArgs = new Bundle();
        mAdapter = new Adapter(getSupportFragmentManager());
        Cursor c = getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI,null,null,null,null);
        Log.d("TAG", "the size of the cursor is " + c.getCount());
        List<String> categories = new ArrayList<>();
        if (c.getCount()<=0){
            categories.add(getString(R.string.category_animals));
            categories.add(getString(R.string.category_food));
            categories.add(getString(R.string.category_people));

//            if(mAdapter.mFragments.size() <= 0) {
//                addCategory(ItemsGridFragment.newInstance(getString(R.string.category_animals)), getString(R.string.category_animals));
//                addCategory(ItemsGridFragment.newInstance(getString(R.string.category_food)), getString(R.string.category_food));
//                addCategory(ItemsGridFragment.newInstance(getString(R.string.category_people)), getString(R.string.category_people));
//            }
        }else{
            categories = getCategories(c);
        }

        for (String category : categories){
            addCategory(ItemsGridFragment.newInstance(category), category);
        }





        mViewPager.setAdapter(mAdapter);
        c.close();

    }

    private void addCategory(Fragment fragment, String category){
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

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm){super(fm);}

        public void addFragment(Fragment fragment, String category){
            mFragments.add(fragment);
            mFragmentTitles.add(category);
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

    private List<String> getCategories(Cursor c){
        List<String> categories = new ArrayList<String>();
        c.moveToFirst();
        do{
            String category = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
            if(!categories.contains(category)){
                categories.add(category);
            }
        }while(c.moveToNext());
        return categories;
    }

}