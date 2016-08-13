package com.example.android.project7;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Long mItemId;

    private String mGetPhotoUriString;
    private String mTakePhotoUriString;

    private ImageView mPreviewImage;
    private LinearLayout addCardLayout;
    private EditText photoUrlBox;

    private RecyclerView mRecyclerView;

    private ItemDetailAdapter mItemDetailAdapter;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 0;


    private static final int ITEM_CARDS_LOADER = 1;

    private static final String[] CARD_COLUMNS = {
            ItemsContract.CardsEntry.TABLE_NAME + "." + ItemsContract.CardsEntry._ID,
            ItemsContract.CardsEntry.COLUMN_ITEM_KEY,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO,
            ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LOCATION
    };


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
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null){
            mItemId = args.getLong(ItemsContract.CardsEntry.COLUMN_ITEM_KEY);
        }else{
            mItemId = null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(ItemDetailActivity.getEditMode()){
            Log.d(LOG_TAG, "onResume edit mode is true");
            //ItemDetailActivity.mEditMode = true;
            ItemDetailActivity.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCard();
                }
            });
        }else{
            Log.d(LOG_TAG, "onResume edit mode is false");
            //ItemDetailActivity.mEditMode = false;
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
                                addCard();
                        }
                    });
                }else{
                    Toast toast = Toast.makeText(this.getActivity(), getString(R.string.edit_mode_turned_off),Toast.LENGTH_LONG);
                    toast.show();
                    item.setIcon(R.drawable.ic_create_24dp);
                    ItemDetailActivity.fab.setVisibility(View.GONE);
                }
                break;
            default:
        }
        return true;
    }

    public void addCard() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final ContentValues cv = new ContentValues();

        AlertDialog.Builder b = new AlertDialog.Builder(ItemDetailFragment.this.getActivity());
        b.setTitle("Add");

        addCardLayout = new LinearLayout(ItemDetailFragment.this.getContext());
        addCardLayout.setOrientation(LinearLayout.VERTICAL);


        final EditText titleBox = new EditText(ItemDetailFragment.this.getContext());
        titleBox.setHint("Title");
        addCardLayout.addView(titleBox);


        final EditText descriptionBox = new EditText(ItemDetailFragment.this.getContext());
        descriptionBox.setHint("Description");
        addCardLayout.addView(descriptionBox);

        //Initialize photoUrlBox and set Hint
        photoUrlBox = new EditText(ItemDetailFragment.this.getContext());
        photoUrlBox.setHint("Enter a photo URL");

        //initialize PreviewImageView and set layout params
        mPreviewImage = new ImageView(this.getActivity());
        int pixels = (int)ItemDetailActivity.dipToPixels(this.getActivity(),200);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mPreviewImage.setLayoutParams(params);

        photoUrlBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString();
                loadPreviewImage(url);
            }
        });

        //Created buttons Layout
        final LinearLayout editPictureButtons = new LinearLayout(this.getActivity());
        editPictureButtons.setOrientation(LinearLayout.HORIZONTAL);

        //create button to get image from files and set onclick
        final ImageButton getPhotoButton = new ImageButton(this.getActivity());
        getPhotoButton.setImageResource(R.drawable.ic_folder_open_24dp);
        getPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCardLayout.removeView(mPreviewImage);
                addCardLayout.removeView(photoUrlBox);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        //create button to take a new picture with camera and set onclick
        final ImageButton takeNewPhotoButton = new ImageButton(this.getActivity());
        takeNewPhotoButton.setImageResource(R.drawable.ic_photo_camera_24dp);
        takeNewPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        takeNewPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCardLayout.removeView(mPreviewImage);
                addCardLayout.removeView(photoUrlBox);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }
        });

        //add buttons to button layout
        editPictureButtons.addView(getPhotoButton);
        editPictureButtons.addView(takeNewPhotoButton);

        //add buttons, urlbox and preview image to main layout
        addCardLayout.addView(editPictureButtons);
        addCardLayout.addView(photoUrlBox);
        addCardLayout.addView(mPreviewImage);
        b.setView(addCardLayout);

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
                    if (getGetPhotoUriString() != null && !getGetPhotoUriString().equals("")) {
                        photoUrl = getGetPhotoUriString();
                    } else if (getTakePhotoUriString() != null && !getTakePhotoUriString().equals("")) {
                        photoUrl = getTakePhotoUriString();
                    }
                }
                cv.put(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO, photoUrl);
                Uri itemUri = ItemsContract.ItemsEntry.buildItemUri(mItemId);
                Uri cardUri = getActivity().getContentResolver().insert(ItemsContract.CardsEntry.buildCardsByItemUri(itemUri), cv);
                mItemDetailAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "inserted: cardUri = " + cardUri);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

            }
        });
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
        b.create().show();
    }

    private void loadPreviewImage(String path) {
        Glide.with(this)
                .load(path)
                .fitCenter()
                .into(mPreviewImage);
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
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == this.getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathCol = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathCol, null, null, null);
            cursor.moveToFirst();
            String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
            if(picPath == null || picPath.equals("")){
                Toast.makeText(this.getContext(),getString(R.string.get_file_failed_warning), Toast.LENGTH_LONG).show();
            }
            else {
                setGetPhotoUriString(picPath);
                Log.d("IGF", "getGetPhotoUriString = " + picPath);
                if (picPath != null && !picPath.equals("")) {
                    loadPreviewImage(picPath);
                    photoUrlBox.setText(picPath);
                    addCardLayout.addView(photoUrlBox);
                    addCardLayout.addView(mPreviewImage);
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == this.getActivity().RESULT_OK){
                try{
                    Bundle extras = data.getExtras();
                    Uri takenPictureUri = data.getData();
                    String[] filePathCol = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(takenPictureUri, filePathCol, null, null, null);
                    cursor.moveToFirst();
                    String picPath = cursor.getString(cursor.getColumnIndex(filePathCol[0]));
                    if (picPath == null || picPath.equals("")) {
                        Toast.makeText(this.getContext(), getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
                    } else {
                        setTakePhotoUriString(picPath);
                        Log.d("IGF", "getTakePhotoUriString = " + picPath);
                        if (picPath != null && !picPath.equals("")) {
                            loadPreviewImage(picPath);
                            photoUrlBox.setText(picPath);
                            addCardLayout.addView(photoUrlBox);
                            addCardLayout.addView(mPreviewImage);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this.getContext(), getString(R.string.take_photo_failed_warning), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
