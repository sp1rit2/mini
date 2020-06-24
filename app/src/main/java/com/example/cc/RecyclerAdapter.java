package com.example.cc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<shops> l;
    private  OnNoteListner on;

    MyViewHolder viewHolder;

    public RecyclerAdapter(Context mContext, List<shops> l,OnNoteListner on) {
        this.mContext = mContext;
        this.l = l;
        this.on=on;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li=LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.card_view,parent, false);
        viewHolder= new MyViewHolder(view,on);


        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.name.setText(l.get(position).getName());
            //holder.img.setImageResource(l.get(position).getImg());
    }


    @Override
    public int getItemCount() {
        return l.size();
    }

    public interface OnNoteListner {
        void  onNoteClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView img;
        OnNoteListner on;
        CardView cardView;
        public  MyViewHolder(View view,OnNoteListner on)
        {
            super(view);
            cardView=view.findViewById(R.id.cardView);
            name=view.findViewById(R.id.bn);
            //img=view.findViewById(R.id.img);
            this.on=on;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            on.onNoteClick(getAdapterPosition());

        }

    }
}
