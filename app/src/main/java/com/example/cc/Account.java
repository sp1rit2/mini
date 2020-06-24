package com.example.cc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        this.setTitle(getResources().getString(R.string.app_name3));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText name,pass,cpass,phone,add,op;
        Button b=findViewById(R.id.save);
        Button d=findViewById(R.id.delete);


        name=findViewById(R.id.name);
        pass=findViewById(R.id.Password);
        cpass=findViewById(R.id.Confirm);
        phone=findViewById((R.id.phone));
        op=findViewById(R.id.oldpass);


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        String uid=user.getUid();





        System.out.println(uid);

        b.setOnClickListener(view -> {
            final ProgressDialog progressBar = new ProgressDialog(this);
            progressBar.setIndeterminate(true);
            progressBar.setProgressStyle(0);
            progressBar.setCancelable(true);
            progressBar.incrementProgressBy(50);
            progressBar.setTitle("Please Wait!");
            progressBar.setMessage("Loading...");
            progressBar.show();
            String s=name.getText().toString().trim();
            String p=pass.getText().toString().trim();
            String cp=cpass.getText().toString().trim();
            String ph=phone.getText().toString().trim();
            String oldp=op.getText().toString().trim();


            System.out.println(s);

            if(!s.isEmpty())
            {
                DatabaseReference db=FirebaseDatabase.getInstance().getReference("Shopkeeper/"+uid+"/sname");
                    db.setValue(s);
                    System.out.println("here");
                    name.setText("");
                Toast.makeText(getApplicationContext(),"name changed",Toast.LENGTH_SHORT).show();


            }

            if(!ph.isEmpty())
            {
                DatabaseReference db=FirebaseDatabase.getInstance().getReference("Shopkeeper/"+uid+"/mn");
                db.setValue(ph);
                System.out.println("here");
                phone.setText("");
                Toast.makeText(getApplicationContext(),"phone number changed",Toast.LENGTH_SHORT).show();
            }


            if(!p.isEmpty()&&p.length()>6) {
                if (p.equals(cp)) {
                    if (oldp.isEmpty()) {
                        op.requestFocus();
                        Toast.makeText(getApplicationContext(), "enter old psa", Toast.LENGTH_LONG).show();
                    } else {
                        AuthCredential auth = EmailAuthProvider.getCredential(user.getEmail(), oldp);

                        user.reauthenticate(auth).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    user.updatePassword(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_LONG).show();
                                                op.setText("");
                                                pass.setText("");
                                                cpass.setText("");
                                            } else {

                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }

                } else {
                    progressBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                progressBar.dismiss();
            }

            progressBar.dismiss();


        });


        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                String uid=user.getUid();

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Shopkeeper");
                            ref.child(uid).removeValue();

                            Toast.makeText(getApplicationContext(),"deleted "+user.getEmail(),Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }
                });
            }
        });









    }



}
