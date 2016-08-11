package com.example.android.project7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.io.File;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class ItemsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private final String LOG_TAG = this.getClass().getSimpleName();

	private static final String ARG_ITEM_CATEGORY = "category";

	private RecyclerView mRecyclerView;
	private Cursor mCursor;
	private long mItemId;
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


	private static final int ITEMS_LOADER = 0;

	private static final String[] ITEM_COLUMNS = {
			ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID,
			ItemsContract.ItemsEntry.COLUMN_NAME,
			ItemsContract.ItemsEntry.COLUMN_CATEGORY,
			ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID,
			ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1
	};

	static final int COL_ITEM_ID = 0;
	static final int COL_ITEM_NAME = 1;
	static final int COL_ITEM_CATEGORY = 2;
	static final int COL_ITEM_PHOTO = 3;
	static final int COL_PHOTO_EXTRA_1 = 4;


	public static Fragment newInstance(String category) {
		Bundle args = new Bundle();
		//args.putLong(ARG_ITEM_ID, id);
		args.putString(ARG_ITEM_CATEGORY, category);
		ItemsGridFragment fragment = new ItemsGridFragment();
		fragment.setArguments(args);
		return fragment;
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

	public interface Callback {
		public void onItemSelected(Uri idUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh);
		public void onItemLongSelected(Long id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		Fresco.initialize(this.getActivity());
		MobileAds.initialize(getActivity().getApplicationContext(), "ca-app-pub-3364537753375699~3862655769");
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
		//mInterstitialAd.setAdUnitId("ca-app-pub-3364537753375699/5339388964");


		//Test Ads
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

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
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.itemsgridfragment, menu);
		if(getEditMode()){
			menu.getItem(0).setIcon(R.drawable.ic_create_24dp_accent);
		}else{
			menu.getItem(0).setIcon(R.drawable.ic_create_24dp);
		}
	}

	private String[] getNames(){
		Cursor c = getActivity().getContentResolver().query(
				ItemsContract.ItemsEntry.CONTENT_URI,
				new String[]{ItemsContract.ItemsEntry.COLUMN_NAME},
				null,null,null,null);
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		//super.onOptionsItemSelected(item);
		int id = item.getItemId();
		switch(id){
			case android.R.id.home:
				MainActivity.openDrawerLayout();
				break;
//			case R.id.add_item:
//				if(getEditMode()) {
//					addItem();
//				}else{
//					Toast toast = Toast.makeText(this.getActivity(),"You must be in edit mode to add info",Toast.LENGTH_LONG);
//					toast.show();
//				}
//				break;
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
//							ItemsGridFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						}
					});
					//item.setTitle(getString(R.string.edit_mode_on));
				}else{
					Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_off),Toast.LENGTH_LONG);
					toast.show();
					item.setIcon(R.drawable.ic_create_24dp);
					MainActivity.mFab.setVisibility(View.GONE);
					//item.setTitle(getString(R.string.edit_mode_off));
				}
				break;
			default:
		}
		return true;
	}

	private void loadPreviewImage(String path) {
		File file = new File(path);
//		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

		Glide.with(this)
				.load(path)
				.fitCenter()
				.into(mPreviewImage);
	}
//	@Override
//	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState){
//		super.onInflate(activity,attrs,savedInstanceState);
//	}

//    public void ptivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//			Uri selectedImage = data.getData();
//			String[] filePathColumn = { MediaStore.Images.Media.DATA };
//			Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
//			cursor.moveToFirst();
//			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//			String picturePath = cursor.getString(columnIndex);
//			cursor.close();
////			ImageView imageView = (ImageView) findViewById(R.id.imgView);
////			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//		}
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_grid, container, false);

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
		mItemsGridAdapter = new ItemsGridAdapter(getActivity(),new ItemsGridAdapter.ItemsGridAdapterOnClickHandler(){

			@Override
			public void onClick(Long id, ItemsGridAdapter.ItemsGridAdapterViewHolder vh) {
				//id = mCursor.getLong(COL_ITEM_ID);
				((Callback)getActivity()).onItemSelected(ItemsContract.ItemsEntry.buildItemUri(id),vh);
			}

			@Override
			public void onLongClick(Long id) {
				((Callback)getActivity()).onItemLongSelected(id);
			}


		});

		//mRecyclerView.setHasFixedSize(true);
		//mItemsGridAdapter.setHasStableIds(true);

		mRecyclerView.setAdapter(mItemsGridAdapter);
		//mItemsGridAdapter.setHasStableIds(true);
		int numColumns = getResources().getInteger(R.integer.staggered_grid_columns);
		StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL);
		sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
		//LinearLayoutManager sglm = new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false);
		mRecyclerView.setLayoutManager(sglm);
		return rootView;
	}

	public void addItem() {
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		final ContentValues cv = new ContentValues();

		AlertDialog.Builder b = new AlertDialog.Builder(ItemsGridFragment.this.getActivity());
		b.setTitle("Add Item");

		mLayout = new LinearLayout(ItemsGridFragment.this.getContext());
		mLayout.setOrientation(LinearLayout.VERTICAL);
//				final LinearLayout layout = new LinearLayout(ItemsGridFragment.this.getContext());
//				layout.setOrientation(LinearLayout.VERTICAL);

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
//							final String photoUriString = getGetPhotoUriString();
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
//							final String photoUriString = getTakePhotoUriString();
			}
		});

//					final ImageButton enterUrlButton = new ImageButton(this.getActivity());
//					enterUrlButton.setImageResource(R.drawable.ic_attachment_24dp);
//					enterUrlButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//
//				enterUrlButton.setOnClickListener(new View.OnClickListener() {
//					int count = 0;
//
//					@Override
//					public void onClick(View v) {
////						if (count <= 0) {
////							EditText enterUrlBox = new EditText(ItemsGridFragment.this.getActivity());
////							enterUrlBox
////							mPhotoUrlBox.setHint("Enter a photo URL");
//							//mLayout.addView(mPhotoUrlBox);
////						}
////						count++;
//
//					}
//				});



		mPreviewImage= new ImageView(ItemsGridFragment.this.getContext());
		int pixels = (int) dipToPixels(ItemsGridFragment.this.getContext(), 200);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		mPreviewImage.setLayoutParams(params);

		//Initialize photoUrlBox and setText or hint
//				String photoUriString = mPhotoUrlBox.getText().toString();
//				if(photoUriString != null && !photoUriString.equals("")) {
//					mPhotoUrlBox.setText(photoUriString);
//					loadPreviewImage(photoUriString);
//				}else{
//					mPhotoUrlBox.setHint("Enter photo URL");
//				}

		buttonLayout.addView(getPhotoButton);
		buttonLayout.addView(takePhotoButton);
		//buttonLayout.addView(enterUrlButton);

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
		//mLayout.addView(mPhotoUrlBox);
		//mLayout.addView(mPreviewImage);
		mLayout.addView(buttonLayout);
		mLayout.addView(mPhotoUrlBox);
		mLayout.addView(mPreviewImage);

		b.setView(mLayout);

		b.setPositiveButton("ADD", null);/*new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});*/
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

//		class CustomListener implements View.OnClickListener {
//			private final Dialog dialog;
//			public CustomListener(Dialog dialog) {
//				this.dialog = dialog;
//			}
//			@Override
//			public void onClick(View v) {
//				// put your code here
//				String categoryText = categoryBox.getText().toString();
//				if(categoryText != null && !categoryText.equals("")){
//					dialog.dismiss();
//				}else{
//					Toast.makeText(ItemsGridFragment.this.getActivity(), "Invalid data", Toast.LENGTH_SHORT).show();
//				}
//			}
//		}

		Button button = d.getButton(AlertDialog.BUTTON_POSITIVE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String category = categoryBox.getText().toString();
				if(!category.equals("")){
					//d.dismiss();
					String name = nameBox.getText().toString();
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
						//cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.v_face);
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
					if (mInterstitialAd.isLoaded()) {
						mInterstitialAd.show();
					}
					addItem();
//					ItemsGridFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					requestNewInterstitial();
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
		if (!data.moveToFirst() && (mCategory == getString(R.string.category_animals) ||
				mCategory==getString(R.string.category_food) || mCategory == getString(R.string.category_people))){
			insertStarterData();
			Log.d("TAG", "insertStarterData ran");
		}
		mItemsGridAdapter.swapCursor(data);


	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		mItemsGridAdapter.swapCursor(null);
	}

	private void insertStarterData() {
		ContentValues cv = new ContentValues();
		ArrayList<Item> starterItems = new ArrayList<Item>(){};

		starterItems.add(new Item(getContext(),getString(R.string.animal_label_cat),getString(R.string.android_resource_uri_base) + R.drawable.cat_1, getString(R.string.category_animals)));
				starterItems.add(new Item(getContext(), getString(R.string.animal_label_cow), getString(R.string.android_resource_uri_base) + R.drawable.cow_1, getString(R.string.category_animals)));
		starterItems.add(new Item(getContext(), getString(R.string.animal_label_dog), getString(R.string.android_resource_uri_base) + R.drawable.dog_1, getString(R.string.category_animals)));
		starterItems.add(new Item(getContext(), getString(R.string.animal_label_owl), getString(R.string.android_resource_uri_base) + R.drawable.owl_1, getString(R.string.category_animals)));
		starterItems.add(new Item(getContext(), getString(R.string.animal_label_elephant), getString(R.string.android_resource_uri_base) + R.drawable.elephant_1, getString(R.string.category_animals)));
		starterItems.add(new Item(getContext(), getString(R.string.animal_label_lion), getString(R.string.android_resource_uri_base) + R.drawable.lion_1, getString(R.string.category_animals)));
		starterItems.add(new Item(getContext(), getString(R.string.animal_label_squirrel), getString(R.string.android_resource_uri_base) + R.drawable.squirrel_1, getString(R.string.category_animals)));

		starterItems.add(new Item(getContext(), getString(R.string.person_name_mom), getString(R.string.android_resource_uri_base) + R.drawable.mom_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_dad), getString(R.string.android_resource_uri_base) + R.drawable.dad_1, getString(R.string.category_people)));

		starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_bones), getString(R.string.android_resource_uri_base) + R.drawable.grandpa_bones_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_amy), getString(R.string.android_resource_uri_base) + R.drawable.grandma_amy_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_porter), getString(R.string.android_resource_uri_base) + R.drawable.grandpa_porter_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_carmen), getString(R.string.android_resource_uri_base) + R.drawable.grandma_carmen_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_tone), getString(R.string.android_resource_uri_base) + R.drawable.uncle_tone_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_vinnie), getString(R.string.android_resource_uri_base) + R.drawable.uncle_vinnie_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_mel), getString(R.string.android_resource_uri_base) + R.drawable.aunt_mel_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_rachel), getString(R.string.android_resource_uri_base) + R.drawable.cousin_rachel_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_porter), getString(R.string.android_resource_uri_base) + R.drawable.cousin_porter_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_sophia), getString(R.string.android_resource_uri_base) + R.drawable.cousin_sophia_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_pam), getString(R.string.android_resource_uri_base) + R.drawable.aunt_pam_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_debi), getString(R.string.android_resource_uri_base) + R.drawable.aunt_debi_1, getString(R.string.category_people)));
		starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_punkin), getString(R.string.android_resource_uri_base) + R.drawable.aunt_punkin_1, getString(R.string.category_people)));


		starterItems.add(new Item(getContext(), getString(R.string.food_name_apple), getString(R.string.android_resource_uri_base) + R.drawable.apple_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_banana), getString(R.string.android_resource_uri_base) + R.drawable.banana_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_brocolli), getString(R.string.android_resource_uri_base) + R.drawable.broccoli_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_grapes), getString(R.string.android_resource_uri_base) + R.drawable.grapes_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_oranges), getString(R.string.android_resource_uri_base) + R.drawable.oranges_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_peas), getString(R.string.android_resource_uri_base) + R.drawable.peas_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_strawberries), getString(R.string.android_resource_uri_base) + R.drawable.strawberries_1, getString(R.string.category_food)));
		starterItems.add(new Item(getContext(), getString(R.string.food_name_tomatoes), getString(R.string.android_resource_uri_base) + R.drawable.tomatoes_1, getString(R.string.category_food)));

		for (Item item : starterItems){
			cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, item.getName().toLowerCase());
			cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, item.getCategory().toLowerCase());
//			cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, item.getPhoto());
			cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, item.getPhotoUriString());
			Uri itemUri = getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);
			mItemsGridAdapter.notifyDataSetChanged();

			Log.d(LOG_TAG, "Added item with name " + item.getName() + " uri = " + itemUri.toString());
		}
	}

//	private void sendPicToDb(Uri picUri){
//		ContentValues cv = new ContentValues();
//		cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1, picUri.toString());
//		int updated = getActivity().getContentResolver().update(itemUri,cv,null,null);
//
//		Log.d("TAG", "updated = " + updated);
//	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == this.getActivity().RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathCol = {MediaStore.Images.Media.DATA};
			Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathCol, null, null, null);
			cursor.moveToFirst();
			String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
			setGetPhotoUriString(picPath);
			if(picPath !=null && !picPath.equals("")){
				loadPreviewImage(picPath);
//				try {
					mPhotoUrlBox.setText(picPath);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);
//				}catch (Exception e){
//					e.printStackTrace();
//				}
			}
			Log.d("IGF", "getGetPhotoUriString = " + selectedImage.toString());
		}
		if (requestCode == REQUEST_IMAGE_CAPTURE){
			if (resultCode == this.getActivity().RESULT_OK){
				Bundle extras = data.getExtras();
				Uri takenPictureUri = data.getData();
				String[] filePathCol = {MediaStore.Images.Media.DATA};
				Cursor cursor = this.getActivity().getContentResolver().query(takenPictureUri, filePathCol, null, null, null);
				cursor.moveToFirst();
				String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
				setTakePhotoUriString(picPath);
				if(picPath !=null && !picPath.equals("")){
					loadPreviewImage(picPath);
					mPhotoUrlBox.setText(picPath);
					mLayout.addView(mPhotoUrlBox);
					mLayout.addView(mPreviewImage);
				}
			}
		}
	}
}

