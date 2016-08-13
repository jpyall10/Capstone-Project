package com.example.android.project7;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ItemDetailFragment.Callback {
	private ViewPager mViewPager;
	private Adapter mAdapter;

	private ImageView mPreviewImage;
	private LinearLayout mLayout;
	private EditText mPhotoUrlBox;

	private int mPosition;

	private String mGetPhotoUriString;
	private String mTakePhotoUriString;

	private  String mName;
	private  String mPhotoUriString;

	public static FloatingActionButton fab;
	private TextToSpeech myTTS;

	private Uri itemUri;
	private Cursor mCursor;

	private CollapsingToolbarLayout mCollapsingToolbar;
	public static Boolean mEditMode = false;

	private int MY_DATA_CHECK_CODE = 0;

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int RESULT_LOAD_IMAGE = 3;

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();

		public Adapter(FragmentManager fm){super(fm);}

		public void addFragment(Fragment fragment){
			mFragments.add(fragment);
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
			return mFragments.get(position).toString();
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		itemUri = intent.getData();
		mPosition = intent.getIntExtra("mPosition",0);

		Log.d("TAG", "itemUri = " + itemUri);

		mCursor = getContentResolver().query(itemUri,null,null,null,null);
		mCursor.moveToFirst();
		mName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));//intent.getStringExtra(EXTRA_NAME);
		TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
		splitter.setString(mName);
		String capitalizedName = "";
		for(String s : splitter){
			capitalizedName += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		}
		mPhotoUriString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		mCollapsingToolbar.setTitle(capitalizedName);

		//final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

		fab = (FloatingActionButton)findViewById(R.id.fab);
		if (getEditMode()){
			fab.setVisibility(View.VISIBLE);
		}else{
			fab.setVisibility(View.GONE);
		}
		ActivityCompat.requestPermissions(ItemDetailActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				1);

		if (mPhotoUriString == null || mPhotoUriString.equals("")){
			mPhotoUriString = getString(R.string.android_resource_uri_base) + R.drawable.cat_1;
		} else if (mPhotoUriString != null && !mPhotoUriString.equals("")){
			loadBackdrop(mPhotoUriString);
		}


		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		setupViewPager();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
		return true;
	}

	public void setGetPhotoUriString(String getPhotoUriString) {
		mGetPhotoUriString = getPhotoUriString;
	}
	public String getGetPhotoUriString(){
		return mGetPhotoUriString;
	}

	public void setTakePhotoUriString(String takePhotoUriString) {
		mTakePhotoUriString = takePhotoUriString;
	}
	public String getTakePhotoUriString(){
		return mTakePhotoUriString;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		//super.onOptionsItemSelected(menuItem);
		switch(menuItem.getItemId()){
			case android.R.id.home:
				this.onBackPressed();
				break;
			case R.id.edit_item:
				if(getEditMode()) {
					editItem();
				}else{
					Toast toast = Toast.makeText(this, "You must be in Edit Mode to edit item", Toast.LENGTH_LONG);
					toast.show();
				}
				break;
			default:
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void editItem(){
		//create ContentValues to add to Provider
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		final ContentValues cvEdit = new ContentValues();

		//Start Dialog Builder
		AlertDialog.Builder bEdit = new AlertDialog.Builder(this);
		bEdit.setTitle("Edit Item");

		//Initialize main dialog layout
		mLayout = new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);

		//get current item name and set edit box
		mCursor.moveToFirst();
		String currentName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
		final EditText editNameBox = new EditText(this);
		editNameBox.setText(currentName);
		mLayout.addView(editNameBox);

		//get current item category and set edit box
		String currentCategory = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
		final EditText editCategoryBox = new EditText(this);
		editCategoryBox.setText(currentCategory);
		mLayout.addView(editCategoryBox);

		//get current pic uristring
		String currentPicUriString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));

		//initialize PreviewImageView and set layout params
		mPreviewImage = new ImageView(this);
		int pixels = (int)dipToPixels(this,200);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		mPreviewImage.setLayoutParams(params);

		//Initialize photoUrlBox and setText or hint
		mPhotoUrlBox = new EditText(this);

		if(currentPicUriString != null && !currentPicUriString.equals("")) {
			mPhotoUrlBox.setText(currentPicUriString);
			loadPreviewImage(currentPicUriString);
		}else{
			mPhotoUrlBox.setHint("Enter photo URL");
		}

		mPhotoUrlBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String url = s.toString();
				loadPreviewImage(url);
			}
		});

		//Created buttons Layout
		final LinearLayout editPictureButtons = new LinearLayout(this);
		editPictureButtons.setOrientation(LinearLayout.HORIZONTAL);

		//create button to get image from files and set onclick
		final ImageButton getPhotoButton = new ImageButton(this);
		getPhotoButton.setImageResource(R.drawable.ic_folder_open_24dp);
		getPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		getPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLayout.removeView(mPreviewImage);
				mLayout.removeView(mPhotoUrlBox);
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});

		//create button to take a new picture with camera and set onclick
		final ImageButton takeNewPhotoButton = new ImageButton(this);
		takeNewPhotoButton.setImageResource(R.drawable.ic_photo_camera_24dp);
		takeNewPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		takeNewPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLayout.removeView(mPreviewImage);
				mLayout.removeView(mPhotoUrlBox);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

			}
		});

		//add buttons to button layout
		editPictureButtons.addView(getPhotoButton);
		editPictureButtons.addView(takeNewPhotoButton);

		//add buttons, urlbox and preview image to main layout
		mLayout.addView(editPictureButtons);
		mLayout.addView(mPhotoUrlBox);
		mLayout.addView(mPreviewImage);

		//set dialog layout
		bEdit.setView(mLayout);

		//set dialog add button
		bEdit.setPositiveButton("SAVE", null);

		//set dialog cancel button
		bEdit.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			bEdit.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

				}
			});
		}
		final AlertDialog d = bEdit.create();
		d.show();

		Button button = d.getButton(DialogInterface.BUTTON_POSITIVE);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String category = editCategoryBox.getText().toString();
				if (!category.equals("")) {
					String name = editNameBox.getText().toString();
					String photoUrl = mPhotoUrlBox.getText().toString();
					cvEdit.put(ItemsContract.ItemsEntry.COLUMN_NAME, name);
					cvEdit.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, category);

					if (photoUrl.length() <= 0) {
						//photoUrl = getString(R.string.android_resource_uri_base) + R.drawable.v_face;
						if (getGetPhotoUriString() != null && !getGetPhotoUriString().equals("")) {
							photoUrl = getGetPhotoUriString();
						} else if (getTakePhotoUriString() != null && !getTakePhotoUriString().equals("")) {
							photoUrl = getTakePhotoUriString();
						}else{
							photoUrl = getString(R.string.android_resource_uri_base) + R.color.colorAccent;
						}
					}
					cvEdit.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, photoUrl);
					Log.d("IGF", "photoUrl is " + photoUrl);

					int rowsUpdated = getContentResolver().
							update(itemUri, cvEdit, null, null);

					mCursor = getContentResolver().query(itemUri, null, null, null, null);
					loadBackdrop(photoUrl);
					mCollapsingToolbar.setTitle(name);
					MainActivity.addTab(category);
					toggleEditMode();

					Log.d("IDA", "updatedRows = " + rowsUpdated);
					d.dismiss();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				} else {
					Toast.makeText(ItemDetailActivity.this, getString(R.string.enter_category_warning), Toast.LENGTH_SHORT).show();
				}

			}
		});


	}

	private void setupViewPager(){
		mAdapter = new Adapter(getSupportFragmentManager());
		if (itemUri != null){
			Long id = ItemsContract.ItemsEntry.getIdFromUri(itemUri);
			mAdapter.addFragment(ItemDetailFragment.newInstance(id));
		}
		mViewPager.setAdapter(mAdapter);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {
				// If request is cancelled, the result arrays are empty.

				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {


				} else {

					Toast.makeText(ItemDetailActivity.this, "Permission deny to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}

		}
	}

	@Override
	protected void onDestroy(){
		if(myTTS != null) {

			myTTS.stop();
			myTTS.shutdown();
		}
		mCursor.close();
		super.onDestroy();
	}

	private void readText(String text){
		myTTS.setSpeechRate(0.75f);
		myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	private void loadBackdrop(String photoUriString) {
		final ImageView imageView =(ImageView) findViewById(R.id.backdrop);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			imageView.setTransitionName(getString(R.string.transition_image_avatar)+mPosition);
		}
		Glide.with(this)
				.load(photoUriString)
				.centerCrop()
				.into(imageView);
	}

	public static float dipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}

	private void loadPreviewImage(String path) {
		Glide.with(this)
				.load(path)
				.fitCenter()
				.into(mPreviewImage);
	}

	public static void toggleEditMode(){
		mEditMode = !mEditMode;

	}

	public static boolean getEditMode(){return mEditMode;}

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

	public void onPhotoClick(View view) {

		if (getEditMode() == true){
		}else{
			readText(mName);

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putParcelable("item_uri", itemUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		itemUri = savedInstanceState.getParcelable("item_uri");
	}

	@Override
	public void onItemSelected(Long id, ItemDetailAdapter.ItemDetailAdapterViewHolder vh) {
		final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), id);
		Log.d("IDA", "item uri = " + itemUri.toString());

		Cursor c = getContentResolver().query(myContentUri,
				new String[]{ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL, ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION},
				null, null, null);
		c.moveToFirst();
		final String title = c.getString(c.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL));
		Log.d("IDA", "title = " + title);
		if (title != null && title != "") {

			final String description = c.getString(c.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION));
			readText(title + "     " + description);
		}
		c.close();
	}

	@Override
	public void onItemLongSelected(Long id) {
		if(getEditMode()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			AlertDialog.Builder b = new AlertDialog.Builder(ItemDetailActivity.this);
			b.setMessage("Do you want to delete this card?");
			final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), id);
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
		if (requestCode == RESULT_LOAD_IMAGE){
			if (resultCode == RESULT_OK){
				Uri selectedImage = data.getData();
				String[] filePathCol = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(selectedImage, filePathCol, null, null, null);
				cursor.moveToFirst();
				String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));

				if(picPath == null || picPath.equals("")){
					Toast.makeText(this,getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
				}
				else {
					setTakePhotoUriString(picPath);
					if (picPath != null && !picPath.equals("")) {
						loadPreviewImage(picPath);
						mPhotoUrlBox.setText(picPath);
						mLayout.addView(mPhotoUrlBox);
						mLayout.addView(mPreviewImage);
					}
				}
			}
		}
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == RESULT_OK){
				try {
					Bundle extras = data.getExtras();
					Uri takenPictureUri = data.getData();
					String[] filePathCol = {MediaStore.Images.Media.DATA};

					Cursor cursor = getContentResolver().query(takenPictureUri, filePathCol, null, null, null);
					cursor.moveToFirst();
					String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
					setGetPhotoUriString(picPath);
					if (picPath != null && !picPath.equals("")) {
						loadPreviewImage(picPath);
						mPhotoUrlBox.setText(picPath);
						mLayout.addView(mPhotoUrlBox);
						mLayout.addView(mPreviewImage);
					}
				}catch(Exception e){
					Toast.makeText(this, getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
