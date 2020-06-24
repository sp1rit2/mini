package com.example.cc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import androidx.appcompat.app.AppCompatActivity;

public class Inventory extends AppCompatActivity {


    ListView l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        this.setTitle(getResources().getString(R.string.app_name1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fetching teachers
        ArrayList<String> itemList = new ArrayList<>();
        ArrayList<String> priceList=new ArrayList<>();


        //database ref
        DatabaseReference ref;

        ref= FirebaseDatabase.getInstance().getReference("Shopkeeper");

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();



        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid);
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.hasChild("menu")){
                    return;
                }
                //used for student too
                l=findViewById(R.id.listview);
                DataSnapshot d=dataSnapshot.child("menu");

                for(DataSnapshot d1:d.getChildren()) {
                    String item = d1.getKey();
                    String price = d1.getValue().toString();
                    System.out.println("list " + item);
                    itemList.add("\t\t\t\t\t\t\t" + item + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + price + "/-");
                    //priceList.add(price);
                    ArrayAdapter<String> arrayAdapter, arrayAdapter1;
                    arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, itemList);
                    l.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
                //setting arrayadapter to listview


                //arrayAdapter1=new ArrayAdapter<>(getApplication(),android.R.layout.simple_list_item_2,priceList);
                //l.setAdapter(arrayAdapter1);

//                uref.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//
//                        //   arrayAdapter1.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                        System.out.println("inside onchild chnged");
//                        String item = dataSnapshot.getKey();
//                        String price=dataSnapshot.getValue(String.class);
//                        System.out.println("list "+item);
//                        itemList.add("\t\t\t\t"+item+"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+price+"/-");
//                        //priceList.add(price);
//                        ArrayAdapter<String> arrayAdapter,arrayAdapter1;
//                        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,itemList);
//                        l.setAdapter(arrayAdapter);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Button b;

        b=findViewById(R.id.add);

        EditText i,p;

        i=findViewById(R.id.item);
        p=findViewById(R.id.price);



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String item=i.getText().toString().trim();
                String price=p.getText().toString().trim();

                try {
                    if(item.isEmpty())
                    {
                        i.requestFocus();
                    }
                    else if(price.isEmpty())
                    {
                        p.requestFocus();
                    }
                    else {
                        dref.child("menu").child(item).setValue(price);
                        i.setText("");
                        p.setText("");
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        });

        Button r=findViewById(R.id.remove);

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String item=i.getText().toString().trim();

                try {
                    if(item.isEmpty())
                    {
                        i.requestFocus();
                    }
                    else {
                        ref.child(uid).child("menu").child(item).removeValue();
                        i.setText("");
                        p.setText("");
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }

            }
        });





    }
}
