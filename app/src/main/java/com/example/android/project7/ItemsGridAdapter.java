package com.example.android.project7;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;

import java.util.List;

public class ItemsGridAdapter
        extends RecyclerView.Adapter<ItemsGridAdapter.ItemsGridAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ItemsGridAdapterOnClickHandler mClickHandler;

    public class ItemsGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String mNameString;

        public final CardView mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ItemsGridAdapterViewHolder(View v) {
            super(v);
            mView = (CardView)v.findViewById(R.id.card_view);
            mImageView = (ImageView) v.findViewById(R.id.avatar);
            mTextView = (TextView) v.findViewById(R.id.text1);
            v.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry.TABLE_NAME + "." + ItemsContract.ItemsEntry._ID);
            mClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
        }
    }

//    public Item getValueAt(int position) {
//        return mCursor.get(position);
//    }

    public static interface ItemsGridAdapterOnClickHandler {
        void onClick(Long id, ItemsGridAdapterViewHolder vh);
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
        mCursor.moveToPosition(position);
        int photo;
        final String name;
        //holder.mNameString
//        if(!mCursor.isLast()) {
//            mCursor.moveToNext();
//        }else{
//            mCursor.moveToFirst();
//        }
        name = mCursor.getString(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_NAME));
        holder.mTextView.setText(name);
        photo = mCursor.getInt(mCursor.getColumnIndex(ItemsContract.ItemsEntry.COLUMN_PHOTO_RES_ID));

        Log.d("IGA", "OnBindViewHolder ran " + " and photo, name " + photo + ", " + name);

        Log.d("IGA", "OnBindViewHolder ran " + " position = " + position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailActivity.EXTRA_NAME, name);

                context.startActivity(intent);
            }
        });

        Glide.with(holder.mImageView.getContext())
                .load(photo)
                .fitCenter()
                .into(holder.mImageView);
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
            ivh.onClick(ivh.mView);
        }
    }


}
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.List;
//
///**
// * Created by Jon on 6/14/2016.
// */
//public class ItemsGridAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
//
//    private List<Item> itemsList;
//    private Context context;
//
//    public ItemsGridAdapter(Context context, List<Item> itemsList){
//        this.itemsList = itemsList;
//        this.context =context;
//    }
//
//    @Override
//    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
//        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
//        return rcv;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
//        holder.itemName.setText(itemsList.get(position).getName());
//        holder.itemPhoto.setImageResource(itemsList.get(position).getPhoto());
//    }
//
//    @Override
//    public int getItemCount() {
//        return this.itemsList.size();
//    }
//}
//
//class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//    public TextView itemName;
//    public ImageView itemPhoto;
//    public RecyclerViewHolders(View itemView){
//        super(itemView);
//        itemView.setOnClickListener(this);
//        itemName = (TextView)itemView.findViewById(R.id.text1);
//        itemPhoto = (ImageView)itemView.findViewById(R.id.avatar);
//    }
//    @Override
//    public void onClick(View v) {
//        Toast.makeText(v.getContext(), "Clicked item position " + getPosition(), Toast.LENGTH_SHORT).show();
//    }
//}
