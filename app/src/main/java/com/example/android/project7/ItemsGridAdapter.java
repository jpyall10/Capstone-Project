package com.example.android.project7;

import android.content.Context;
import android.content.SyncAdapterType;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.squareup.picasso.Picasso;

import java.io.File;
//import com.facebook.drawee.view.SimpleDraweeView;

public class ItemsGridAdapter
        extends RecyclerView.Adapter<ItemsGridAdapter.ItemsGridAdapterViewHolder> {

    //private static final float ASPECT_RATIO = 4.0f/3;
    private Cursor mCursor;
    final private Context mContext;
    final private ItemsGridAdapterOnClickHandler mClickHandler;

    public class ItemsGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        //public final CardView mView;
        public final ImageView mAvatar;
        public final TextView mTextView;
//        public final LinearLayout mCheckableLayout;

        public ItemsGridAdapterViewHolder(View v) {
            super(v);
            //mView = (CardView)v.findViewById(R.id.card_view);
            mAvatar = (ImageView) v.findViewById(R.id.avatar);
            mTextView = (TextView) v.findViewById(R.id.text1);
            //mCheckbox = (ImageView) v.findViewById(R.id.checkbox);
//            mCheckableLayout = (LinearLayout) v.findViewById(R.id.linear_layout);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
            mTextView.setOnClickListener(this);
//            mTextView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            if(mTextView == v) {
                try{
                    String name = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
                    MainActivity.readText(name);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                int idColumnIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry._ID);
                mClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry._ID);
            mClickHandler.onLongClick(mCursor.getLong(idColumnIndex));
            return true;
        }
    }

//    public Item getValueAt(int position) {
//        return mCursor.get(position);
//    }

    public static interface ItemsGridAdapterOnClickHandler {
        void onClick(Long id, ItemsGridAdapterViewHolder vh);
        void onLongClick(Long id);
    }

//    public ItemsGridAdapter(Context context, Cursor cursor, int flags, int loaderID){
//        //super(context,cursor,flags);
//        //context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        //mBackground = mTypedValue.resourceId;
//        mContext = context;
//        mCursor = cursor;
//        //sLoaderID = loaderID;
//        //mItems = items;
//    }

    public ItemsGridAdapter(Context context, ItemsGridAdapterOnClickHandler handler){
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public ItemsGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        //view.setBackgroundResource(mBackground);
        //return new RecyclerView.ViewHolder(view);
        return new ItemsGridAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsGridAdapterViewHolder holder, int position) {
        if(mCursor != null && mCursor.moveToFirst()){
            Log.d("TAG", "item count is " + getItemCount());
            mCursor.moveToPosition(position);
//            final int photo;
            final String name, photoUrl;
            name = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
            holder.mTextView.setText(name);
//            photo = mCursor.getInt(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID));
            photoUrl = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
            Log.d("IGA", "photo url = " + photoUrl + " at position " + position);
            Uri photoUri;
            int height, width;
            if (photoUrl != null && !photoUrl.equals("")) {
                Log.d("TAG", "Glide ran with photo url" + photoUrl);
                photoUri = Uri.parse(photoUrl);
//                holder.mAvatar.setImageURI(photoUri);

            }
////            else if (photo > 0){
////                Log.d("TAG", "Glide ran with photo int " + photo);
////                photoUri = Uri.parse("android.resource://com.example.android.project7/" + photo);
//////                holder.mAvatar.setImageURI(photoUri);
            else{
                photoUri = Uri.parse("android.resource://com.example.android.project7/" + R.drawable.v_face);
                Log.d("TAG", "Glide ran with photouri " + photoUri);
            }
//            Picasso.with(holder.mAvatar.getContext()) //
//                    .load(photoUri) //
//                    .placeholder(R.drawable.v_face) //
////                    .error(R.drawable.error) //
////                    .fit() //
////                    .tag(context) //
//                    .into(holder.mAvatar);
            Glide.with(holder.mAvatar.getContext())
                    .load(photoUrl)
                    .fitCenter()
                    .into(holder.mAvatar);


//            final ImageView avatar = holder.mAvatar;
//            //final TextView nameBox = holder.mTextView;
//            ViewTreeObserver vto = avatar.getViewTreeObserver();
//            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                public boolean onPreDraw() {
//                    int finalHeight,finalWidth;
//                    avatar.getViewTreeObserver().removeOnPreDrawListener(this);
//                    finalHeight = avatar.getMeasuredHeight();
//                    finalWidth = avatar.getMeasuredWidth();
//                    return true;
//                }
//            });


//            height = holder.mAvatar.getHeight();
//            width = holder.mAvatar.getWidth();

            Log.d("IGA", "OnBindViewHolder ran " + " and photo, name " + photoUrl + ", " + name);

//            Log.d("IGA", "OnBindViewHolder ran " + " position = " + position + "height, width: " + height + ", " + width);

            //holder.mAvatar.setAspectRatio(ASPECT_RATIO);
            //holder.mAvatar.setImageURI(uri);
            //holder.mAvatar.setTransitionName(getString(R.string.transition_image) +
            //        position);
        }
    }

    @Override
    public int getItemCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }else {
            return 0;
        }
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder){
        if (viewHolder instanceof ItemsGridAdapterViewHolder){
            ItemsGridAdapterViewHolder ivh = (ItemsGridAdapterViewHolder) viewHolder;
            ivh.onClick(ivh.mAvatar);
        }
    }


}