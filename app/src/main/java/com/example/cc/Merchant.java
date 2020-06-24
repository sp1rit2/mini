package com.example.cc;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Merchant extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 234;

    //Buttons
    private Button buttonChoose;
    private Button uploaddp;

    //ImageView
    private ImageView imageView, imageView2;

    //a Uri object to store file path
    private Uri filePath;
    private TextView t;

    int clicked = 0;

    int PLACE_PICKER = 1;

    DatabaseReference ref,x;
    final String[] name = new String[1];

    private CardView inventory, account, order, transaction, cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchnt);

        cd = findViewById(R.id.cd);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        //display pics
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uid +".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            DatabaseReference ref;
            ref = FirebaseDatabase.getInstance().getReference("Shopkeeper/" + uid + "/url");
            ref.setValue(uri.toString());
            System.out.println(uri);
            Glide.with(Merchant.this)
                    .load(uri)
                    .into(imageView);

        });

        final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("dp/" + uid +".jpg");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            System.out.println(uri);
            Glide.with(Merchant.this)
                    .load(uri)
                    .into(imageView2);

        });

        uploaddp = findViewById(R.id.uploaddp);
        buttonChoose = (Button) findViewById(R.id.uploadcp);
        //textview welcm address
        t = findViewById(R.id.location);
        // define cards
        account = (CardView) findViewById(R.id.account_card);
        inventory = (CardView) findViewById(R.id.inventory_card);
        order = (CardView) findViewById(R.id.order_card);
        transaction = (CardView) findViewById(R.id.transaction_card);
        // add click listener to cards

        account.setOnClickListener(this);
        inventory.setOnClickListener(this);
        order.setOnClickListener(this);
        transaction.setOnClickListener(this);

        //location listner


        //logout listener


        Button l = findViewById(R.id.logout);


        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mPreferences = getSharedPreferences(uid, MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove("UserName");
                editor.remove("PassWord");
                editor.commit();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
        //uploading image

        buttonChoose.setOnClickListener(this);
        uploaddp.setOnClickListener(this);

        TextView t;
        t = findViewById(R.id.location);

        //set address on mainpage
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference("Shopkeeper/" + uid + "/sname");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("address-->>");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("address-->>");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ValueEventListener v = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(v);

        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });


    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onactivity");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                if (clicked == 1) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView2.setImageBitmap(bitmap);
                }
                uploadFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //this method will upload the file

    StorageReference storageReference;

    private void uploadFile() {
        //if there is a file to upload
        System.out.println("upload");
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = null;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            storageReference = FirebaseStorage.getInstance().getReference();
            if (clicked == 1) {
                riversRef = storageReference.child("images/" + uid + ".jpg");
            }
            if (clicked == 2) {
                riversRef = storageReference.child("dp/" + uid + ".jpg");
            }
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        if (view == buttonChoose) {
            clicked = 1;
            Toast.makeText(getApplicationContext(), "Prefered resolution: 108px X 108px", Toast.LENGTH_LONG).show();
            showFileChooser();
        }
        if (view == uploaddp) {
            clicked = 2;
            Toast.makeText(getApplicationContext(), "Prefered resolution: match_parent X 200px", Toast.LENGTH_LONG).show();
            showFileChooser();

        }
        switch (view.getId()) {
            case R.id.account_card:
                intent = new Intent(Merchant.this, Account.class);
                this.startActivity(intent);
                break;
            case R.id.inventory_card:
                intent = new Intent(this, Inventory.class);
                this.startActivity(intent);
                break;
            case R.id.order_card:
                intent = new Intent(this, OrderSummory.class);
                this.startActivity(intent);
                break;
            case R.id.transaction_card:
                intent = new Intent(this, Transaction.class);
                this.startActivity(intent);
                break;
            default:
                break;
        }


    }

    long backButtonCount = 0;

    public void onBackPressed() {
        long t=System.currentTimeMillis();
        if (t-backButtonCount<1500) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            backButtonCount=t;
            Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();

        }
    }


}

//location





