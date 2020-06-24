package com.example.cc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShoplistFragment extends Fragment  {

    String fileName[];
    List<shops> l;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_shoplist,container,false);


        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Physics").child("Bansal");

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("here " + dataSnapshot.child("Chapter_1").child("downloadLink").getValue() + " " + dataSnapshot.child("Chapter_1").child("chapterName").getValue());


//                l = new ArrayList<>();
//                l.add(new shops("asd", R.drawable.common_full_open_on_phone));
//                l.add(new shops("asd", R.drawable.common_full_open_on_phone));
//                l.add(new shops("asd", R.drawable.common_full_open_on_phone));


//                RecyclerView rc = view.findViewById(R.id.rv);
//                //RecyclerAdapter ra = new RecyclerAdapter(getContext(),l);
//                rc.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//                //rc.setAdapter(ra);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }



    private void switchFunction(int position) {
        switch (position) {
            case 0:

        }

    }
    }

