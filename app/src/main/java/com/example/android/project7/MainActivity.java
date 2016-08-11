package com.example.android.project7;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.test.SingleLaunchActivityTestCase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.project7.data.ItemsContract;
import com.example.android.project7.data.ItemsDbHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener, ItemsGridFragment.Callback{

    private static DrawerLayout mDrawerLayout;
    private static ViewPager mViewPager;
    private static Adapter mAdapter;
    private static Bundle mArgs;
    private static TabLayout mTabLayout;
    private static TextToSpeech myTTS;
    private static List<String> mCategories;
    private static NavigationView mNavigationView;
    private static TextView mUsername;
    private static SignInButton mSignInButton;
    private static Button mSignOutButton;
    public static FloatingActionButton mFab;

    private static boolean editMode = false;

    private String mGetPhotoUriString;
    private String mTakePhotoUriString;
    private EditText mPhotoUrlBox;
    private ImageView mPreviewImage;
    private LinearLayout mLayout;

    private InterstitialAd mInterstitialAd;
    private GoogleApiClient mGoogleApiClient;

    private static final int MY_DATA_CHECK_CODE = 0;
    private static final int RC_SIGN_IN = 1;
    private String mDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MA", "database path is : " + getDatabasePath("items.db"));
//        Uri contentUri = getIntent() != null ? getIntent().getData() : null;
//        if(contentUri!=null){
//            Log.d("MA", "contentUri = " + contentUri.toString());
//        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if(mNavigationView != null){
            setupDrawerContent(mNavigationView);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if(mViewPager != null){
            setupViewPager();
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Put code here to add a member
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }
//                requestNewInterstitial();
//            }
//        });
        if (ItemsGridFragment.getEditMode()){
            mFab.setVisibility(View.VISIBLE);
        }else{
            mFab.setVisibility(View.GONE);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                requestNewInterstitial();
//            }
//        });
//
//        requestNewInterstitial();
    }

    private void updateNavigationHeader(){
        if(!mGoogleApiClient.isConnecting()){
            if(mGoogleApiClient.isConnected()){
                Log.d("MA", "updateNavHeader with mGoogleApiClient connected");
                mUsername.setVisibility(View.VISIBLE);
                mUsername.setText(getDisplayName());
                mSignInButton.setVisibility(View.GONE);
                mSignOutButton.setVisibility(View.VISIBLE);
            }else{
                Log.d("MA", "updateNavHeader with mGoogleApiClient disconnected");
                //Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
                //mGoogleApiClient.clearDefaultAccountAndReconnect();
                mUsername.setVisibility(View.GONE);
                mSignInButton.setVisibility(View.VISIBLE);
                mSignOutButton.setVisibility(View.GONE);
            }
            //View headerView = nav.getHeaderView(0);
            //nav.removeView(userName);
//            userName.setText(getDisplayName());
//            nav.addView(userName);
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        //updateNavigationHeader();
        mSignInButton.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.GONE);
        mUsername.setVisibility(View.GONE);
        Toast.makeText(this, "User has signed out", Toast.LENGTH_LONG).show();

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
            for (String category : mCategories){
            }
        }

        for (String category : mCategories){
            //category = category.substring(0,1).toUpperCase() + category.substring(1);
            addCategory(ItemsGridFragment.newInstance(category.toLowerCase()),
                    category /*.substring(0,1).toUpperCase() + category.substring(1).toLowerCase()*/);
        }
        mViewPager.setAdapter(mAdapter);
        c.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        Log.d("MA", "item ID = " + item.getItemId() + " string is " + item.toString());
        switch (id){
//            case android.R.id.home:
//                openDrawerLayout();
//                break;
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
        View headerView = navigationView.getHeaderView(0);
        mSignInButton = (SignInButton)headerView.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.sign_in_button:
                        Log.d("MA", "Sign in clicked");
                        signIn();
                        break;
                    default:
                }
            }
        });
        mSignOutButton = (Button)headerView.findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.sign_out_button:
                        Log.d("MA", "Sign out clicked");
                        signOut();
                        break;
                    default:
                }
            }
        });
        //mSignOutButton.setVisibility(View.GONE);
        mUsername = (TextView)headerView.findViewById(R.id.username);
        //mUsername.setVisibility(View.GONE);
        updateNavigationHeader();
        Log.d("MA", "Username is " + mUsername.getText().toString());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.backup_db:
                                final String inFileName = "/data/user/0/com.example.android.project7/databases/items.db";
                                File dbFile = new File(inFileName);
                                try {
                                    FileInputStream fis = new FileInputStream(dbFile);
                                    String outFileName = Environment.getExternalStorageDirectory() + "/items_db_copy.db";
                                    Log.d("MA", "outFileName path = " + outFileName);
                                    OutputStream output = new FileOutputStream(outFileName);

                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = fis.read(buffer)) > 0) {
                                        output.write(buffer, 0, length);
                                    }

                                    output.flush();
                                    output.close();
                                    fis.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.restore_db:
                                final String savedFile = Environment.getExternalStorageDirectory() + "/items_db_copy.db";
                                File savedDBFile = new File(savedFile);
                                try {
                                    FileInputStream fis = new FileInputStream(savedDBFile);
                                    String outFileName = "/data/user/0/com.example.android.project7/databases/items.db";
                                    Log.d("MA", "outFileName path = " + outFileName);
                                    OutputStream output = new FileOutputStream(outFileName);

                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = fis.read(buffer)) > 0) {
                                        output.write(buffer, 0, length);
                                    }

                                    output.flush();
                                    output.close();
                                    fis.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent restartApp = new Intent(MainActivity.this, MainActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId,
                                        restartApp, PendingIntent.FLAG_CANCEL_CURRENT);

                                AlarmManager mgr = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                                break;

                            default:

                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currenDBPath = "/data/" + "com.example.android.project7" + "/databases/" + "items.db";
        String backupDBPath = "";
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;

    }

    private String getDisplayName(){
        return mDisplayName;
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
        if(ItemsGridFragment.getEditMode()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            //        b.setTitle("Please enter a category");
            b.setMessage("Do you want to delete this item?");
            final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.ItemsEntry.CONTENT_URI, id);
            Log.d("TAG", "myContentUri = " + myContentUri.toString());
            b.setPositiveButton(R.string.delete_item, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    // SHOULD NOW WORK
                    int rowsDeleted = getContentResolver().delete(myContentUri, null, null);


                    Log.d("TAG", "rows deleted = " + rowsDeleted);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                }
            });
            b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                b.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                });
            }
            b.create().show();
        }else{
            Toast.makeText(this,getString(R.string.delete_item_warning),Toast.LENGTH_LONG).show();
        }
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

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String displayName = acct.getDisplayName();
            setDisplayName(displayName);
            Toast toast = Toast.makeText(this, displayName + " signed in successfully", Toast.LENGTH_LONG);
            toast.show();//mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
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
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            updateNavigationHeader();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("MA", "onDestroy");
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
        }
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

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("DEVICE_ID_EMULATOR") //("03157df319a82d3d")
//                .build();
//        mInterstitialAd.loadAd(adRequest);
//    }

//    private void addItem(){
//        final ContentValues cv = new ContentValues();
//
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setTitle("Add Item");
//
//        mLayout = new LinearLayout(this);
//        mLayout.setOrientation(LinearLayout.VERTICAL);
//
//        String[] names = getNames();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,names);
//
//
//        final AutoCompleteTextView nameBox = new AutoCompleteTextView(this);
//        nameBox.setAdapter(adapter);
//        nameBox.setHint("Name");
//        mLayout.addView(nameBox);
//
//        ArrayList<String> categories = getCategories();
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,categories);
//
//
//        final AutoCompleteTextView categoryBox = new AutoCompleteTextView(this);
//        categoryBox.setHint("Category (Required)");
//        categoryBox.setAdapter(adapter2);
//        mLayout.addView(categoryBox);
//        mPhotoUrlBox = new EditText(this);
//        mPhotoUrlBox.setHint("Enter a photo URL");
//
//        final LinearLayout buttonLayout = new LinearLayout(this);
//        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//        final ImageButton getPhotoButton = new ImageButton(this);
//        getPhotoButton.setImageResource(R.drawable.ic_folder_open_24dp);
//        getPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        getPhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mLayout.removeView(mPreviewImage);
//                mLayout.removeView(mPhotoUrlBox);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//            }
//        });
//
//        final ImageButton takePhotoButton = new ImageButton(this);
//        takePhotoButton.setImageResource(R.drawable.ic_photo_camera_24dp);
//        takePhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        takePhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mLayout.removeView(mPreviewImage);
//                mLayout.removeView(mPhotoUrlBox);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        });
//
//        mPreviewImage= new ImageView(this);
//        int pixels = (int) dipToPixels(this, 200);
//        LinearLayout.LayoutParams params =
//                new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER;
//        mPreviewImage.setLayoutParams(params);
//
//        buttonLayout.addView(getPhotoButton);
//        buttonLayout.addView(takePhotoButton);
//
//        mPhotoUrlBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String url = s.toString();
//                loadPreviewImage(url);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String url = s.toString();
//                loadPreviewImage(url);
//            }
//        });
//
//        mLayout.addView(buttonLayout);
//        mLayout.addView(mPhotoUrlBox);
//        mLayout.addView(mPreviewImage);
//
//        b.setView(mLayout);
//
//        b.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String name = nameBox.getText().toString();
//                String category = categoryBox.getText().toString();
//                String photoUrl = mPhotoUrlBox.getText().toString();
//                cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, name);
//                cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, category);
//                if(photoUrl.length() <= 0) {
//                    if (getGetPhotoUriString() != null && !getGetPhotoUriString().equals("")){
//                        photoUrl = getGetPhotoUriString();
//                    }else if (getTakePhotoUriString() != null && !getTakePhotoUriString().equals("")){
//                        photoUrl = getTakePhotoUriString();
//                    }
//                    //cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.v_face);
//                }
//                Log.d("IGF", "photoUrl is " + photoUrl);
//                cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, photoUrl);
//
//                Uri itemUri = getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);
//
//                ItemsGridFragment.mItemsGridAdapter.notifyDataSetChanged();
//
//                MainActivity.addTab(category);
//
//            }
//        });
//        b.setNegativeButton("CANCEL", null);
//        b.create().show();
//
//
//
//    }
}