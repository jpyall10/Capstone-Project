package com.example.android.project7;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.facebook.drawee.view.SimpleDraweeView;

public class ItemGridFragment extends Fragment {

	private final String LOG_TAG = this.getClass().getSimpleName();
	private RecyclerView mRecyclerView;
	private ArrayList<String> list;
	private Cursor mCursor;


	public ItemGridFragment(){
		//init();
	}

	private void init(){
		try {
			mCursor = getActivity().getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI, null, null, null, null);
		}catch (Exception e){
			Log.d(LOG_TAG, "Exception " + e);
			e.printStackTrace();
		}
		if (mCursor == null) {

			ArrayList<String> starterAnimals = new ArrayList<>();
			starterAnimals.add("cat");//getString(R.string.animal_label_cat));
			starterAnimals.add("dog");//getString(R.string.animal_label_dog));
			starterAnimals.add("cow");//getString(R.string.animal_label_cow));
			starterAnimals.add("owl");//getString(R.string.animal_label_owl));

			ArrayList<String> starterPeople = new ArrayList<>();
			starterPeople.add("Vera");//getString(R.string.person_name_label));
			starterPeople.add("Dad");//getString(R.string.person_name_dad));
			starterPeople.add("Mom");//getString(R.string.person_name_mom));
//			starterPeople.add(getString(R.string.person_name_brother));
//			starterPeople.add(getString(R.string.person_name_sister));
//			starterAnimals.add(getString(R.string.person_name_label));
//			starterAnimals.add(getString(R.string.person_name_label));

			ArrayList<String> starterFoods = new ArrayList<>();
			starterFoods.add("Apple");//getString(R.string.fruit_label_apple));
			starterFoods.add("Banana");//getString(R.string.fruit_label_banana));

			Vector<ContentValues> cVVector = new Vector<ContentValues>(starterAnimals.size()+starterFoods.size()+starterPeople.size());

			for (String item_name : starterAnimals) {
				ContentValues itemValues = new ContentValues();

				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, R.string.category_animals);
				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);

				switch (item_name.toLowerCase()) {
					case "cat":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cat_1);
						break;
					case "cow":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cow_1);
						break;
					case "dog":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.dog_1);
						break;
					case "owl":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.owl_1);
						break;
					default:
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.cat_1);
						break;
				}
				cVVector.add(itemValues);
			}

			for (String item_name : starterFoods) {
				ContentValues itemValues = new ContentValues();

				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, R.string.category_food);
				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);
				switch (item_name) {
					case "apple":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.apple_1);
						break;
					case "banana":
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.banana_1);
					default:
						itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.apple_1);
				}
				cVVector.add(itemValues);
			}

			for (String item_name : starterPeople) {
				ContentValues itemValues = new ContentValues();

				itemValues.put(ItemsContract.ItemsEntry.COLUMN_CATEGORY, "People");//getString(R.string.category_people)
				itemValues.put(ItemsContract.ItemsEntry.COLUMN_NAME, item_name);
				itemValues.put(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID, R.drawable.v_face);
				cVVector.add(itemValues);
			}

		}

		mCursor = getActivity().getContentResolver().query(ItemsContract.ItemsEntry.CONTENT_URI,null,null,null,null);

	}

	//@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRecyclerView = (RecyclerView) inflater.inflate(
				R.layout.fragment_item_grid, container, false);
		setupRecyclerView(mRecyclerView);
		return mRecyclerView;
	}

	private void setupRecyclerView(RecyclerView recyclerView) {
		recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
//		int categoryIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_CATEGORY);
//		String category = mCursor.getString(categoryIndex);
//		switch(category.toLowerCase()){
//			case "animals":
//				recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
//						getSublistByCategory(Item.sAnimals, getString(R.string.category_animals))));
//				break;
//			case "people":
//				recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
//						getSublistByCategory(Item.sPeople, getString(R.string.category_people))));
//				break;
//			case "food":
//				recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
//						getSublistByCategory(Item.sFoods, getString(R.string.category_food))));
//				break;
//			default:
//				recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
//						getRandomSublist(Item.sItemStrings, 30)));
//		}
		recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
				getRandomSublist(Item.sItemStrings, 30)));
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

	public static class RecyclerViewAdapter
			extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
		private final TypedValue mTypedValue = new TypedValue();
		private int mBackground;
		private List<String> mValues;

		public static class ViewHolder extends RecyclerView.ViewHolder {
			public String mBoundString;

			public final CardView mView;
			public final ImageView mImageView;
			public final TextView mTextView;

			public ViewHolder(View v) {
				super(v);
				mView = (CardView)v.findViewById(R.id.card_view);
				mImageView = (ImageView) v.findViewById(R.id.avatar);
				mTextView = (TextView) v.findViewById(R.id.text1);
			}

			@Override
			public String toString() {
				return super.toString() + " '" + mTextView.getText();
			}
		}

		public String getValueAt(int position) {
			return mValues.get(position);
		}

		public RecyclerViewAdapter(Context context, List<String> items) {
			context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
			mBackground = mTypedValue.resourceId;
			mValues = items;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.list_item, parent, false);
			//view.setBackgroundResource(mBackground);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			holder.mBoundString = mValues.get(position);
			holder.mTextView.setText(holder.mBoundString);

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = v.getContext();
					Intent intent = new Intent(context, ItemDetailActivity.class);
					intent.putExtra(ItemDetailActivity.EXTRA_NAME, holder.mBoundString);

					context.startActivity(intent);
				}
			});

			Glide.with(holder.mImageView.getContext())
					.load(Item.getItemDrawable(mValues.get(position)))
					.fitCenter()
					.into(holder.mImageView);
		}

		@Override
		public int getItemCount() {
			return mValues.size();
		}
	}
}
