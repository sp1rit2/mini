package com.example.cc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterForTransaction extends RecyclerView.Adapter<RecyclerAdapterForTransaction.MyViewHolder> {

    private Context mContext;
    private List<ItemnPrice> l;
    private  OnNoteListner on;

    MyViewHolder viewHolder;

    public RecyclerAdapterForTransaction(Context mContext, List<ItemnPrice> l, OnNoteListner on) {
        this.mContext = mContext;
        this.l = l;
        this.on=on;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li=LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.card_view_activity_transaction,parent, false);
        viewHolder= new MyViewHolder(view,on);


        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.item.setText(l.get(position).getItem());
            holder.price.setText(l.get(position).getPrice());
    }


    @Override
    public int getItemCount() {
        return l.size();
    }

    public interface OnNoteListner {
        void  onNoteClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView item,price;
        OnNoteListner on;
        public  MyViewHolder(View view,OnNoteListner on)
        {
            super(view);
            item=view.findViewById(R.id.item);
            price=view.findViewById(R.id.price);
            this.on=on;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            on.onNoteClick(getAdapterPosition());

        }

    }
}
