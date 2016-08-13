package com.example.android.project7;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

public class ItemsGridAdapter
        extends RecyclerView.Adapter<ItemsGridAdapter.ItemsGridAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ItemsGridAdapterOnClickHandler mClickHandler;

    public class ItemsGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public final ImageView mAvatar;
        public final TextView mTextView;

        public ItemsGridAdapterViewHolder(View v) {
            super(v);
            mAvatar = (ImageView) v.findViewById(R.id.avatar);
            mTextView = (TextView) v.findViewById(R.id.text1);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
            mTextView.setOnClickListener(this);
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

    public static interface ItemsGridAdapterOnClickHandler {
        void onClick(Long id, ItemsGridAdapterViewHolder vh);
        void onLongClick(Long id);
    }

    public ItemsGridAdapter(Context context, ItemsGridAdapterOnClickHandler handler){
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public ItemsGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ItemsGridAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsGridAdapterViewHolder holder, int position) {
        if(mCursor != null && mCursor.moveToFirst()){
            Log.d("TAG", "item count is " + getItemCount());
            mCursor.moveToPosition(position);
            String name, photoUrl;
            name = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
            splitter.setString(name);
            String capitalizedName = "";
            for(String s : splitter){
                capitalizedName += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
            }

            holder.mTextView.setText(capitalizedName);
            photoUrl = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_EXTRA_1));
            Log.d("IGA", "photo url = " + photoUrl + " at position " + position);
            if (photoUrl == null || photoUrl.equals("")) {
                //default image if cursor has no image
                photoUrl = mContext.getString(R.string.android_resource_uri_base) + R.drawable.cat_1;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.mAvatar.setTransitionName(mContext.getString(R.string.transition_image_avatar)+position);
            }
            Log.d("IGA","transition name = " + mContext.getString(R.string.transition_image_avatar)+position);
            Glide.with(holder.mAvatar.getContext())
                    .load(photoUrl)
                    .fitCenter()
                    .into(holder.mAvatar);

            Log.d("IGA", "OnBindViewHolder ran " + " and photo, name " + photoUrl + ", " + name);
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