package com.example.cc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Transaction extends AppCompatActivity implements RecyclerAdapterForTransaction.OnNoteListner {

    EditText e;
    Button d;
    String mn=null,OTP,suid;
    ArrayList<ItemnPrice> l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        System.out.println("in transaction");
        this.setTitle(getResources().getString(R.string.app_name4));
        e=findViewById(R.id.otp);
        d=findViewById(R.id.delivery);



    }


    public void findOTP(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        OTP=e.getText().toString().trim();
        if(OTP.isEmpty()){
            builder.setMessage("Invalid OTP!");
            builder.setCancelable(true);
            builder.show();
            return;
        }
        System.out.println("student t otp "+OTP);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Student");
        System.out.println("btn is clicked");
        ref.addValueEventListener(new ValueEventListener() {                                                                 //student snapshot for otp&mn
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot d:dataSnapshot.getChildren())                                                           //check if otp child present
                {
                    if(d.hasChild("otp")){
                        String curOTP=d.child("otp").getValue().toString();
                        System.out.println("student "+curOTP);
                        if(OTP.equals(curOTP)){
                            System.out.println("key of mn "+d.getKey());
                            suid=d.getKey();
                            mn=d.child("mn").getValue().toString();
                            System.out.println("student "+mn);
                            flag=1;
                            break;
                        }

                    }
                }
                if(flag==0){
                        System.out.println("1st");
                        builder.setMessage("Invalid OTP!");
                        builder.setCancelable(true);
                        builder.show();
                        return;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        System.out.println("in findOTP");

        FirebaseDatabase.getInstance().getReference("Shopkeeper").addValueEventListener(new ValueEventListener() {//mrchnt snapshot for order related to student mn
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l=new ArrayList<>();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    System.out.println("student "+d.getKey());
                    if(d.hasChild("orders")){
                        for(DataSnapshot d1:d.child("orders").getChildren()){
                            System.out.println("student------->"+d1.getKey());
                            if(mn!=null&&mn.equals(d1.getKey())){
                                int x = 0;
                                int count = (int) d1.getChildrenCount();
                                System.out.println("key " + count);
                                while (x < count / 2) {
                                    l.add(new ItemnPrice(d1.child("item" + x).getValue().toString(), d1.child("price" + x).getValue().toString()));
                                    x++;

                                }
                                RecyclerView rc = findViewById(R.id.rv);
                                RecyclerAdapterForTransaction ra = new RecyclerAdapterForTransaction(getApplicationContext(), l, Transaction.this);
                                rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                rc.setAdapter(ra);

                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        d.setOnClickListener(view1 -> {
            System.out.println("xyz");
            System.out.println("student uid "+suid);
            ref.child(suid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("otp "+dataSnapshot.getKey()+" value "+dataSnapshot.getValue());
                    if(dataSnapshot.hasChild("otp")) {
                        ref.child("otp").removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid=user.getUid();
            DatabaseReference xref=FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid);
            xref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("orders")){
                        if(dataSnapshot.child("orders").hasChild(mn)) {
                            FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid).child("orders").child(mn).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            finish();
            startActivity(new Intent(getApplicationContext(),Merchant.class));
        });
    }

    @Override
    public void onNoteClick(int position) {

    }
}
