package com.example.android.project7;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project7.data.ItemsContract;
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

    private static LinearLayout mGetFileLayout;
    private static EditText mFilePathEditText;
    private static String mBackupPath;

    private GoogleApiClient mGoogleApiClient;

    private static final int MY_DATA_CHECK_CODE = 0;
    private static final int RC_SIGN_IN = 1;
    private static final int FILE_SELECT_CODE=2;
    private String mDisplayName;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d("MA", "database path is : " + getDatabasePath("items.db"));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        startAsyncInsert();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if(mNavigationView != null){
            setupDrawerContent(mNavigationView);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if(mViewPager != null){
            setupViewPager();
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (ItemsGridFragment.getEditMode()){
            mFab.setVisibility(View.VISIBLE);
        }else{
            mFab.setVisibility(View.GONE);
        }


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void startAsyncInsert(){
        Cursor c = getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI,null,null,null,null);

        if(c == null || !c.moveToFirst()){
            StarterDataAsyncTask sdat = new StarterDataAsyncTask(this);
            sdat.execute();
        }else{
            c.close();
        }
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
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        mSignInButton.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.GONE);
        mUsername.setVisibility(View.GONE);
        Toast.makeText(this, "User has signed out", Toast.LENGTH_LONG).show();

    }

    public static void openDrawerLayout(){
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
        }else{
            mCategories = getCategories(c);
            for (String category : mCategories){
            }
        }

        for (String category : mCategories){
            addCategory(ItemsGridFragment.newInstance(category.toLowerCase()),
                    category);
        }
        mViewPager.setAdapter(mAdapter);
        c.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//        Log.d("MA", "item ID = " + item.getItemId() + " string is " + item.toString());
//        switch (id){
//            default:
//        }
//        return false;
//    }

    private static void addCategory(Fragment fragment, String category){
        mArgs.putString("category", category);
        mAdapter.addFragment(fragment, category);
        mViewPager.setAdapter(mAdapter);
    }


    private void setupDrawerContent(NavigationView navigationView){
        View headerView = navigationView.getHeaderView(0);

        ImageButton closeButton = (ImageButton)headerView.findViewById(R.id.close_drawer_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mSignInButton = (SignInButton)headerView.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.sign_in_button:
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
                        signOut();
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    default:
                }
            }
        });

        mUsername = (TextView)headerView.findViewById(R.id.username);
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
                                verifyStoragePermissions(MainActivity.this);
                                final String inFileName = getString(R.string.default_db_path);
                                File dbFile = new File(inFileName);
                                try {
                                    FileInputStream fis = new FileInputStream(dbFile);
                                    String outFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + getString(R.string.db_copy_name);
                                    OutputStream output = new FileOutputStream(outFileName);
                                    Log.d("MA", "outFileName = " + outFileName);

                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = fis.read(buffer)) > 0) {
                                        output.write(buffer, 0, length);
                                    }

                                    output.flush();
                                    output.close();
                                    fis.close();
                                    Log.d("MA", "File saved to ... " + outFileName);
                                    Toast.makeText(MainActivity.this,"File saved to ... " + outFileName,Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.restore_db:

                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                                b.setTitle(getString(R.string.restore_db_title));

                                mGetFileLayout = new LinearLayout(MainActivity.this);
                                mGetFileLayout.setOrientation(LinearLayout.VERTICAL);
                                mGetFileLayout.setPadding(50, 5, 50, 5);
                                mGetFileLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                mFilePathEditText = new EditText(MainActivity.this);
                                mFilePathEditText.setHint(getString(R.string.db_restore_hint));

                                final ImageButton openFileManagerButton = new ImageButton(MainActivity.this);
                                openFileManagerButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                openFileManagerButton.setImageResource(R.drawable.ic_folder_open_24dp);
                                openFileManagerButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //get file intent
                                        mGetFileLayout.removeView(mFilePathEditText);
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType(getString(R.string.get_content_type_file));
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);

                                        try {
                                            startActivityForResult(
                                                    Intent.createChooser(intent, "Select a File to Upload"),
                                                    FILE_SELECT_CODE);
                                        } catch (ActivityNotFoundException ex) {
                                            // Potentially direct the user to the Market with a Dialog
                                            Toast.makeText(MainActivity.this, "Please install a File Manager.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                mGetFileLayout.addView(mFilePathEditText);
                                mGetFileLayout.addView(openFileManagerButton);
                                b.setView(mGetFileLayout);
                                b.setPositiveButton(getString(R.string.restore_db_button), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String filePathString = mFilePathEditText.getText().toString();
                                        if (filePathString.equals("")) {
                                            mBackupPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + getString(R.string.db_copy_name);//getString(R.string.default_restore_path);
                                        } else {
                                            mBackupPath = filePathString;
                                        }
                                        Log.d("restore", "mBackupPath = " + mBackupPath);
                                        File savedDBFile = new File(mBackupPath);
                                        try {
                                            FileInputStream fis = new FileInputStream(savedDBFile);
                                            String outFileName = getString(R.string.default_db_path);
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

                                            Intent restartApp = new Intent(MainActivity.this, MainActivity.class);
                                            int mPendingIntentId = 123456;
                                            PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId,
                                                    restartApp, PendingIntent.FLAG_CANCEL_CURRENT);

                                            AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                            System.exit(0);
                                        }catch (FileNotFoundException f){
                                            Toast.makeText(MainActivity.this,"No backup found", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                    }
                                });
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    b.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mBackupPath = getString(R.string.default_restore_path);
                                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                        }
                                    });
                                }
                                b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                        mBackupPath = getString(R.string.default_restore_path);
                                    }
                                });
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
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
        intent.putExtra("mPosition",vh.getAdapterPosition());

        // Pass data object in the bundle and populate details activity.
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, vh.mAvatar, getString(R.string.transition_image_avatar) + vh.getAdapterPosition());
        startActivity(intent, options.toBundle());
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
        } else {
            // Signed out, show unauthenticated UI.
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
            try {
                myTTS.setLanguage(Locale.US);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(status == TextToSpeech.ERROR){
            Toast.makeText(this, "Sorry! Text to Speech failed", Toast.LENGTH_LONG).show();
        }
    }
    public static void readText(String text){
        myTTS.setSpeechRate(0.75f);
        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            updateNavigationHeader();
        }

        // Result returned from get file dialog
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String filePath = uri.getPath().toString();//Images.Media.DATA};
            Log.d("MA", "filePath = " + filePath);
            mFilePathEditText.setText(filePath);
            mGetFileLayout.addView(mFilePathEditText);

        }
    }
}