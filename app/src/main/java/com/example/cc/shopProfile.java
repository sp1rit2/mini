package com.example.cc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class shopProfile extends AppCompatActivity implements RecyclerAdapterforInventory.OnNoteListner, OnFailureListener {

    ItemnPrice i=new ItemnPrice();
    DatabaseReference ref;
    List<ItemnPrice> menu;
    ArrayList<String> item,price;
    String uidValue;
    StorageReference sref;
    int flag=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopprofile);


        ImageView imageView;
        TextView mapView;
        mapView = findViewById(R.id.findpath);
        imageView = findViewById(R.id.img);
        final String[] url = new String[1];

        Intent intent = getIntent();
        uidValue = intent.getStringExtra("uidValue");
        System.out.println("inside shopProfile " + uidValue);


        ref = FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uidValue);

        sref= FirebaseStorage.getInstance().getReference().child("images/"+uidValue+".jpg");

            sref.getDownloadUrl().addOnSuccessListener(uri -> {
                System.out.println(uri);
                Glide.with(shopProfile.this)
                        .load(uri)
                        .into(imageView);

            });



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild("url")) {
//                    url[0] = dataSnapshot.child("url").getValue().toString();
//                    System.out.println("url " + url);
//                    System.out.println("url[0] " + url[0]);
//                    Glide.with(shopProfile.this)
//                            .load(url[0])
//                            .into(imageView);
//                }

                menu = new ArrayList<>();
                item=new ArrayList<>();
                price=new ArrayList<>();

                if (dataSnapshot.hasChild("menu")) {
                    for (DataSnapshot d : dataSnapshot.child("menu").getChildren()) {
                        System.out.println(d.getKey() + " " + d.getValue());
                        item.add(d.getKey());
                        price.add(d.getValue().toString());
                        menu.add(new ItemnPrice(d.getKey(),d.getValue().toString()));
                    }


//                    ArrayAdapter<String> ra = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, menu);
//                    //retrieving inventory
//                    l.setAdapter(ra);
                    RecyclerView rc=findViewById(R.id.listview);
                    RecyclerAdapterforInventory ra = new RecyclerAdapterforInventory(shopProfile.this, menu, shopProfile.this);
                    rc.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    rc.setAdapter(ra);

                }
                else{
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//       l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println(i+"       "+l);
//            }
//        });



        mapView.setOnClickListener(view -> {
            System.out.println("clicked");
            Intent intent1 = new Intent(getApplicationContext(), GetPath.class);
            intent1.putExtra("location", uidValue);
            startActivity(intent1);
        });

        if(!this.isDestroyed()) {
            sref.getDownloadUrl().addOnSuccessListener(uri -> {
                System.out.println(uri);
                Glide.with(shopProfile.this)
                        .load(uri)
                        .into(imageView);

            });
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference dreff = FirebaseDatabase.getInstance().getReference("Student").child(uid).child("mn");                //taking mn and using for comparisn
        dreff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mn = dataSnapshot.getValue().toString();
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

//                                        AlertDialog.Builder builder=new AlertDialog.Builder(shopProfile.this);
//                                        builder.setMessage("Your have already ordered something........\nPick up that order to order next item");
//                                        builder.setCancelable(true);
//                                        builder.show();
                                        Toast.makeText(getApplicationContext(),"Your have already ordered something........\nPick up that order to order next item",Toast.LENGTH_LONG).show();
                                        flag=1;

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

        if(flag==1)
        {
            return;
        }
        Intent intent;
//        System.out.println("here");
//        System.out.println("posi "+position);
//        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        String uid=user.getUid();
//        System.out.println("here "+uid);

//        DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Student").child(uid).child("orders").push();
//        dref.child(item.get(position)).setValue(price.get(position));

        i.count++;
        i.x.add(item.get(position));
        i.y.add(price.get(position));
        System.out.println("count "+i.count);
        intent=new Intent(getApplicationContext(),Checkout.class);
        intent.putExtra("count",i.count);
        intent.putExtra("uidValue",uidValue);
//        intent.putExtra("item",item.get(position));
//        intent.putExtra("price",price.get(position));
        startActivity(intent);
//        Toast.makeText(getApplicationContext(),item.get(position)+" "+price.get(position),Toast.LENGTH_LONG).show();
////        switch (position)
////        {
//            case 0:
//                intent=new Intent(getApplicationContext(),Checkout.class);
//                intent.putExtra("item",item.get(0));
//                intent.putExtra("price",price.get(0));
//                startActivity(intent);
//                Toast.makeText(getApplicationContext(),item.get(0)+" "+price.get(0),Toast.LENGTH_LONG).show();
//                break;
//            case 1:
//                intent=new Intent(getApplicationContext(),Checkout.class);
//                intent.putExtra("item",item.get(1));
//                intent.putExtra("price",price.get(1));
//                startActivity(intent);
//                break;
//
//        }

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        System.out.println("File not exists "+e );
        return;
    }
}
