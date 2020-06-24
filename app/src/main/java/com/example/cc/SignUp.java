package com.example.cc;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity   {

    DatabaseReference ref;
    private FirebaseAuth mAuth;
    int btnclick=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable f
        setContentView(R.layout.signup);
        EditText shopdetails,username,shopname;
        shopdetails=findViewById(R.id.shopdetails);
        shopdetails.setVisibility(shopdetails.INVISIBLE);
        username=findViewById(R.id.username);
        username.setVisibility(username.INVISIBLE);
        shopname=findViewById(R.id.shopname);
        shopname.setVisibility(shopname.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
    }

    public void mainActivity(View view) {
        Intent i=new Intent(this,MainActivity.class);             //to jump from one activity to another
        startActivity(i);
    }














































    //database Insertion
    public void student(View view) {
        EditText shopdetails,username,shopname;
        shopdetails=findViewById(R.id.shopdetails);
        shopdetails.setVisibility(shopdetails.INVISIBLE);
        username=findViewById(R.id.username);
        username.setVisibility(username.VISIBLE);
        shopname=findViewById(R.id.shopname);
        shopname.setVisibility(shopname.INVISIBLE);

        insertStudent();


    }

    public void insertStudent() {
        Button b;
        b=findViewById(R.id.signupbutton);
        b.setOnClickListener(view -> {
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

            int flag=0;
            final EditText username,pin,cpin,mobilenum,email;


            username=findViewById(R.id.username);
            mobilenum=findViewById(R.id.signupmobile);
            pin=findViewById(R.id.signuppass);
            cpin=findViewById(R.id.passconfirm);
            email=findViewById(R.id.email);


            final String uname=username.getText().toString().trim();
            final String mn=mobilenum.getText().toString().trim() ;
            final String p=pin.getText().toString().trim();
            final String emailid=email.getText().toString().trim();



            if(ValidateUsername())
            {
                flag=1;
                progressBar.dismiss();
            }
             if(validateEmail())
            {
                flag=1;
                progressBar.dismiss();
            }
             if(validateNumber())
            {
                flag=1;
                progressBar.dismiss();
            }
             if(validatePin())
            {
                flag=1;
                progressBar.dismiss();
            }
             if(validateCpin())
            {
                flag=1;
                progressBar.dismiss();
            }
            if(flag==0) {

            mAuth.createUserWithEmailAndPassword(emailid,p)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                System.out.println(currentFirebaseUser.getUid());
                                ref = FirebaseDatabase.getInstance().getReference("Student");

                                verifyEmail();

                                Student st = new Student();
                                st.setUname(uname);
                                st.setMn(mn);
                                st.setPin(p);
                                st.setEmail(emailid);
                                //st.setUid(currentFirebaseUser.getUid());

                                ref.child(currentFirebaseUser.getUid()).setValue(st);
//                                DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Student").child(currentFirebaseUser.getUid()).child("orders");
//                                dref.setValue("");

                                username.setText("");
                                mobilenum.setText("");
                                pin.setText("");
                                cpin.setText("");
                                email.setText("");
                                //Toast.makeText(getApplicationContext(),"Registered Successfully-Login to your account",Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(), "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));



                            } else {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                startActivity(getIntent());
                            }

                            // ...
                        }
                    });


            /*FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            System.out.println(currentFirebaseUser.getUid());
            Toast.makeText(getApplicationContext(), "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();*/


            }
        });



    }

    private void verifyEmail() {
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        currentFirebaseUser.sendEmailVerification()
                .addOnCompleteListener(SignUp.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button


                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verification email sent to " + currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateCpin() {
        EditText pin,cpin;

        pin=findViewById(R.id.signuppass);
        cpin=findViewById(R.id.passconfirm);

        String p=pin.getText().toString().trim();
        String cp=cpin.getText().toString().trim();

        System.out.println(p+"ASDASD"+cp);

        if(!p.equals(cp))
        {
            cpin.setError("pin do not match");
            cpin.requestFocus();
            return true;
        }

        return false;
    }

    private boolean validatePin() {
        EditText pin;

        pin=findViewById(R.id.signuppass);
        String p=pin.getText().toString().trim();

        if(p.isEmpty())
        {
            pin.setError("enter pin");
            pin.requestFocus();
            return true;
        }
        else if(p.length()<6)
        {
            pin.setError("Must contain 6 digits");
            pin.requestFocus();
            return true;
        }

        return false;
    }

    private boolean validateNumber() {
        EditText num;
        num=findViewById(R.id.signupmobile);

        String mn=num.getText().toString().trim();

        if(mn.isEmpty())
        {
            num.setError("enter mobile number");
            num.requestFocus();
            return true;
        }
        else if(!Pattern.compile("[7-9][0-9]{9}").matcher(mn).matches())
        {
            num.setError("enter valid number");
            num.requestFocus();
            return true;
        }
        return false;
    }

    private boolean ValidateUsername() {
        EditText username;
        username=findViewById((R.id.username));

        String uname=username.getText().toString().trim();

        if(uname.isEmpty())
        {
            username.setError("enter username");
            username.requestFocus();
            return true;
        }

        return false;
    }

    private boolean validateEmail() {

        EditText email;
        email=findViewById(R.id.email);

        String emailid=email.getText().toString().trim();

        if(emailid.isEmpty())
        {
            email.setError("enter your email id");
            email.requestFocus();
            return true;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailid).matches())
        {
            email.setError("Invalid email id");
            email.requestFocus();
            return true;
        }
        return false;
    }

    public void Shopkeeper(View view) {
        EditText shopdetails,username,shopname;
        shopdetails=findViewById(R.id.shopdetails);
        shopdetails.setVisibility(shopdetails.VISIBLE);
        username=findViewById(R.id.username);
        username.setVisibility(username.INVISIBLE);
        shopname=findViewById(R.id.shopname);
        shopname.setVisibility(shopname.VISIBLE);

        insertShopkeeper();

    }

    public void insertShopkeeper() {


        Button b;
        b=findViewById(R.id.signupbutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                int flag=0;

                final EditText username,shopname,pin,cpin,mobilenum,shopno,email;


                username=findViewById(R.id.username);
                shopname=findViewById(R.id.shopname);
                mobilenum=findViewById(R.id.signupmobile);
                pin=findViewById(R.id.signuppass);
                cpin=findViewById(R.id.passconfirm);
                shopno=findViewById(R.id.shopdetails);
                email=findViewById(R.id.email);


                final String uname=username.getText().toString().trim();
                final String sname=shopname.getText().toString().trim();
                final String mn=mobilenum.getText().toString().trim() ;
                final String p=pin.getText().toString().trim();
                String cp=cpin.getText().toString().trim();
                final String emailid=email.getText().toString().trim();
                final String sn=shopno.getText().toString().trim();


                if(validateShopnum())
                {
                    flag=1;
                }
                if(validateShopname())
                {
                    flag=1;
                }
                if(validateEmail())
                {
                    flag=1;
                }
                if(validateNumber())
                {
                    flag=1;
                }
                if(validatePin())
                {
                    flag=1;
                }
                if(validateCpin()) {
                    flag=1;
                }

                if(flag==0) {

                mAuth.createUserWithEmailAndPassword(emailid,p)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    System.out.println(currentFirebaseUser.getUid());
                                    ref = FirebaseDatabase.getInstance().getReference("Shopkeeper");
                                    Shopkeeper sk = new Shopkeeper();
                                    sk.setSname(sname);
                                    sk.setShopno(sn);
                                    sk.setMn(mn);
                                    sk.setPin(p);
                                    sk.setEmail(emailid);
                                    sk.setFlag(1);
                                    sk.setStatusOfVerification("0");

                                    ref.child(currentFirebaseUser.getUid()).setValue(sk);

                                    verifyEmail();
                                    shopname.setText("");
                                    mobilenum.setText("");
                                    pin.setText("");
                                    cpin.setText("");
                                    shopno.setText("");
                                    email.setText("");
                                    Toast.makeText(getApplicationContext(),"Registered Successfully-Verify you account",Toast.LENGTH_LONG).show();
                                    //Toast.makeText(getApplicationContext(), "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));


                                } else {
                                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }

                                // ...
                            }
                        });
//                    ref = FirebaseDatabase.getInstance().getReference("Shopkeeper");
//                    Shopkeeper sk = new Shopkeeper();
//                    sk.setSname(sname);
//                    sk.setShopno(sn);
//                    sk.setMn(mn);
//                    sk.setPin(p);
//                    sk.setEmail(emailid);
//
//                    ref.push().setValue(sk);
//
//                    shopname.setText("");
//                    mobilenum.setText("");
//                    pin.setText("");
//                    cpin.setText("");
//                    shopno.setText("");
//                    email.setText("");
                }

            }
        });


    }

    private boolean validateShopnum() {
        EditText e;
        e=findViewById(R.id.shopdetails);
        String sn=e.getText().toString().trim();

        if(sn.isEmpty())
        {
            e.setError("enter reg number");
            return true;
        }
        return false;
    }

    private boolean validateShopname() {
        EditText e;
        e=findViewById(R.id.shopname);
        String sn=e.getText().toString().trim();

        if(sn.isEmpty())
        {
            e.setError("enter shopname");
            return true;
        }
        return false;
    }

    public void check(View view) {
        RadioButton st,sk;
        st=findViewById(R.id.student);
        sk=findViewById(R.id.shopkeeper);
        if(!st.isChecked()&&!sk.isChecked())
        {
            Toast.makeText(getApplicationContext(),"Please Select Student or Merchant",Toast.LENGTH_LONG).show();
        }
    }
}







/* public void dataInsertion(View view) {

        EditText username,shopname,pin,cpin,mobilenum,shopno,email;
        username=findViewById(R.id.username);
        shopname=findViewById(R.id.shopname);
        mobilenum=findViewById(R.id.signupmobile);
        pin=findViewById(R.id.signuppass);
        cpin=findViewById(R.id.passconfirm);
        shopno=findViewById(R.id.shopdetails);
        email=findViewById(R.id.email);


        String uname=username.getText().toString().trim();
        String sname=shopname.getText().toString().trim();
        String mn=mobilenum.getText().toString().trim();
        String p=pin.getText().toString().trim();
        String cp=cpin.getText().toString().trim();
        String emailid=email.getText().toString().trim();
        String sn=shopno.getText().toString().trim();



        if(uname.length()!=0) {


            ref = FirebaseDatabase.getInstance().getReference("Student");
            Student st=new Student();
            st.setUname(uname);
            st.setMn(mn);
            st.setPin(p);
            st.setEmail(emailid);

            ref.push().setValue(st);

            username.setText("");
            mobilenum.setText("");
            pin.setText("");
            cpin.setText("");


        }
        if(sname.length()!=0)
        {
            ref = FirebaseDatabase.getInstance().getReference("Shopkeeper");
            Shopkeeper sk=new Shopkeeper();
            sk.setSname(sname);
            sk.setShopno(sn);
            sk.setMn(mn);
            sk.setPin(p);
            sk.setEmail(emailid);

            ref.push().setValue(sk);

            shopname.setText("");
            mobilenum.setText("");
            pin.setText("");
            cpin.setText("");
            shopno.setText("");
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Who are you? Student or Shopkeeper?",Toast.LENGTH_LONG).show();
        }
    }*/