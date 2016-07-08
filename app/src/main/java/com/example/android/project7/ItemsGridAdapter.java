package com.example.android.project7;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

    public class ItemsGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String mNameString;

        public final CardView mView;
        public final ImageView mAvatar;
        public final TextView mTextView;
        public final CheckableLinearLayout mCheckableLayout;

        public ItemsGridAdapterViewHolder(View v) {
            super(v);
            mView = (CardView)v.findViewById(R.id.card_view);
            mAvatar = (ImageView) v.findViewById(R.id.avatar);
            mTextView = (TextView) v.findViewById(R.id.text1);
            //mCheckbox = (ImageView) v.findViewById(R.id.checkbox);
            mCheckableLayout = (CheckableLinearLayout) v.findViewById(R.id.checkable_layout);
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
            int idColumnIndex = mCursor.getColumnIndex(ItemsContract.ItemsEntry._ID);
            mClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
            mCheckableLayout.toggle();
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
        final int photo;
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


//            public void onClick(View v) {
//                Context context = v.getContext();
//                Intent intent = new Intent(context, ItemDetailActivity.class);
//                intent.putExtra(ItemDetailActivity.EXTRA_NAME, name);
//                intent.putExtra(ItemDetailActivity.EXTRA_PHOTO, photo);
//                //intent.putExtra(ItemDetailActivity.EXTRA_DESCRIPTION, description);
//
//                context.startActivity(intent);
//            }

        Glide.with(holder.mAvatar.getContext())
                .load(photo)
                .fitCenter()
                .into(holder.mAvatar);
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