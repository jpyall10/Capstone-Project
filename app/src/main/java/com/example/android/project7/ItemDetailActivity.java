package com.example.android.project7;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
//	public static final String EXTRA_NAME = "item_name";
//	public static final String EXTRA_PHOTO = "photo_id";
//	public static final String EXTRA_DESCRIPTION = "description";

	private String mName;
	private FloatingActionButton fab;
	private TextToSpeech myTTS;

	private ImageView imgPreview;
	private Bitmap mImageBitmap;
	private Uri itemUri;
	private Cursor mCursor;
	private TextView mCardLabelView_1;
	private TextView mCardDescriptionView_1;
	private CardView mExtraCard_1;

	private int MY_DATA_CHECK_CODE = 0;

	private static final int REQUEST_IMAGE_CAPTURE = 1;
//	private static final int REQUEST_VIDEO_CAPTURE = 2;
	public static final int MEDIA_TYPE_IMAGE = 100;
	public static final int MEDIA_TYPE_VIDEO = 200;

	private static final String IMAGE_DIRECTORY_NAME = "Teach Me";

	private Uri fileUri;




	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		//imgPreview = (ImageView) findViewById(R.id.imageView);

		Intent intent = getIntent();
		itemUri = intent.getData();

		mCursor = getContentResolver().query(itemUri,null,null,null,null);
		mCursor.moveToFirst();
		final String itemName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));//intent.getStringExtra(EXTRA_NAME);
		final int photoId = mCursor.getInt(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID));//intent.getIntExtra(EXTRA_PHOTO, R.drawable.v_face);
		//final String description = intent.getStringExtra(EXTRA_DESCRIPTION);

		mName = itemName;

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(itemName);

		//final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

		fab = (FloatingActionButton)findViewById(R.id.fab);
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

		loadBackdrop(photoId);
		initExtraCardViews();

		//initAdditionalPic();
		//mCursor.close();
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		initExtraCardViews();
		Log.d("TAG", "onResumeRan");

	}


	private void initExtraCardViews(){
		try{
			mCursor = getContentResolver().query(itemUri,null,null,null,null);
			mCursor.moveToFirst();
			String cardLabel_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_1));
			String cardDescription_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1));
			if(cardLabel_1.length() > 0 || cardDescription_1.length() > 0) {
				mExtraCard_1 = (CardView)findViewById(R.id.extra_card_1);
				mExtraCard_1.setVisibility(View.VISIBLE);
				mCardDescriptionView_1 = (TextView)findViewById(R.id.card_description_1);
				mCardLabelView_1 = (TextView) findViewById(R.id.card_label_1);
				mCardLabelView_1.setText(cardLabel_1);
				mCardDescriptionView_1.setText(cardDescription_1);
//				if (cardLabel_1.length() <= 0){
//					mCardLabelView_1.setText(cardLabel_1);
//					mCardDescriptionView_1.setVisibility(View.GONE);
//				}else{
//					mCardLabelView_1.setVisibility(View.GONE);
//					mCardDescriptionView_1.setText(cardDescription_1);
//				}
							}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {
				// If request is cancelled, the result arrays are empty.

				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            /*HERE PERMISSION IS ALLOWED.
            *
            * YOU SHOULD CODE HERE*/


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

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_detail_fragment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){

		switch(menuItem.getItemId()){
			case R.id.item_settings:
				Intent intent = new Intent(this, EditItemDetailActivity.class).setData(itemUri);
				startActivity(intent);
			default:
		}
		return true;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS){
			myTTS.setLanguage(Locale.US);
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
				Cursor c = getContentResolver().query(itemUri,null,null,null,null);
				c.moveToFirst();
				String uriString = 	c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
				//mCursor.close();

				imgPreview.setImageURI(Uri.parse(uriString));
			}
		}
//		if (requestCode == REQUEST_IMAGE_CAPTURE) {
//			if (resultCode == RESULT_OK) {
//				try {
////					grantUriPermission(com.example.android.project7,);
//					Log.i("TAG", "inside Samsung Phones");
//					String[] projection = {
//							MediaStore.Images.Thumbnails._ID, // The columns we want
//							MediaStore.Images.Thumbnails.IMAGE_ID,
//							MediaStore.Images.Thumbnails.KIND,
//							MediaStore.Images.Thumbnails.DATA };
//					String selection = MediaStore.Images.Thumbnails.KIND + "=" + // Select
//							// only
//							// mini's
//							MediaStore.Images.Thumbnails.MINI_KIND;
//
//					String sort = MediaStore.Images.Thumbnails._ID + " DESC";
//
//					Cursor myCursor = getContentResolver().query(
//							MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//							projection, selection, null, sort);
//
//					long imageId = 0l;
//					long thumbnailImageId = 0l;
//					String thumbnailPath = "";
//
//					try {
//						myCursor.moveToFirst();
//						imageId = myCursor
//								.getLong(myCursor
//										.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
//						thumbnailImageId = myCursor
//								.getLong(myCursor
//										.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
//						thumbnailPath = myCursor
//								.getString(myCursor
//										.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
//					} finally {
//						// myCursor.close();
//					}
//
//					// Create new Cursor to obtain the file Path for the large image
//
//					String[] largeFileProjection = {
//							MediaStore.Images.ImageColumns._ID,
//							MediaStore.Images.ImageColumns.DATA };
//
//					String largeFileSort = MediaStore.Images.ImageColumns._ID
//							+ " DESC";
//					myCursor = getContentResolver().query(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//							largeFileProjection, null, null, largeFileSort);
//					String largeImagePath = "";
//
//					try {
//						myCursor.moveToFirst();
//
//						// This will actually give yo uthe file path location of the
//						// image.
//						largeImagePath = myCursor
//								.getString(myCursor
//										.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
//						fileUri = Uri.fromFile(new File(
//								largeImagePath));
//
//					} finally {
//						// myCursor.close();
//					}
//					// These are the two URI's you'll be interested in. They give
//					// you a
//					// handle to the actual images
//					Uri uriLargeImage = Uri.withAppendedPath(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//							String.valueOf(imageId));
//					Uri uriThumbnailImage = Uri.withAppendedPath(
//							MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//							String.valueOf(thumbnailImageId));
//
//					// I've left out the remaining code, as all I do is assign the
//					// URI's
//					// to my own objects anyways...
//
//					imgPreview.setImageURI(fileUri);
//					Log.d("TAG", "uriLargeImage is " + uriLargeImage.toString());
//				} catch (Exception e) {
//
//					Log.i("TAG",
//							"inside catch Samsung Phones exception " + e.toString());
//
//				}
//			}
//				//previewCapturedImage();
//				Bundle extras = data.getExtras();
//				mImageBitmap = (Bitmap) extras.get("data");
//
//				imgPreview.setImageBitmap(mImageBitmap);
//			}else if(resultCode == RESULT_CANCELED) {
//				Toast.makeText(getApplicationContext(), "User cancelled camera", Toast.LENGTH_LONG);
//			}else{
//				Toast.makeText(getApplicationContext(), "Failed to take picture", Toast.LENGTH_LONG);
//
//			}
//		}
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


}
