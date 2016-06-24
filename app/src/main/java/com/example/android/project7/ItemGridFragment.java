package com.example.android.project7;

import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
	private RecyclerView mRecyclerView;
	private ArrayList<String> list;
	private Cursor mCursor;
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
//	starterItems.add(new Item(getContext(),getString(R.string.animal_label_cat),R.drawable.cat_1, getString(R.string.category_animals)));,
//			new Item(getContext(),getString(R.string.animal_label_cow),R.drawable.cow_1,getString(R.string.category_animals)),
//			new Item(getContext(),getString(R.string.animal_label_dog),R.drawable.dog_1,getString(R.string.category_animals)),
//			new Item(getContext(),getString(R.string.animal_label_owl),R.drawable.owl_1,getString(R.string.category_animals)),
//			new Item(getContext(),getString(R.string.person_name_dad),R.drawable.v_face, getString(R.string.category_people)),
//			new Item(getContext(),getString(R.string.person_name_mom),R.drawable.v_face,getString(R.string.category_people)),
//			new Item(getContext(),getString(R.string.food_name_apple),R.drawable.apple_1,getString(R.string.category_food)),
//			new Item(getContext(),getString(R.string.food_name_banana),R.drawable.banana_1, getString(R.string.category_food))




	public interface Callback {
		public void onItemSelected(Uri idUri, ItemsGridAdapter.ItemsGridAdapterViewHolder vh);
	}


	public ItemGridFragment(){
		//init();
	}

//	private void init(){
//		try {
//			mCursor = getActivity().getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI, null, null, null, null);
//		}catch (Exception e){
//			Log.d(LOG_TAG, "Exception " + e);
//			e.printStackTrace();
//		}
//		if (mCursor == null) {
//
//			ArrayList<String> starterAnimals = new ArrayList<>();
//			starterAnimals.add("cat");//getString(R.string.animal_label_cat));
//			starterAnimals.add("dog");//getString(R.string.animal_label_dog));
//			starterAnimals.add("cow");//getString(R.string.animal_label_cow));
//			starterAnimals.add("owl");//getString(R.string.animal_label_owl));
//
//			ArrayList<String> starterPeople = new ArrayList<>();
//			starterPeople.add("Vera");//getString(R.string.person_name_label));
//			starterPeople.add("Dad");//getString(R.string.person_name_dad));
//			starterPeople.add("Mom");//getString(R.string.person_name_mom));
////			starterPeople.add(getString(R.string.person_name_brother));
////			starterPeople.add(getString(R.string.person_name_sister));
////			starterAnimals.add(getString(R.string.person_name_label));
////			starterAnimals.add(getString(R.string.person_name_label));
//
//			ArrayList<String> starterFoods = new ArrayList<>();
//			starterFoods.add("Apple");//getString(R.string.fruit_label_apple));
//			starterFoods.add("Banana");//getString(R.string.fruit_label_banana));
//
//			//Vector<ContentValues> cVVector = new Vector<ContentValues>(starterAnimals.size()+starterFoods.size()+starterPeople.size());

//			ContentValues[] itemValues = new ContentValues[];
//
//			for (Item item : starterItems){
//				String name = item.getName();
//				int photo = item.getPhoto();
//				itemValues.put();


//			for (String item_name : starterAnimals) {
//
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, R.string.category_animals);
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);
//
//				switch (item_name.toLowerCase()) {
//					case "cat":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cat_1);
//						break;
//					case "cow":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cow_1);
//						break;
//					case "dog":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.dog_1);
//						break;
//					case "owl":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.owl_1);
//						break;
//					default:
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cat_1);
//						break;
//				}
//				//cVVector.add(itemValues);
//			}
//
//			for (String item_name : starterFoods) {
//
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, R.string.category_food);
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);
//				switch (item_name) {
//					case "apple":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.apple_1);
//						break;
//					case "banana":
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.banana_1);
//					default:
//						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.apple_1);
//				}
//				//cVVector.add(itemValues);
//			}
//
//			for (String item_name : starterPeople) {
//
//
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, "People");//getString(R.string.category_people)
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);
//				itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.v_face);
//				//cVVector.add(itemValues);
//			}

//			if(!mCursor.moveToFirst())
//			{
//				Uri insertedUri = getContext().getContentResolver().insert(
//						ItemsContract.ItemsEntry.CONTENT_URI,
//						itemValues);
//
//				//itemRowId = ContentUris.parseId(insertedUri);
//
//
//				mCursor.close();
//			}
//			else{
//				//int rowsUpdated = getContext().getContentResolver().update(ItemsContract.ItemsEntry.CONTENT_URI, itemValues, ItemsContract.ItemsEntry.COLUMN_NAME + " = ?", new String[]{id});
//				mCursor.close();
//
//			}
//			//mCursor = getActivity().getContentResolver().bulkInsert(ItemsContract.ItemsEntry.CONTENT_URI,cVVector);
//
//		}
//
//		mCursor = getActivity().getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI,null,null,null,null);
//
//	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		View rootView = inflater.inflate(R.layout.fragment_main_grid, container, false);

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		//mRecyclerView.setHasFixedSize(true);

		final long mLong = 0;

		mItemsGridAdapter = new ItemsGridAdapter(getActivity(),new ItemsGridAdapter.ItemsGridAdapterOnClickHandler(){

			@Override
			public void onClick(Long id, ItemsGridAdapter.ItemsGridAdapterViewHolder vh) {
				long _id = mCursor.getLong(COL_ITEM_ID);
				((Callback)getActivity()).onItemSelected(ItemsContract.ItemsEntry.buildItemUri(_id),vh);
			}
		});
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


	private void setupRecyclerView(RecyclerView recyclerView) {
		recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
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
	}

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

		Uri allItems = ItemsContract.ItemsEntry.CONTENT_URI;

		return new CursorLoader(getActivity(),
				allItems,
				ITEM_COLUMNS,
				null,
				null,
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
