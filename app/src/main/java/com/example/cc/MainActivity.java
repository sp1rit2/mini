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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    SignUp s;
    private FirebaseAuth mAuth;
    int btnclick=0;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable f
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Button btn_login;


        btn_login=findViewById(R.id.login);

        btn_login.setOnClickListener(view -> {
            final ProgressDialog progressBar = new ProgressDialog(this);
            progressBar.setIndeterminate(true);
            progressBar.setProgressStyle(0);
            progressBar.setCancelable(true);
            progressBar.incrementProgressBy(50);
            progressBar.setTitle("Please Wait!");
            progressBar.setMessage("Loading...");
            progressBar.show();
            btnclick++;
            if(btnclick>1){
                return;
            }
            EditText emailid,pin;
            emailid=findViewById(R.id.mn);
            pin=findViewById(R.id.pass);

            String mail=emailid.getText().toString().trim();
            String pn=pin.getText().toString().trim();
            //startActivity(new Intent(getApplicationContext(), StudentDashboard.class));
            if(mail.isEmpty())
            {
                progressBar.dismiss();
                emailid.requestFocus();
            }
            else if(pn.isEmpty())
            {
                progressBar.dismiss();
                pin.requestFocus();
            }
            else {

                mAuth.signInWithEmailAndPassword(mail, pn)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser user=mAuth.getInstance().getCurrentUser();
                                    String uid = user.getUid();
                                    System.out.println("uid "+uid);

                                    DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Student");
                                    final DatabaseReference uref=dref.child(uid);
                                    System.out.println("uref "+uref);


                                    final ValueEventListener ve=new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            System.out.println("snapshot "+dataSnapshot);
                                            System.out.println(user.isEmailVerified ());
                                            if(user.isEmailVerified()) {
                                                if (dataSnapshot.exists()) {
                                                    startActivity(new Intent(getApplicationContext(), StudentDashboard.class));
                                                } else{
                                                    System.out.println("in mechrt login");
                                                    DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid).child("flag");
                                                    dref.setValue(2);                           //to check if merchant has logged in or not
                                                    startActivity(new Intent(getApplicationContext(), Merchant.class));
                                                }

                                                progressBar.dismiss();
                                                finish();
                                            }
                                            else {
                                                progressBar.dismiss();
                                                Toast.makeText(getApplicationContext(),"Please Verify your email first",Toast.LENGTH_LONG).show();
                                                verifyEmail();
                                            }

                                            emailid.setText("");
                                            pin.setText("");

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            System.out.println("asdasd");
                                            Toast.makeText(getApplicationContext(), "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    };
                                    uref.addListenerForSingleValueEvent(ve);


                                    }
                                else
                                {
                                    pin.setText("");
                                    System.out.println("Error");
                                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(getIntent());
                                }

                                }

                                // ...

                        });
            }
        });


    }

    private void verifyEmail() {
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            currentFirebaseUser.sendEmailVerification()
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            // Re-enable button


                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification email sent to " + currentFirebaseUser.getEmail(), Toast.LENGTH_LONG).show();

                            } else {
                                System.out.println(task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Failed to send verification email."+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }



    public void signUp(View view) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }


    public void reset(View view) {
        startActivity(new Intent(getApplicationContext(),ResetPassword.class));
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
