package com.example.android.project7;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends AppCompatActivity {
//
//    private GridLayoutManager gridLayoutManager;
//    private DrawerLayout mDrawerLayout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setTitle(null);
//
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
////        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
////        setSupportActionBar(topToolBar);
//////        topToolBar.setLogo(R.drawable.logo);
////        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if(navigationView != null){
//            setupDrawerContent(navigationView);
//        }
//
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        if(viewPager != null){
//            setupViewPager(viewPager);
//        }
//
//        List<Item> rowListItem = getAllItemList();
//        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
//
//        RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
//        rView.setHasFixedSize(true);
//        rView.setLayoutManager(gridLayoutManager);
//
//        ItemsGridAdapter rcAdapter = new ItemsGridAdapter(MainActivity.this, rowListItem);
//        rView.setAdapter(rcAdapter);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view){
//                //Put code here to add a member
//            }
//        });
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void setupViewPager(ViewPager viewPager){
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_people));
//        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_animals));
//        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_food));
//        viewPager.setAdapter(adapter);
//    }
//
//    private void setupDrawerContent(NavigationView navigationView){
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem){
//                menuItem.setChecked(true);
//                mDrawerLayout.closeDrawers();
//                return true;
//            }
//        });
//    }
//
//    private List<Item> getAllItemList(){
//
//        List<Item> allItems = new ArrayList<Item>();
//        allItems.add(new Item(this, R.string.animal_label_cow, R.drawable.cow_1));
//        allItems.add(new Item(this, R.string.animal_label_cat, R.drawable.cat_1));
//        allItems.add(new Item(this, R.string.animal_label_dog, R.drawable.dog_1));
//        allItems.add(new Item(this, R.string.animal_label_owl, R.drawable.owl_1));
//        allItems.add(new Item(this, R.string.person_name_label, R.drawable.v_face));
//        allItems.add(new Item(this, R.string.person_name_label, R.drawable.v_face));
//        allItems.add(new Item(this, R.string.person_name_label, R.drawable.v_face));
//
//        return allItems;
//    }
//
//    static class Adapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragments = new ArrayList<>();
//        private final List<String> mFragmentTitles = new ArrayList<>();
//
//        public Adapter(FragmentManager fm){super(fm);}
//
//        public void addFragment(Fragment fragment, String title){
//            mFragments.add(fragment);
//            mFragmentTitles.add(title);
//        }
//
//        @Override
//        public Fragment getItem(int position){
//            return mFragments.get(position);
//        }
//
//        @Override
//        public int getCount(){
//            return mFragments.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position){
//            return mFragmentTitles.get(position);
//        }
//    }
//}

public class MainActivity extends AppCompatActivity implements ItemGridFragment.Callback{
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //if(navigationView != null){
        //    setupDrawerContent(navigationView);
        //}

//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        if(viewPager != null){
//            setupViewPager(viewPager);
//        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Put code here to add a member
            }
        });

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager){
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_people));
        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_animals));
        adapter.addFragment(new ItemGridFragment(), getString(R.string.category_food));
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem){
                menuItem.setChecked(true);
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
    }

}