package com.example.android.project7;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;

public class ItemsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private final String LOG_TAG = this.getClass().getSimpleName();

	private static final String ARG_ITEM_CATEGORY = "category";

	private RecyclerView mRecyclerView;
	private String mCategory;
	private static boolean mEditMode = false;

	public ItemsGridAdapter mItemsGridAdapter;

	private InterstitialAd mInterstitialAd;

	private String mGetPhotoUriString;
	private String mTakePhotoUriString;
	private EditText mPhotoUrlBox;
	private ImageView mPreviewImage;
	private static LinearLayout mLayout;

	private static final int RESULT_LOAD_IMAGE = 10;
	private static final int REQUEST_IMAGE_CAPTURE = 11;

	private static final int REQUEST_EXTERNAL_STORAGE = 99;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};


	private static final int ITEMS_LOADER = 0;

	private static final String[] ITEM_COLUMNS = {
			ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID,
			ItemsContract.ItemsEntry.COLUMN_NAME,
			ItemsContract.ItemsEntry.COLUMN_CATEGORY,
			ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID,
			ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1
	};

	public static Fragment newInstance(String category) {
		Bundle args = new Bundle();
		args.putString(ARG_ITEM_CATEGORY, category);
		ItemsGridFragment fragment = new ItemsGridFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public interface Callback {
		public void onItemSelected(Uri idUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh);
		public void onItemLongSelected(Long id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MobileAds.initialize(getActivity().getApplicationContext(), getString(R.string.ad_app_id));
		setHasOptionsMenu(true);
		Bundle args = getArguments();
		if (args != null){
			mCategory = args.getString(ARG_ITEM_CATEGORY);
		}else{
			mCategory = null;
		}

		mInterstitialAd = new InterstitialAd(this.getContext());
//		App ID: ca-app-pub-3364537753375699~3862655769
//		Ad unit ID: ca-app-pub-3364537753375699/5339388964

		//Real Ads
		mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));


		//Test Ads
//		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
			}
		});

		requestNewInterstitial();
	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("DEVICE_ID_EMULATOR") //("03157df319a82d3d")
				.build();
		mInterstitialAd.loadAd(adRequest);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_grid, container, false);

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

		mItemsGridAdapter = new ItemsGridAdapter(getActivity(),new ItemsGridAdapter.ItemsGridAdapterOnClickHandler(){

			@Override
			public void onClick(Long id, ItemsGridAdapter.ItemsGridAdapterViewHolder vh) {
				((Callback)getActivity()).onItemSelected(ItemsContract.ItemsEntry.buildItemUri(id),vh);
			}

			@Override
			public void onLongClick(Long id) {
				((Callback)getActivity()).onItemLongSelected(id);
			}


		});

		mRecyclerView.setAdapter(mItemsGridAdapter);

		int numColumns = getResources().getInteger(R.integer.staggered_grid_columns);
		StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL);
		sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
		mRecyclerView.setLayoutManager(sglm);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.itemsgridfragment, menu);
		if(getEditMode()){
			menu.getItem(0).setIcon(R.drawable.ic_create_24dp_accent);
		}else{
			menu.getItem(0).setIcon(R.drawable.ic_create_24dp);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		//super.onOptionsItemSelected(item);
		int id = item.getItemId();
		switch(id){
			case android.R.id.home:
				if(getEditMode()){
					MainActivity.openDrawerLayout();
				}else{
					Toast.makeText(this.getContext(),getString(R.string.open_drawer_warning), Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.edit_mode:
				toggleEditMode();
				if(getEditMode()){
					if (mInterstitialAd.isLoaded()) {
						mInterstitialAd.show();
					}
					requestNewInterstitial();
					Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_on),Toast.LENGTH_LONG);
					toast.show();
					item.setIcon(R.drawable.ic_create_24dp_accent);
					MainActivity.mFab.setVisibility(View.VISIBLE);
					MainActivity.mFab.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							addItem();
						}
					});
				}else{
					Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_off),Toast.LENGTH_LONG);
					toast.show();
					item.setIcon(R.drawable.ic_create_24dp);
					MainActivity.mFab.setVisibility(View.GONE);
				}
				break;
			default:
		}
		return true;
	}

	public static void toggleEditMode(){
		mEditMode = !mEditMode;
	}
	public static boolean getEditMode(){
		return mEditMode;
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

	private String[] getNames(){
		Cursor c = getActivity().getContentResolver().query(
				ItemsContract.ItemsEntry.CONTENT_URI,
				new String[]{ItemsContract.ItemsEntry.COLUMN_NAME},
				null, null, null, null);
		if(c != null) {
			String[] names = new String[c.getCount()];
			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				names[i] = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
				c.moveToNext();
			}
			c.close();
			return names;
		}
		else{
			return null;
		}
	}

	private ArrayList<String> getCategories(){
		Cursor c = getActivity().getContentResolver().query(
				ItemsContract.ItemsEntry.CONTENT_URI,
				new String[]{ItemsContract.ItemsEntry.COLUMN_CATEGORY},
				null, null, null, null);
		if(c != null) {
			ArrayList<String> categories = new ArrayList<>();
			c.moveToFirst();
			String category;
			for (int i = 0; i < c.getCount(); i++) {
				category = c.getString(c.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY));
				if (!categories.contains(category)){
					categories.add(category);
				}
				c.moveToNext();
			}
			c.close();
			return categories;
		}
		else{
			return null;
		}
	}

	private void loadPreviewImage(String path) {
		Glide.with(this)
				.load(path)
				.fitCenter()
				.into(mPreviewImage);
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

	public void addItem() {
		verifyStoragePermissions(ItemsGridFragment.this.getActivity());
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		final ContentValues cv = new ContentValues();

		AlertDialog.Builder b = new AlertDialog.Builder(ItemsGridFragment.this.getActivity());
		b.setTitle("Add Item");

		mLayout = new LinearLayout(ItemsGridFragment.this.getContext());
		mLayout.setOrientation(LinearLayout.VERTICAL);

		String[] names = getNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,names);


		final AutoCompleteTextView nameBox = new AutoCompleteTextView(ItemsGridFragment.this.getContext());
		nameBox.setAdapter(adapter);
		nameBox.setHint("Name");
		mLayout.addView(nameBox);

		ArrayList<String> categories = getCategories();
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,categories);


		final AutoCompleteTextView categoryBox = new AutoCompleteTextView(ItemsGridFragment.this.getContext());
		categoryBox.setHint("Category (Required)");
		categoryBox.setAdapter(adapter2);
		mLayout.addView(categoryBox);
		mPhotoUrlBox = new EditText(ItemsGridFragment.this.getContext());
		mPhotoUrlBox.setHint("Enter a photo URL");

		final LinearLayout buttonLayout = new LinearLayout(ItemsGridFragment.this.getContext());
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

		final ImageButton getPhotoButton = new ImageButton(this.getActivity());
		getPhotoButton.setImageResource(R.drawable.ic_folder_open_24dp);
		getPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		getPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLayout.removeView(mPreviewImage);
				mLayout.removeView(mPhotoUrlBox);
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}
		});

		final ImageButton takePhotoButton = new ImageButton(this.getActivity());
		takePhotoButton.setImageResource(R.drawable.ic_photo_camera_24dp);
		takePhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		takePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLayout.removeView(mPreviewImage);
				mLayout.removeView(mPhotoUrlBox);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
			}
		});

		mPreviewImage= new ImageView(ItemsGridFragment.this.getContext());
		int pixels = (int) dipToPixels(ItemsGridFragment.this.getContext(), 200);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		mPreviewImage.setLayoutParams(params);
		buttonLayout.addView(getPhotoButton);
		buttonLayout.addView(takePhotoButton);

		mPhotoUrlBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				String url = s.toString();
				loadPreviewImage(url);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d(LOG_TAG, "text changed = " + s);
				String url = s.toString();
				loadPreviewImage(url);
			}
		});
		mLayout.addView(buttonLayout);
		mLayout.addView(mPhotoUrlBox);
		mLayout.addView(mPreviewImage);

		b.setView(mLayout);

		b.setPositiveButton("ADD", null);
		b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			b.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
		}
		final AlertDialog d = b.create();
		d.show();

		Button button = d.getButton(AlertDialog.BUTTON_POSITIVE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String category = categoryBox.getText().toString();
				if(!category.equals("")){
					String name = nameBox.getText().toString().toLowerCase();
					String photoUrl = mPhotoUrlBox.getText().toString();

					if (name == null || name.equals("")) {
						name = getString(R.string.default_name);
					}
					cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, name);
					cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, category);
					if (photoUrl.length() <= 0) {
						Log.d(LOG_TAG, "photoUrl is " + photoUrl);
						if (getGetPhotoUriString() != null && !getGetPhotoUriString().equals("")) {
							photoUrl = getGetPhotoUriString();
						} else if (getTakePhotoUriString() != null && !getTakePhotoUriString().equals("")) {
							photoUrl = getTakePhotoUriString();
						}
					}
					Log.d("IGF", "photoUrl is " + photoUrl);
					cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, photoUrl);

					Uri itemUri = getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);

					mItemsGridAdapter.notifyDataSetChanged();

					MainActivity.addTab(category);

					Log.d(LOG_TAG, "inserted: itemUri = " + itemUri);
					d.dismiss();
					ItemsGridFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}else{
					Toast.makeText(ItemsGridFragment.this.getActivity(), getString(R.string.enter_category_warning), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public static float dipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(ITEMS_LOADER, null, this);
	}

	@Override
	public void onResume(){
		super.onResume();
		if(getEditMode()){
			mEditMode = true;
			MainActivity.mFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					addItem();

				}
			});
		}else{
			mEditMode = false;
		}
	}


	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}


	/**
	 * LOADER METHODS
	 * **/
	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

		String sortOrder = ItemsContract.ItemsEntry.COLUMN_NAME + " ASC";
		String selection;
		String[] selectionArgs;

		if(mCategory != null){
			selection = ItemsContract.ItemsEntry.COLUMN_CATEGORY + " = ?";
			selectionArgs = new String[]{mCategory.toLowerCase()};
		}
		else {
			selection = null;
			selectionArgs = null;
		}

		Uri allItems = ItemsContract.ItemsEntry.CONTENT_URI;

		return new CursorLoader(getActivity(),
				allItems,
				ITEM_COLUMNS,
				selection,
				selectionArgs,
				sortOrder);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		mItemsGridAdapter.notifyDataSetChanged();
		mItemsGridAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		mItemsGridAdapter.swapCursor(null);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == this.getActivity().RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathCol = {MediaStore.Images.Media.DATA};
			Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathCol, null, null, null);
			cursor.moveToFirst();
			String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
			if(picPath == null || picPath.equals("")){
				Toast.makeText(this.getContext(),getString(R.string.get_file_failed_warning), Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG, "getPath = " + selectedImage.getPath() + " toString = " + selectedImage.toString());

				//picPath = selectedImage.getPath();
			}else {
				setGetPhotoUriString(picPath);
				if (picPath != null && !picPath.equals("")) {
					loadPreviewImage(picPath);
					mPhotoUrlBox.setText(picPath);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);
				}
				Log.d("IGF", "getGetPhotoUriString = " + selectedImage.toString());
			}
		}
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == this.getActivity().RESULT_OK && null !=data){
				try {
					Bundle extras = data.getExtras();
					Uri takenPictureUri = data.getData();
					String[] filePathCol = {MediaStore.Images.Media.DATA};
					Cursor cursor = this.getActivity().getContentResolver().query(takenPictureUri, filePathCol, null, null, null);
					cursor.moveToFirst();
					String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
					if (picPath == null || picPath.equals("")) {
						Toast.makeText(this.getContext(), getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
						Log.d(LOG_TAG, "getPath = " + takenPictureUri.getPath() + " toString = " + takenPictureUri.toString());
					} else {
						setTakePhotoUriString(picPath);
						if (picPath != null && !picPath.equals("")) {
							loadPreviewImage(picPath);
							mPhotoUrlBox.setText(picPath);
							mLayout.addView(mPhotoUrlBox);
							mLayout.addView(mPreviewImage);
						}
					}
				}catch (Exception e){
					Toast.makeText(this.getContext(), getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}

