package com.example.cc;


import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Order extends AppCompatActivity implements RecyclerAdapterForOrderHistory.OnNoteListner {


    List<ItemnPrice> l;
    String mn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        l = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference dreff = FirebaseDatabase.getInstance().getReference("Student").child(uid).child("mn");
        dreff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mn = dataSnapshot.getValue().toString();
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Shopkeeper");

                dref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d.hasChild("orders")) {

                                for (DataSnapshot d1 : d.child("orders").getChildren()) {
                                    String key = d1.getKey();
                                    System.out.println("key " + key + " " + mn);
                                    if (key.equals(mn)) {
                                        int x = 0;
                                        int count = (int) d1.getChildrenCount();
                                        System.out.println("key " + count);
                                        while (x < count / 2) {
                                            System.out.println("key key "+d1.child("item" + x).getValue().toString()+" "+d1.child("price" + x).getValue().toString());
                                            l.add(new ItemnPrice(d1.child("item" + x).getValue().toString(), d1.child("price" + x).getValue().toString()));
                                            x++;
                                        }
                                        RecyclerView rc = findViewById(R.id.rv);
                                        RecyclerAdapterForOrderHistory ra = new RecyclerAdapterForOrderHistory(getApplicationContext(), l, Order.this);
                                        rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        rc.setAdapter(ra);

                                    }
                                }


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onNoteClick(int position) {

    }
}
