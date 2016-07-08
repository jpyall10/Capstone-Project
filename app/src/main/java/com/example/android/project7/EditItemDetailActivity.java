package com.example.android.project7;

import android.Manifest;
import android.content.ContentValues;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

import java.util.Locale;

public class EditItemDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
//	public static final String EXTRA_NAME = "item_name";
//	public static final String EXTRA_PHOTO = "photo_id";
//	public static final String EXTRA_DESCRIPTION = "description";

	private String mName;
	private FloatingActionButton fab;
	private TextToSpeech myTTS;

//	private ImageView imgPreview;
//	private Bitmap mImageBitmap;
	private Uri itemUri;
	private Cursor mCursor;

	//private int MY_DATA_CHECK_CODE = 0;

	private static final int REQUEST_IMAGE_CAPTURE = 1;
//	private static final int REQUEST_VIDEO_CAPTURE = 2;
	//public static final int MEDIA_TYPE_IMAGE = 100;
	//public static final int MEDIA_TYPE_VIDEO = 200;

	//private static final String IMAGE_DIRECTORY_NAME = "Teach Me";



	@Override
	protected void onDestroy(){
		syncCardInfoToDb();
		super.onDestroy();
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item_detail);

		//imgPreview = (ImageView) findViewById(R.id.imageView);

		Intent intent = getIntent();
		itemUri = intent.getData();

		mCursor = getContentResolver().query(itemUri,null,null,null,null);
		mCursor.moveToFirst();
		final String itemName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));//intent.getStringExtra(EXTRA_NAME);
		final int photoId = mCursor.getInt(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID));//intent.getIntExtra(EXTRA_PHOTO, R.drawable.v_face);
		//final String description = intent.getStringExtra(EXTRA_DESCRIPTION_1);

		mName = itemName;

//		Intent checkTTSIntent = new Intent();
//		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(itemName);

		//final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

//		fab = (FloatingActionButton)findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				speakName(mName);
////				mp.start();
//			}
//		});

		ActivityCompat.requestPermissions(EditItemDetailActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				1);


		loadBackdrop(photoId);

		syncCardInfoToDb();
//		initAdditionalPic();
		//mCursor.close();
	}

	public void syncCardInfoToDb(){
		try {
			ContentValues cv = new ContentValues();
			int updated = 0;
			EditText editLabel_1 = (EditText) findViewById(R.id.card_label_1);
			String cursorLabel_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_1));
			if (cursorLabel_1.equals("")) {
				String enteredLabel_1 = editLabel_1.getText().toString();
				cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_1, enteredLabel_1);
				updated = getContentResolver().update(itemUri, cv, null, null);
			} else {
				editLabel_1.setHint(null);
				if(editLabel_1.getText().toString().equals(cursorLabel_1))
				editLabel_1.setText(cursorLabel_1);
//				editLabel_1.setHint(null);
			}

			EditText editDescription_1 = (EditText) findViewById(R.id.card_description_1);
			String cursorDescription_1 = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1));
			if (cursorDescription_1.equals("")) {
				String enteredDescription_1 = editDescription_1.getText().toString();
				cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1, enteredDescription_1);
				updated = getContentResolver().update(itemUri, cv, null, null);
			} else {
				editDescription_1.setHint(null);
				editDescription_1.setText(cursorDescription_1);
			}


//		EditText editLabel_2 = (EditText)findViewById(R.id.card_label_2);
//		String enteredLabel_2 = editLabel_2.getText().toString();
//		String enteredDescription_2 = editDescription_2.getText().toString();
//
//		EditText editLabel_3 = (EditText)findViewById(R.id.card_label_3);
//		String enteredLabel_3 = editLabel_3.getText().toString();
//		EditText editDescription_3 = (EditText) findViewById(R.id.card_description_3);
//		String enteredDescription_3 = editDescription_3.getText().toString();
//
//
//		cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_1, enteredDescription_1);
//		cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_2, enteredLabel_2);
//		cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_2, enteredDescription_2);
//		cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_LABEL_3, enteredLabel_3);
//		cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_CARD_DESCRIPTION_3, enteredDescription_3);


//]		int updated = getContentResolver().update(itemUri,cv,null,null);
			if (updated <= 0) {
				Log.d("TAG", "item with URI " + itemUri.toString() + " was not updated successfully");
			} else {
				Log.d("TAG", "item with URI " + itemUri.toString() + " was successfully updated");

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


				} else {

					Toast.makeText(EditItemDetailActivity.this, "Permission deny to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}
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

//	@Override
//	protected void onDestroy(){
//		if(myTTS != null) {
//
//			myTTS.stop();
//			myTTS.shutdown();
//		}
//		super.onDestroy();
//	}
//
//	private void speakName(String name){
//		myTTS.setSpeechRate(0.75f);
//		myTTS.speak(name, TextToSpeech.QUEUE_FLUSH, null);
//	}

	private void loadBackdrop(int photo) {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
			.load(photo)
			.centerCrop()
			.into(imageView);
	}

	private void loadBackdropFromUri(Uri photoUri) {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
				.load(photoUri)
				.centerCrop()
				.into(imageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_detail_fragment, menu);
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

//	private void sendPicToDb(Uri picUri){
//		ContentValues cv = new ContentValues();
//		cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, picUri.toString());
//		int updated = getContentResolver().update(itemUri,cv,null,null);
//		Log.d("TAG", "updated = " + updated);
//	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == MY_DATA_CHECK_CODE) {
//			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//				myTTS = new TextToSpeech(this, this);
//			}
//			else {
//				Intent installTTSIntent = new Intent();
//				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//				startActivity(installTTSIntent);
//			}
//		}
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				Uri takenPictureUri = data.getData();
				Toast.makeText(this,"takenPictureUri is " + takenPictureUri.toString(),Toast.LENGTH_LONG);
				//sendPicToDb(takenPictureUri);
				//mImageBitmap = (Bitmap) extras.get("data");
				//ContentValues cv = new ContentValues();
				//cv.put(ItemsContract.ItemsEntry.COLUMN_EXTRA_PIC_URI_1,takenPictureUri.toString());
				//getContentResolver().update(itemUri, cv, null, null);
				//mCursor = getContentResolver().query(itemUri,new String[]{ItemsContract.ItemsEntry.COLUMN_EXTRA_PIC_URI_1},null,null,null);
				//Cursor c = getContentResolver().query(itemUri,null,null,null,null);
				//c.moveToFirst();
				//String uriString = 	c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
				//mCursor.close();
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

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//
//		// save file url in bundle as it will be null on scren orientation
//		// changes
//		outState.putParcelable("item_uri", itemUri);
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//
//		// get the file url
//		itemUri = savedInstanceState.getParcelable("item_uri");
//	}


}
