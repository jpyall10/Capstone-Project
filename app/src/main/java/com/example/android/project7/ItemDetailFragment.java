package com.example.android.project7;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

/**
 * Created by Jon on 7/10/2016.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Long mItemId;
    private FloatingActionButton fab;
    private TextToSpeech myTTS;

    private String mGetPhotoUriString;
    private String mTakePhotoUriString;

    private ImageView mPreviewImage;

//    private Uri itemUri;

    private RecyclerView mRecyclerView;

    private ItemDetailAdapter mItemDetailAdapter;

    private int MY_DATA_CHECK_CODE = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 0;


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
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LOCATION
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
    static final int COL_EXTRA_LOCATION = 5;

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
        public void onItemSelected(Long id, ItemDetailAdapter.ItemDetailAdapterViewHolder vh);
        public void onItemLongSelected(Long id);
        //public void onBackdropChanged(String uriString);
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
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(ItemDetailActivity.getEditMode()){
            Log.d(LOG_TAG, "onResume edit mode is true");
            ItemDetailActivity.mEditMode = true;
            ItemDetailActivity.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCard();
                }
            });
        }else{
            Log.d(LOG_TAG, "onResume edit mode is false");
            ItemDetailActivity.mEditMode = false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        if(ItemDetailActivity.getEditMode()){
            menu.getItem(1).setIcon(R.drawable.ic_create_24dp_accent);
        }else{
            menu.getItem(1).setIcon(R.drawable.ic_create_24dp);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        Log.d(LOG_TAG, "item selected is " + item.toString() + " " + item.getItemId());


        switch(id) {
            case R.id.edit_mode:
                ItemDetailActivity.toggleEditMode();

                if(ItemDetailActivity.getEditMode()) {
                    Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_on),Toast.LENGTH_LONG);
                    toast.show();
                    item.setIcon(R.drawable.ic_create_24dp_accent);
                    ItemDetailActivity.fab.setVisibility(View.VISIBLE);
                    ItemDetailActivity.fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                                if (mInterstitialAd.isLoaded()) {
//                                    mInterstitialAd.show();
//                                }
                                addCard();
//                                requestNewInterstitial();
                        }
                    });
                    //item.setTitle(getString(R.string.edit_mode_on));
                }else{
                    Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_off),Toast.LENGTH_LONG);
                    toast.show();
                    item.setIcon(R.drawable.ic_create_24dp);
                    ItemDetailActivity.fab.setVisibility(View.GONE);
                    //item.setTitle(getString(R.string.edit_mode_off));
                }
                break;
            default:
        }
//            case R.id.edit_mode:
//                ItemDetailActivity.toggleEditMode();
//
//                if(ItemDetailActivity.getEditMode()) {
//                    item.setIcon(R.drawable.ic_create_24dp_accent);
//                    ItemDetailActivity.fab.setVisibility(View.VISIBLE);
//                    ItemDetailActivity.fab.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                                if (mInterstitialAd.isLoaded()) {
////                                    mInterstitialAd.show();
////                                }
////
////                                requestNewInterstitial();
//                            addCard();
//                        }
//                    });
//                    //item.setTitle(getString(R.string.edit_mode_on));
//                }else{
//                    item.setIcon(R.drawable.ic_create_24dp);
//                    ItemDetailActivity.fab.setVisibility(View.GONE);
//                    //item.setTitle(getString(R.string.edit_mode_off));
//                }
//                break;
//            default:
//        }
        return true;
    }

    public void addCard() {
        final ContentValues cv = new ContentValues();

        AlertDialog.Builder b = new AlertDialog.Builder(ItemDetailFragment.this.getActivity());
        b.setTitle("Add");

        final LinearLayout layout = new LinearLayout(ItemDetailFragment.this.getContext());
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
                if (photoUrl.length() <= 0) {
                    Log.d(LOG_TAG, "photoUrl is " + photoUrl);
                    cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, "");
                } else {
                    cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, photoUrl);
                }
                Uri itemUri = ItemsContract.ItemsEntry.buildItemUri(mItemId);
                Uri cardUri = getActivity().getContentResolver().insert(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), cv);
                mItemDetailAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "inserted: cardUri = " + cardUri);
            }
        });
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        b.create().show();
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

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.item_recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mItemDetailAdapter = new ItemDetailAdapter(getActivity(),new ItemDetailAdapter.ItemDetailAdapterOnClickHandler(){

            @Override
            public void onClick(Long id, ItemDetailAdapter.ItemDetailAdapterViewHolder vh) {
                //id = mCursor.getLong(COL_ITEM_ID);
                ((Callback)getActivity()).onItemSelected(id,vh);
            }

            @Override
            public void onLongClick(Long id) {
                ((Callback)getActivity()).onItemLongSelected(id);
            }
        });

        mRecyclerView.setAdapter(mItemDetailAdapter);
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
        mItemDetailAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemDetailAdapter.swapCursor(null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == this.getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathCol = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathCol, null, null, null);
            cursor.moveToFirst();
            String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
            //setGetPhotoUriString(picPath);
            //loadPreviewImage(selectedImage);
            Log.d("IGF", "getGetPhotoUriString = " + picPath);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == this.getActivity().RESULT_OK){
                Bundle extras = data.getExtras();
                Uri takenPictureUri = data.getData();
//                setTakePhotoUriString(takenPictureUri.toString());
                //loadPreviewImage(takenPictureUri.toString());
            }
        }
    }
}
