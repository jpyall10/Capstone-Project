package com.example.android.project7;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ItemDetailFragment.Callback {
//	public static final String EXTRA_NAME = "item_name";
//	public static final String EXTRA_PHOTO = "photo_id";
//	public static final String EXTRA_DESCRIPTION = "description";

	private ViewPager mViewPager;
	private Adapter mAdapter;

	private String mName;
	private FloatingActionButton fab;
	private TextToSpeech myTTS;

	private Uri itemUri;
	private Uri mCardsUri;
	private Cursor mCursor;

	private Boolean mEditMode = false;

	private int MY_DATA_CHECK_CODE = 0;

	private static final int REQUEST_IMAGE_CAPTURE = 1;
//	private static final int REQUEST_VIDEO_CAPTURE = 2;
	public static final int MEDIA_TYPE_IMAGE = 100;
	public static final int MEDIA_TYPE_VIDEO = 200;

	private static final String IMAGE_DIRECTORY_NAME = "Teach Me";

	private Uri fileUri;

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



		//ab.setDisplayHomeAsUpEnabled(true);

		//imgPreview = (ImageView) findViewById(R.id.imageView);

		Intent intent = getIntent();
		itemUri = intent.getData();
		Log.d("TAG", "itemUri = " + itemUri);




		mCursor = getContentResolver().query(itemUri,null,null,null,null);
		mCursor.moveToFirst();
		final String itemName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));//intent.getStringExtra(EXTRA_NAME);
		final int photoId = mCursor.getInt(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID));//intent.getIntExtra(EXTRA_PHOTO, R.drawable.v_face);
		final String photoUrlString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
		//final String description = intent.getStringExtra(EXTRA_DESCRIPTION);

		mName = itemName;

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(itemName);

		//final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

		fab = (FloatingActionButton)findViewById(R.id.fab);
		//fab.setImageURI(Uri.parse("android.resource://com.example.android.project7/R.drawable.ic_add_24dp"));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				speakName(mName);
//				mp.start();
			}
		});

		ActivityCompat.requestPermissions(ItemDetailActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				1);

		if (photoId > 0 && (photoUrlString == null || photoUrlString.equals(""))){
			loadBackdrop(photoId);
		} else if (photoUrlString != null && !photoUrlString.equals("")){
			loadBackdrop(photoUrlString);
		}


		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		setupViewPager();


//		initExtraCardViews();

		//initAdditionalPic();
		//mCursor.close();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu){
//		//getMenuInflater().inflate(R.menu.menu_detail_fragment, menu);
//		return true;
//	}
	private void setupViewPager(){
		mAdapter = new Adapter(getSupportFragmentManager());
		if (itemUri != null){
			Long id = ItemsContract.ItemsEntry.getIdFromUri(itemUri);
			mAdapter.addFragment(ItemDetailFragment.newInstance(id));
		}
		mViewPager.setAdapter(mAdapter);
	}
//
	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
//		initExtraCardViews();
		Log.d("TAG", "onResumeRan");

	}


//	private void initExtraCardViews(){
//		try{
//			mCursor = getContentResolver().query(itemUri,null,null,null,null);
//			mCursor.moveToFirst();
//			String cardLabel_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_1));
//			String cardDescription_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1));
//			if(!cardLabel_1.isEmpty() || !cardDescription_1.isEmpty()) {
//				mExtraCard_1 = (CardView)findViewById(R.id.extra_card_1);
//				mExtraCard_1.setVisibility(View.VISIBLE);
//				mCardDescriptionView_1 = (TextView)findViewById(R.id.card_description_1);
//				mCardLabelView_1 = (TextView) findViewById(R.id.card_label_1);
//				mCardLabelView_1.setText(cardLabel_1);
//				mCardDescriptionView_1.setText(cardDescription_1);
////				if (cardLabel_1.length() <= 0){
////					mCardLabelView_1.setText(cardLabel_1);
////					mCardDescriptionView_1.setVisibility(View.GONE);
////				}else{
////					mCardLabelView_1.setVisibility(View.GONE);
////					mCardDescriptionView_1.setText(cardDescription_1);
////				}
//							}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}

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

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

//	private void initAdditionalPic(){
//		String picUri = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
//		if (picUri == null){
//			imgPreview.setImageResource(R.drawable.v_face);
//		}else{
//			imgPreview.setImageURI(Uri.parse(picUri));
//		}
//	}

	@Override
	protected void onDestroy(){
		if(myTTS != null) {

			myTTS.stop();
			myTTS.shutdown();
		}
		super.onDestroy();
	}

	private void speakName(String name){
		myTTS.setSpeechRate(0.75f);
		myTTS.speak(name, TextToSpeech.QUEUE_FLUSH, null);
	}

	private void loadBackdrop(int photo) {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
			.load(photo)
			.centerCrop()
			.into(imageView);
	}

	private void loadBackdrop(String photoUriString) {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
				.load(Uri.parse(photoUriString))
				.centerCrop()
				.into(imageView);
	}

	private void loadBackdrop(Uri uri){
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
				.load(uri)
				.centerCrop()
				.into(imageView);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu){
//		getMenuInflater().inflate(R.menu.menu_detail_fragment, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem menuItem){
//
//		switch(menuItem.getItemId()){
//			case R.id.edit_item:
////				toggleEditMode();
////				if(mEditMode){
////
////				}
////			case R.id.item_settings:
////				Intent intent = new Intent(this, EditItemDetailActivity.class).setData(itemUri);
////				startActivity(intent);
//			default:
//		}
//		return true;
//	}

	private void toggleEditMode(){
		mEditMode = !mEditMode;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS){
			myTTS.setLanguage(Locale.UK);
		}else if(status == TextToSpeech.ERROR){
			Toast.makeText(this, "Sorry! Text to Speech failed", Toast.LENGTH_LONG).show();
		}
	}

	private void sendPicToDb(Uri picUri){
		ContentValues cv = new ContentValues();
		cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, picUri.toString());
		int updated = getContentResolver().update(itemUri,cv,null,null);

		Log.d("TAG", "updated = " + updated);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				Uri takenPictureUri = data.getData();
				sendPicToDb(takenPictureUri);
				//mImageBitmap = (Bitmap) extras.get("data");
				//ContentValues cv = new ContentValues();
				//cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_PIC_URI_1,takenPictureUri.toString());
				//getContentResolver().update(itemUri, cv, null, null);
				//mCursor = getContentResolver().query(itemUri,new String[]{ItemsContract.ItemsEntry.COLUMN_EXTRA_PIC_URI_1},null,null,null);
				//Cursor c = getContentResolver().query(itemUri,null,null,null,null);
				mCursor.moveToFirst();
				String uriString = 	mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
				//mCursor.close();
				if(uriString != null){
					loadBackdrop(uriString);
				}
				//c.close();

//				imgPreview.setImageURI(Uri.parse(uriString));
			}
		}
	}

	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}
	public void onTakePhotoClick(View view) {
		takePicture();
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
	public void onItemSelected(Uri idUri, ItemDetailAdapter.ItemDetailAdapterViewHolder vh) {

	}

	@Override
	public void onItemLongSelected(Long id) {
		//edit details of card when in edit mode
		//else do nothing
	}


}
