package com.example.android.project7;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.project7.data.ItemsContract;

/**
 * Created by Jon on 7/10/2016.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Long mItemId;
    private FloatingActionButton fab;
    private TextToSpeech myTTS;

//    private Uri itemUri;

    private RecyclerView mRecyclerView;

    private ItemDetailAdapter mItemDetailAdapter;

    private Boolean mEditMode = false;

    private int MY_DATA_CHECK_CODE = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int ITEM_CARDS_LOADER = 1;

//    private static final String[] ITEM_COLUMNS = {
//            ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID,
//            ItemsContract.ItemsEntry.COLUMN_NAME,
//            ItemsContract.ItemsEntry.COLUMN_CATEGORY,
//            ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID,
//            ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1,
//    };

    private static final String[] CARD_COLUMNS = {
            ItemsContract.CardsEntry.TABLE_NAME + "." + ItemsContract.CardsEntry._ID,
            ItemsContract.CardsEntry.COLUMN_ITEM_KEY,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO
    };

//    static final int COL_ITEM_ID = 0;
//    static final int COL_ITEM_NAME = 1;
//    static final int COL_ITEM_CATEGORY = 2;
//    static final int COL_ITEM_PHOTO = 3;
//    static final int COL_PHOTO_EXTRA_1 = 4;

    static final int COL_CARD_ID = 0;
    static final int COL_ITEM_ID = 1;
    static final int COL_EXTRA_CARD_LABEL = 2;
    static final int COL_EXTRA_CARD_DESCRIPTION = 3;
    static final int COL_EXTRA_CARD_PHOTO = 4;

//    Create a newInstance method if you want to pass info to the fragment
//            maybe to have types of cards like (info, memories, photo frame, etc.)

    public static Fragment newInstance(Long itemId){
        Bundle args = new Bundle();
        args.putLong(ItemsContract.CardsEntry.COLUMN_ITEM_KEY, itemId);
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public interface Callback {
        public void onItemSelected(Uri idUri, ItemDetailAdapter.ItemDetailAdapterViewHolder vh);
        public void onItemLongSelected(Long id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null){
            mItemId = args.getLong(ItemsContract.CardsEntry.COLUMN_ITEM_KEY);
           // mCategory = args.getString(ARG_ITEM_CATEGORY);
        }else{
            mItemId = null;
           // mCategory = null;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        inflater.inflate(R.menu.menu_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.add_card:
                final ContentValues cv = new ContentValues();

                AlertDialog.Builder b = new AlertDialog.Builder(ItemDetailFragment.this.getActivity());
                b.setTitle("Add");

                LinearLayout layout = new LinearLayout(ItemDetailFragment.this.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);


                final EditText titleBox = new EditText(ItemDetailFragment.this.getContext());
                titleBox.setHint("Title");
                layout.addView(titleBox);


                final EditText descriptionBox = new EditText(ItemDetailFragment.this.getContext());
                descriptionBox.setHint("Description");
                layout.addView(descriptionBox);

                final EditText photoUrlBox = new EditText(ItemDetailFragment.this.getContext());
                photoUrlBox.setHint("Enter a photo URL");
                layout.addView(photoUrlBox);

                b.setView(layout);

                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = titleBox.getText().toString();
                        String description = descriptionBox.getText().toString();
                        String photoUrl = photoUrlBox.getText().toString();
                        cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL, title);
                        cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION, description);
                        cv.put(ItemsContract.CardsEntry.COLUMN_ITEM_KEY, mItemId);
                        if(photoUrl.length() <= 0){
                            Log.d(LOG_TAG, "photoUrl is " + photoUrl);
                            cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, "");
                        }else{
                            cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, photoUrl);
                        }
                        Uri itemUri = ItemsContract.ItemsEntry.buildItemUri(mItemId);
                        Uri cardUri = getActivity().getContentResolver().insert(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), cv);
                        mItemDetailAdapter.notifyDataSetChanged();

                        Log.d(LOG_TAG, "inserted: cardUri = " + cardUri);
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.create().show();
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.item_recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mItemDetailAdapter = new ItemDetailAdapter(getActivity(),new ItemDetailAdapter.ItemDetailAdapterOnClickHandler(){

            @Override
            public void onClick(Long id, ItemDetailAdapter.ItemDetailAdapterViewHolder vh) {
                //id = mCursor.getLong(COL_ITEM_ID);
//                ((Callback)getActivity()).onItemSelected(ItemsContract.CardsEntry.buildCardsByItemUri(id),vh);
            }

            @Override
            public void onLongClick(Long id) {
                ((Callback)getActivity()).onItemLongSelected(id);
            }
        });

//		mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mItemDetailAdapter);
        //setupRecyclerView(mRecyclerView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(ITEM_CARDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState){
        //mItemDetailAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri cardsUri = ItemsContract.CardsEntry.buildCardsByItemUri(ItemsContract.ItemsEntry.buildItemUri(mItemId));
        String sortOrder = ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL + " ASC";
        String selection = ItemsContract.CardsEntry.COLUMN_ITEM_KEY + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(mItemId)};
        Log.d("TAG", "itemId = " + mItemId);


        return new CursorLoader(getActivity(),
                cardsUri,
                //null,null,null,null);
                CARD_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null){
            createBlankCard();
        }
        mItemDetailAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemDetailAdapter.swapCursor(null);

    }

    private void createBlankCard(){
        ContentValues cv = new ContentValues();
        cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL, "");
        cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION, "");
        cv.put(ItemsContract.CardsEntry.COLUMN_ITEM_KEY, mItemId);
        //cv.put(ItemsContract.CardsEntry.COLUMN_PHOTO_RES_ID, item.getPhoto());
        Uri cardsUri = ItemsContract.CardsEntry.buildCardsByItemUri(ItemsContract.ItemsEntry.buildItemUri(mItemId));
        Uri cardUri = getActivity().getContentResolver().insert(cardsUri, cv);
        mItemDetailAdapter.notifyDataSetChanged();

        Log.d(LOG_TAG, "Added item with name " + " uri = " + cardUri.toString());

    }
}
