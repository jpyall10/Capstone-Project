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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.android.project7.data.ItemsContract;

public class ItemsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
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


	public static Fragment newInstance(String category) {
		Bundle args = new Bundle();
		//args.putLong(ARG_ITEM_ID, id);
		args.putString(ARG_ITEM_CATEGORY, category);
		ItemsGridFragment fragment = new ItemsGridFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public interface Callback {
		public void onItemSelected(Uri idUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh);
	}

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
//		switch(id){
//			case R.string.edit_list:
//
//				break;
//			default:
//		}
		return true;
	}

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
				//id = mCursor.getLong(COL_ITEM_ID);
				((Callback)getActivity()).onItemSelected(ItemsContract.ItemsEntry.buildItemUri(id),vh);
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

	private List<String> getSublistByCategory(String[] array, String category){
		ArrayList<String> list = new ArrayList<>();
		for (String s : array){
			list.add(s);
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

		return new CursorLoader(getActivity(),
				allItemsByCategory,
				ITEM_COLUMNS,
				selection,
				selectionArgs,
				sortOrder);
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		if (data == null || !data.moveToFirst()) {
			insertDummyData();
		}
		mItemsGridAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		mItemsGridAdapter.swapCursor(null);
	}

	private void insertDummyData() {
			ContentValues cv = new ContentValues();
			Uri uri;
			ArrayList<Item> starterItems = new ArrayList<Item>(){};

			starterItems.add(new Item(getContext(),getString(R.string.animal_label_cat),R.drawable.cat_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(),getString(R.string.animal_label_cow),R.drawable.cow_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_dog), R.drawable.dog_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_owl), R.drawable.owl_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_elephant), R.drawable.elephant_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_lion), R.drawable.lion_1, getString(R.string.category_animals)));
			starterItems.add(new Item(getContext(), getString(R.string.animal_label_squirrel), R.drawable.squirrel_1, getString(R.string.category_animals)));

			starterItems.add(new Item(getContext(), getString(R.string.person_name_mom), R.drawable.mom_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_dad), R.drawable.dad_1, getString(R.string.category_people)));

			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_bones), R.drawable.grandpa_bones_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_amy), R.drawable.grandma_amy_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandpa_porter), R.drawable.grandpa_porter_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_grandma_carmen), R.drawable.grandma_carmen_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_tone), R.drawable.uncle_tone_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_uncle_vinnie), R.drawable.uncle_vinnie_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_mel), R.drawable.aunt_mel_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_rachel), R.drawable.cousin_rachel_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_porter), R.drawable.cousin_porter_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_cousin_sophia), R.drawable.cousin_sophia_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_pam), R.drawable.aunt_pam_1, getString(R.string.category_people)));
			starterItems.add(new Item(getContext(), getString(R.string.person_name_aunt_debi), R.drawable.aunt_debi_1, getString(R.string.category_people)));

			starterItems.add(new Item(getContext(), getString(R.string.food_name_apple), R.drawable.apple_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_banana), R.drawable.banana_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_brocolli), R.drawable.broccoli_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_grapes), R.drawable.grapes_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_oranges), R.drawable.oranges_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_peas), R.drawable.peas_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_strawberries), R.drawable.strawberries_1, getString(R.string.category_food)));
			starterItems.add(new Item(getContext(), getString(R.string.food_name_tomatoes), R.drawable.tomatoes_1, getString(R.string.category_food)));

			for (Item item : starterItems){
				cv.put(ItemsContract.ItemsEntry.COLUMN_NAME, item.getName());
				cv.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, item.getCategory());
				cv.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, item.getPhoto());
				Uri itemUri = getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);
				mItemsGridAdapter.notifyDataSetChanged();

				Log.d(LOG_TAG, "Added item with name " + item.getName() + " uri = " + itemUri.toString());
			}
		}
	}

