package com.example.cc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPassword extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable f
        setContentView(R.layout.resetpassword);


    }

    public void ReturnLogin(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void resetPassword(View view) {
        EditText e;
        e=findViewById(R.id.resetmail);
        String mail=e.getText().toString().trim();

        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Check your inbox",Toast.LENGTH_LONG).show();
                            System.out.println("email sent");
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            System.out.println("error:"+task.getException().getMessage());
                        }
                    }
                });
    }
}
