//package com.example.android.project7;
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
//public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
//
//    private List<Item> itemsList;
//    private Context context;
//
//    public RecyclerViewAdapter(Context context, List<Item> itemsList){
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
