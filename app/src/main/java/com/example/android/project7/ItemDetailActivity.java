package com.example.android.project7;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
	private static final int RESULT_LOAD_IMAGE = 3;
//	public static final String EXTRA_NAME = "item_name";
//	public static final String EXTRA_PHOTO = "photo_id";
//	public static final String EXTRA_DESCRIPTION = "description";

	private ViewPager mViewPager;
	private Adapter mAdapter;

	private ImageView mPreviewImage;
	private LinearLayout mLayout;
	private EditText mPhotoUrlBox;

	private  String mName;
	private  String mPhotoUriString;

	private FloatingActionButton fab;
	private TextToSpeech myTTS;

	private Uri itemUri;
	private Uri mCardsUri;
	private Cursor mCursor;

	private CollapsingToolbarLayout mCollapsingToolbar;
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
		mName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));//intent.getStringExtra(EXTRA_NAME);
		mPhotoUriString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
		//final String description = intent.getStringExtra(EXTRA_DESCRIPTION);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		mCollapsingToolbar.setTitle(mName);

		//final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

		fab = (FloatingActionButton)findViewById(R.id.fab);
		//fab.setImageURI(Uri.parse("android.resource://com.example.android.project7/R.drawable.ic_add_24dp"));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				readText(mName);
//				mp.start();
			}
		});

		ActivityCompat.requestPermissions(ItemDetailActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				1);

		if (mPhotoUriString == null || mPhotoUriString.equals("")){
			mPhotoUriString = getString(R.string.android_resource_uri_base) + R.drawable.v_face;
		} else if (mPhotoUriString != null && !mPhotoUriString.equals("")){
			loadBackdrop(mPhotoUriString);
		}


		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		setupViewPager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		//super.onOptionsItemSelected(menuItem);
		switch(menuItem.getItemId()){
			case android.R.id.home:
				this.onBackPressed();
				break;
//			case R.id.add_card:
//				final ContentValues cv = new ContentValues();
//
//				AlertDialog.Builder b = new AlertDialog.Builder(this);
//				b.setTitle("Add");
//
//				final LinearLayout layout = new LinearLayout(this);
//				layout.setOrientation(LinearLayout.VERTICAL);
//
//
//				final EditText titleBox = new EditText(this);
//				titleBox.setHint("Title");
//				layout.addView(titleBox);
//
//
//				final EditText descriptionBox = new EditText(this);
//				descriptionBox.setHint("Description");
//				layout.addView(descriptionBox);
//
//				final EditText photoUrlBox = new EditText(this);
//				photoUrlBox.setHint("Enter a photo URL");
//				layout.addView(photoUrlBox);
//
//				b.setView(layout);
//
//				b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int whichButton) {
//						String title = titleBox.getText().toString();
//						String description = descriptionBox.getText().toString();
//						String photoUrl = photoUrlBox.getText().toString();
//						cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL, title);
//						cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION, description);
//						cv.put(ItemsContract.CardsEntry.COLUMN_ITEM_KEY, );
//						if(photoUrl.length() <= 0){
//							cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, "");
//						}else{
//							cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, photoUrl);
//						}
//						Uri itemUri = ItemsContract.ItemsEntry.buildItemUri(mItemId);
//						Uri cardUri = getActivity().getContentResolver().insert(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), cv);
//						mItemDetailAdapter.notifyDataSetChanged();
//
//						Log.d(LOG_TAG, "inserted: cardUri = " + cardUri);
//					}
//				});
//				b.setNegativeButton("CANCEL", null);
//				b.create().show();
//				break;
			case R.id.edit_item:
				boolean editMode = getEditMode();
				if(editMode) {
					final ContentValues cvEdit = new ContentValues();
//					final Cursor c = getContentResolver().query(
//							itemUri,
//							new String[]{ItemsContract.ItemsEntry.COLUMN_NAME, ItemsContract.ItemsEntry.COLUMN_CATEGORY, ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1}, null, null, null);
					AlertDialog.Builder bEdit = new AlertDialog.Builder(this);
					bEdit.setTitle("Edit Item");

					mLayout = new LinearLayout(this);
					mLayout.setOrientation(LinearLayout.VERTICAL);

//					c.moveToFirst();
					mCursor.moveToFirst();
//					String currentName = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
					String currentName = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
					final EditText editNameBox = new EditText(this);
					editNameBox.setText(currentName);
					mLayout.addView(editNameBox);

//					String currentCategory = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
					String currentCategory = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
					final EditText editCategoryBox = new EditText(this);
					editCategoryBox.setText(currentCategory);
					mLayout.addView(editCategoryBox);

					String currentPicUriString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));

					mPreviewImage = new ImageView(this);
					int pixels = (int)dipToPixels(this,200);
					LinearLayout.LayoutParams params =
							new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
//					params.weight = 1.0f;
					params.gravity = Gravity.CENTER;
//					mPreviewImage.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT));
					mPreviewImage.setLayoutParams(params);

					mPhotoUrlBox = new EditText(this);
					if(currentPicUriString != null && !currentPicUriString.equals("")) {
						mPhotoUrlBox.setText(currentPicUriString);
						loadPreviewImage(currentPicUriString);
					}else{
						mPhotoUrlBox.setHint("Enter photo URL");
					}

					final LinearLayout editPictureButtons = new LinearLayout(this);
					editPictureButtons.setOrientation(LinearLayout.HORIZONTAL);
					//
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

							//run when return from intent
//							mCursor.moveToFirst();
//							String photoUriString = c.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
//							mPhotoUrlBox.setText(photoUriString);
							//loadPreviewImage(photoUriString);
//							mLayout.addView(mPreviewImage);
						}
					});
					//
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
					//


					//mCursor.close();;

					editPictureButtons.addView(getPhotoButton);
					editPictureButtons.addView(takeNewPhotoButton);

					mLayout.addView(editPictureButtons);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);


					bEdit.setView(mLayout);

					bEdit.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							String name = editNameBox.getText().toString();
							String category = editCategoryBox.getText().toString();
							String photoUrl = mPhotoUrlBox.getText().toString();
							//sendPicToDb(photoUrl);
							cvEdit.put(ItemsContract.ItemsEntry.COLUMN_NAME, name);
							cvEdit.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, category);

							if (photoUrl.length() <= 0){
								photoUrl = getString(R.string.android_resource_uri_base) + R.drawable.v_face;
							}
							cvEdit.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, photoUrl);

//							if(photoUrl.length() <= 0) {
//								Log.d(LOG_TAG, "photoUrl is " + photoUrl);
//								if (getGetPhotoUriString() != null && !getGetPhotoUriString().equals("")){
//									photoUrl = getGetPhotoUriString();
//									Log.d(LOG_TAG, "inside if photoUrl is " + photoUrl);
//								}else if (getTakePhotoUriString() != null && !getTakePhotoUriString().equals("")){
//									photoUrl = getTakePhotoUriString();
//									Log.d(LOG_TAG, "inside else photoUrl is " + photoUrl);
//
//								}
//								//cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.v_face);
//							}
							Log.d("IGF", "photoUrl is " + photoUrl);

							int rowsUpdated = getContentResolver().
									update(itemUri, cvEdit, null, null);

//							if(rowsUpdated > 0){
//								ItemDetailActivity.updateItemInfo(name, photoUrl);
//							}

							mCursor = getContentResolver().query(itemUri,null,null,null,null);
							loadBackdrop(photoUrl);
							mCollapsingToolbar.setTitle(name);
							MainActivity.addTab(category);

							Log.d("IDA", "updatedRows = " + rowsUpdated);
						}
					});
					bEdit.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					bEdit.create().show();
				}
				break;
			case R.id.edit_mode:
				if(getEditMode()){
					toggleEditMode();
					menuItem.setTitle(R.string.edit_mode_off);
				}else{
					toggleEditMode();
					menuItem.setTitle(R.string.edit_mode_on);
				}
				break;
			default:
		}
		return false;
	}






//	public static void updateItemInfo(String name, String photoUriString){
//		mName = name;
//		mPhotoUriString = photoUriString;
//		mCollapsingToolbar.setTitle(mName);
//		//onBackdropChanged();
//	}
	private void setupViewPager(){
		mAdapter = new Adapter(getSupportFragmentManager());
		if (itemUri != null){
			Long id = ItemsContract.ItemsEntry.getIdFromUri(itemUri);
			mAdapter.addFragment(ItemDetailFragment.newInstance(id));
		}
		mViewPager.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
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
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
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
		File file = new File(path);
//		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		Glide.with(this)
				.load(path)
				.fitCenter()
				.into(mPreviewImage);
	}

	public void toggleEditMode(){
		mEditMode = !mEditMode;
	}

	public boolean getEditMode(){return mEditMode;}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS){
			myTTS.setLanguage(Locale.US);
		}else if(status == TextToSpeech.ERROR){
			Toast.makeText(this, "Sorry! Text to Speech failed", Toast.LENGTH_LONG).show();
		}
	}

	private void sendPicToDb(String picUriString){
		ContentValues cv = new ContentValues();
		cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, picUriString);
		int updated = getContentResolver().update(itemUri,cv,null,null);

		Log.d("TAG", "updated = " + updated);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
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
				Log.d("IDA", "pic path is " + picPath);
				//setGetPhotoUriString(picPath);
				//mAdapter.notifyDataSetChanged();
				//mCursor = getContentResolver().query(itemUri,null,null,null,null);
				//mCursor.moveToFirst();
				//String uriString = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
				//if(uriString !=null || !uriString.equals("")){
				if(picPath !=null && !picPath.equals("")){
					//loadBackdrop(selectedImage);
					loadPreviewImage(picPath);
					mPhotoUrlBox.setText(picPath);
					//loadPreviewImage(photoUriString);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);
				}
			}
		}
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				Uri takenPictureUri = data.getData();
				String[] filePathCol = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(takenPictureUri, filePathCol, null, null, null);
				cursor.moveToFirst();
				String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
//				setGetPhotoUriString(picPath);
				//sendPicToDb(picPath);
//				mCursor.moveToFirst();
//				String uriString = 	mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
//				if(uriString != null){
//					loadBackdrop(uriString);
//				}
				if(picPath !=null && !picPath.equals("")){
					//loadBackdrop(selectedImage);
					loadPreviewImage(picPath);
					mPhotoUrlBox.setText(picPath);
					//loadPreviewImage(photoUriString);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);
				}
			}
		}
	}

	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}
	public void onTakePhotoClick(View view) {

		if (mEditMode == true){
			takePicture();
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
		Log.d("IDA", "item uri = " + itemUri.toString());

		final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), id);
		Cursor c = getContentResolver().query(myContentUri,
				new String[]{ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL, ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION},
				null, null, null);
		c.moveToFirst();
		final String title = c.getString(c.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL));
		Log.d("IDA", "title = " + title);
		if (title != null && title != ""){

			final String description = c.getString(1);
			readText(title + description);
		}
		c.close();

	}

	@Override
	public void onItemLongSelected(Long id) {
		//edit details of card when in edit mode
		//else do nothing
		AlertDialog.Builder b = new AlertDialog.Builder(ItemDetailActivity.this);
//        b.setTitle("Please enter a category");
		b.setMessage("Do you want to delete this card?");
		final Uri myContentUri = ContentUris.withAppendedId(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), id);
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

	@Override
	public void onBackdropChanged(String uriString) {
		loadBackdrop(uriString);
	}


}
