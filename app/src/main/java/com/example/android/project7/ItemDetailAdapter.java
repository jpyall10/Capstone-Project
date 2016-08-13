package com.example.android.project7;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.project7.data.ItemsContract;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

/**
 * Created by Jon on 7/10/2016.
 */
public class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailAdapter.ItemDetailAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ItemDetailAdapterOnClickHandler mClickHandler;

    public class ItemDetailAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final CardView mCardView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;

        public ItemDetailAdapterViewHolder(View v) {
            super(v);
            mCardView = (CardView)v.findViewById(R.id.extra_card);
            mImageView = (ImageView) v.findViewById(R.id.extra_image);
            mTitleView = (TextView) v.findViewById(R.id.extra_title);
            mDescriptionView = (TextView) v.findViewById(R.id.extra_description);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText();
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(ItemsContract.CardsEntry._ID);
            mClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(ItemsContract.CardsEntry._ID);
            mClickHandler.onLongClick(mCursor.getLong(idColumnIndex));
            return true;
        }
    }

    public static interface ItemDetailAdapterOnClickHandler {
        void onClick(Long id, ItemDetailAdapterViewHolder vh);
        void onLongClick(Long id);
    }

    public ItemDetailAdapter(Context context, ItemDetailAdapterOnClickHandler handler){
        mContext = context;
        mClickHandler = handler;
    }


    @Override
    public ItemDetailAdapter.ItemDetailAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.extra_detail_card, parent, false);
        return new ItemDetailAdapterViewHolder(view);    }

    @Override
    public void onBindViewHolder(ItemDetailAdapter.ItemDetailAdapterViewHolder holder, int position) {
        if(mCursor != null && mCursor.moveToFirst()){
            Log.d("TAG", "item count is " + getItemCount());
            mCursor.moveToPosition(position);
            final String extraPhoto;
            final String extraTitle, extraDescription, extraLocation;

            extraTitle = mCursor.getString(mCursor.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_LABEL));
            extraPhoto = mCursor.getString(mCursor.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_PHOTO));
            extraDescription = mCursor.getString(mCursor.getColumnIndex(ItemsContract.CardsEntry.COLUMN_EXTRA_CARD_DESCRIPTION));

            holder.mTitleView.setText(extraTitle);
            holder.mDescriptionView.setText(extraDescription);

            if (extraPhoto != null && !extraPhoto.equals("")) {
                holder.mImageView.setVisibility(View.VISIBLE);
                Glide.with(holder.mImageView.getContext())
                        .load(extraPhoto)
                        .fitCenter()
                        .into(holder.mImageView);
            }
        }
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }else {
            return 0;
        }
    }

    public Cursor getCursor(){
        return mCursor;
    }

}

