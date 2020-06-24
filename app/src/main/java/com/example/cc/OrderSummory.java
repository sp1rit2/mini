package com.example.cc;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderSummory extends AppCompatActivity implements RecyclerAdapterForOrderSummory.OnNoteListner{
    ArrayList<ItemnPrice> l;
    ArrayList<String> s;
    DatabaseReference dref;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordersummory);
        this.setTitle(getResources().getString(R.string.app_name2));
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        dref= FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid);

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l=new ArrayList<>();
                //s=new ArrayList<>();
                if(!dataSnapshot.hasChild("orders"))
                {
                    Toast.makeText(getApplicationContext(),"you dont have any orders",Toast.LENGTH_LONG).show();
                    return;
                }
                for(DataSnapshot d:dataSnapshot.child("orders").getChildren()){
                    System.out.println("getkey "+d.getKey());
                    //s.add(d.getKey());
                    l.add(new ItemnPrice(d.getKey(),d.getValue().toString()));
                }

                RecyclerView rc=findViewById(R.id.rv);
                RecyclerAdapterForOrderSummory ra=new RecyclerAdapterForOrderSummory(getApplicationContext(),l,OrderSummory.this);
                rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rc.setAdapter(ra);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    long backButtonCount=0;
    @Override
    public void onNoteClick(int position) {

//        long t=System.currentTimeMillis();
//        if (t-backButtonCount<1000) {
//            dref.child("orders").child(s.get(position)).removeValue();
//            //s.remove(position);
//            //l.remove(position);
//            finish();
//            startActivity(getIntent());
//        } else {
//            backButtonCount=t;
//            Toast.makeText(this, "Double click if order Completed", Toast.LENGTH_LONG).show();
//
//        }




    }
}
