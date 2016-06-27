package com.example.android.project7;

import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.android.project7.data.ItemsContract;

public class ItemGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private final String LOG_TAG = this.getClass().getSimpleName();

	private static final String ARG_ITEM_ID = "_id";
	private static final String ARG_ITEM_CATEGORY = "category";

	private RecyclerView mRecyclerView;
	private Cursor mCursor;
	private long mItemId;
	private String mCategory;


	private ItemsGridAdapter mItemsGridAdapter;

	//private static final int CURSOR_LOADER_ID = 0;

	private static final String SELECTED_KEY = "selected_position";

	private static final int ITEMS_LOADER = 0;

	private static final String[] ITEM_COLUMNS = {
			ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID,
			ItemsContract.ItemsEntry.COLUMN_NAME,
			ItemsContract.ItemsEntry.COLUMN_CATEGORY,
			ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID
	};

	static final int COL_ITEM_ID = 0;
	static final int COL_ITEM_NAME = 1;
	static final int COL_ITEM_CATEGORY = 2;
	static final int COL_ITEM_PHOTO = 3;

	ArrayList<Item> starterItems = new ArrayList<Item>(){};

	public static Fragment newInstance(String category) {
		Bundle args = new Bundle();
		//args.putLong(ARG_ITEM_ID, id);
		args.putString(ARG_ITEM_CATEGORY, category);
		ItemGridFragment fragment = new ItemGridFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public interface Callback {
		public void onItemSelected(Uri idUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh);
	}

//	public ItemGridFragment(){
//		//init();
//	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle args = getArguments();
		if (args != null){
			mCategory = args.getString(ARG_ITEM_CATEGORY);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		inflater.inflate(R.menu.itemsgridfragment, menu);
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item){
//		int id = item.getItemId();
//		return true;
//	}

//	@Override
//	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState){
//		super.onInflate(activity,attrs,savedInstanceState);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_grid, container, false);

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		final long mLong = 0;

		mItemsGridAdapter = new ItemsGridAdapter(getActivity(),new ItemsGridAdapter.ItemsGridAdapterOnClickHandler(){

			@Override
			public void onClick(Long id, ItemsGridAdapter.ItemsGridAdapterViewHolder vh) {
				long _id = mCursor.getLong(COL_ITEM_ID);
				((Callback)getActivity()).onItemSelected(ItemsContract.ItemsEntry.buildItemUri(_id),vh);
			}
		});

//		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mItemsGridAdapter);
		//setupRecyclerView(mRecyclerView);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		getLoaderManager().initLoader(ITEMS_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}
//		Cursor c =
//				getActivity().getContentResolver().query(
//						ItemsContract.ItemsEntry.CONTENT_URI,
//						new String[]{ItemsContract.ItemsEntry._ID},
//						null,
//						null,
//						null);
//		if (c.getCount() == 0){
//			//insertData();
//		}
//
//		getLoaderManager().initLoader(ITEMS_LOADER, null, this);
//	}

	//@Nullable


//	private void setupRecyclerView(RecyclerView recyclerView) {
//		recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
//		int categoryIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY);
//		String category = mCursor.getString(categoryIndex);
//		switch(category.toLowerCase()){
//			case "animals":
//				recyclerView.setAdapter(new ItemsGridAdapter(getActivity(),
//						getSublistByCategory(Item.sAnimals, getString(R.string.category_animals))));
//				break;
//			case "people":
//				recyclerView.setAdapter(new ItemsGridAdapter(getActivity(),
//						getSublistByCategory(Item.sPeople, getString(R.string.category_people))));
//				break;
//			case "food":
//				recyclerView.setAdapter(new ItemsGridAdapter(getActivity(),
//						getSublistByCategory(Item.sFoods, getString(R.string.category_food))));
//				break;
//			default:
//				recyclerView.setAdapter(new ItemsGridAdapter(getActivity(),
//						getRandomSublist(Item.sItemStrings, 30)));
//		}
//		recyclerView.setAdapter(new ItemsGridAdapter(this.getContext(), starterItems));
//	}

	private List<String> getSublistByCategory(String[] array, String category){
		ArrayList<String> list = new ArrayList<>();
		for (String s : array){
			list.add(s);
		}
		return list;
	}

	private List<String> getRandomSublist(String[] array, int amount) {
		ArrayList<String> list = new ArrayList<>(amount);
		Random random = new Random();
		while (list.size() < amount) {
			list.add(array[random.nextInt(array.length)]);
		}
		return list;
	}

	public void onSaveInstanceState(Bundle outState){
		//mItemsGridAdapter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

		String sortOrder = ItemsContract.ItemsEntry.COLUMN_NAME + " ASC";
		String selection;
		String[] selectionArgs;

		if(mCategory != null){
			selection = ItemsContract.ItemsEntry.COLUMN_CATEGORY + " = ?";
			selectionArgs = new String[]{mCategory};
		}
		else {
			selection = null;
			selectionArgs = null;
		}

		Uri allItemsByCategory = ItemsContract.ItemsEntry.CONTENT_URI;

//		if(mCursor != null){
//			mCategory = mCursor.getString(COL_ITEM_CATEGORY);
//		}else{
//			mCategory = null;
//		}
		//mCategory = mCursor == null ? "people" : mCursor.getString(COL_ITEM_CATEGORY);


		return new CursorLoader(getActivity(),
				allItemsByCategory,
				ITEM_COLUMNS,
				selection,
				selectionArgs,
				sortOrder);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		if (data == null || !data.moveToFirst()){
			ContentValues cv = new ContentValues();
			Uri uri;

			starterItems.add(new Item(getContext(),getString(R.string.animal_label_cat),R.drawable.cat_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(),getString(R.string.animal_label_cow),R.drawable.cow_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_dog), R.drawable.dog_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_owl), R.drawable.owl_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_mom), R.drawable.mom_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_dad), R.drawable.dad_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_bones), R.drawable.grandpa_bones_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_amy), R.drawable.grandma_amy_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_porter), R.drawable.grandpa_porter_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_carmen), R.drawable.grandma_carmen_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_tone), R.drawable.uncle_tone_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_vinnie), R.drawable.uncle_vinnie_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_mel), R.drawable.aunt_mel_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_apple), R.drawable.apple_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_banana), R.drawable.banana_1, getString(R.string.category_food)));

			for (Item item : starterItems){
				cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, item.getName());
				cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, item.getCategory());
				cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, item.getPhoto());
				Uri itemUri = getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);
				mItemsGridAdapter.notifyDataSetChanged();

				Log.d(LOG_TAG, "Added item with name " + item.getName() + " uri = " + itemUri.toString());
			}
			}else{
			mItemsGridAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		mItemsGridAdapter.swapCursor(null);
	}

//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//
//		mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//			@Override
//			public boolean onPreDraw() {
//				if (mRecyclerView.getChildCound() > 0){
//					mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
//					int position = mItemsGridAdapter.getSelectedItemPosition();
//					if (position == RecyclerView.NO_POSITION){
//						Cursor data = mItemsGridAdapter.getCursor();
//						int count = data.getCount();
//						int nameColumn = data.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME);
//						for (int i=0;i<count;i++){
//							data.moveToPosition(i);
//							if(data.getString(nameColumn).equals())
//						}
//					}
//				}
//				return false;
//			}
//		});


//	}

//	@Override
//	public void onLoaderReset(Loader<Cursor> loader) {
//		mItemsGridAdapter.swapCursor(null);
//	}
}
