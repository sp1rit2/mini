package com.example.cc;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class Checkout extends AppCompatActivity implements RecyclerAdapterForCheckout.OnNoteListner {

    private static final int UPI_PAYMENT = 0;
    ArrayList<ItemnPrice> menu=new ArrayList<>();
    String item, price;
    ItemnPrice j = new ItemnPrice();
    int tot = 0;
    TextView total, shopchange;
    Button pay;
    String upi, name;
    int count;
    String uidValue;
    int randomNumber = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
//        menu = new ArrayList<>();

        total = findViewById(R.id.tot);
        pay = findViewById(R.id.checkout);
        shopchange = findViewById(R.id.shopchange);
        System.out.println("total " + total);
        Intent intent = getIntent();
        uidValue = intent.getStringExtra("uidValue");
//        item=intent.getStringExtra("item");
//        price=intent.getStringExtra("price");
        count = intent.getIntExtra("count", 0);
        System.out.println("asdsad " + count);
        for (int i = 0; i < j.count; i++) {
//            if (j.y.get(i) != "null") {
            menu.add(new ItemnPrice(j.x.get(i), j.y.get(i)));
            System.out.println("j.tot " + j.y.get(i));
            j.tot = j.tot + Integer.parseInt(j.y.get(i));
            //}
        }
        System.out.println("inside checkou tot " + j.tot);
        String temp = String.valueOf(j.tot);
        total.setText(temp);
//        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        String uid=user.getUid();

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uidValue);
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upi = dataSnapshot.child("upiid").getValue().toString();
                name = dataSnapshot.child("sname").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //menu.add(new ItemnPrice(item, price));
        RecyclerView rc = findViewById(R.id.rv);
        RecyclerAdapterForCheckout ra = new RecyclerAdapterForCheckout(Checkout.this, menu, Checkout.this);
        rc.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rc.setAdapter(ra);

        AlertDialog builder=new AlertDialog.Builder(this).create();

        if(menu.isEmpty()){
            builder.setMessage("Your cart is empty! Order fast!");
            builder.setCanceledOnTouchOutside(true);
            builder.show();
            System.out.println("menu "+menu);
        }
        builder.setOnCancelListener(dialog -> {
            //String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            finish();
            Intent intent1=new Intent(getApplicationContext(),shopProfile.class);
            intent1.putExtra("uidValue",uidValue);
            startActivity(intent1);
        });
        j.x.forEach((t) -> System.out.println("t " + t));
//        System.out.println("menu get " + menu.get(0));
        shopchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for (int i = 0; i < j.y.size(); i++) {
//                    j.y.set(i, "null");
//                }
                j.y.clear();
                j.x.clear();
                j.checkForOrder = 0;
                j.count = 0;
                j.tot = 0;
                finish();
                startActivity(new Intent(getApplicationContext(), StudentDashboard.class));
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amt = temp;
                String upiId = upi;
                String shopName = name;
                payUsingUPI(amt, upiId, shopName);
            }
        });


    }

    private void payUsingUPI(String amt, String upiId, String shopName) {
        System.out.println("upi " + upiId);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("am", amt)
                .appendQueryParameter("pn", shopName)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayment = new Intent(Intent.ACTION_VIEW);
        upiPayment.setData(uri);

        Intent chooser = Intent.createChooser(upiPayment, "Pay With");

        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(getApplicationContext(), "you dont have gpay", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String text = data.getStringExtra("response");
                        System.out.println("upi payment result " + text);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(text);
                        upiPaymentDataOperation(dataList);
                    } else {
                        System.out.println("onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    System.out.println("onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> dataList) {
        if (isConnectionAvailable(Checkout.this)) {
            String str = dataList.get(0);
            System.out.println("upi payment poeration " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Checkout.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                System.out.println("responseStr: " + approvalRefNo);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                Random random = new Random();
                StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().detectAll().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                randomNumber = random.nextInt(999999);
                String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
                sendEmail(randomNumber,email);
                System.out.println("OTP is "+randomNumber);
                FirebaseDatabase.getInstance().getReference("Student").child(uid).child("otp").setValue(randomNumber);
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uidValue).child("orders");
                DatabaseReference dreff = FirebaseDatabase.getInstance().getReference("Student").child(uid).child("mn");
                dreff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String temp = dataSnapshot.getValue().toString();
                        for (int i = 0; i < j.count; i++) {
                            dref.child(temp).child("item" + i).setValue(j.x.get(i));
                            dref.child(temp).child("price" + i).setValue(j.y.get(i));
                        }

//                        try {
//                            // Construct data
//                            String apiKey = "apikey=" + "FhREdPmOG5E-KjvDv9R2TiCzJljq7IMtraUuqn48VY";
//                            String message = "&message=" + "[" + temp + "]" + "Your otp is " + randomNumber;
//                            String sender = "&sender=" + "TXTLCL";
//                            String numbers = "&numbers=" + "91"+temp;
//
//                            // Send data
//                            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//                            String data = apiKey + numbers + message + sender;
//                            conn.setDoOutput(true);
//                            conn.setRequestMethod("POST");
//                            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//                            conn.getOutputStream().write(data.getBytes("UTF-8"));
//                            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                            final StringBuffer stringBuffer = new StringBuffer();
//                            String line;
//                            while ((line = rd.readLine()) != null) {
//                                stringBuffer.append(line);
//                        String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
//                        sendEmail(randomNumber,email);
//                        System.out.println("OTP is "+randomNumber);
//                                FirebaseDatabase.getInstance().getReference("Student").child(uid).child("otp").setValue(randomNumber);
//                            }
//                            rd.close();
//                            Toast.makeText(getApplicationContext(), "Check OTP sent to your mobile", Toast.LENGTH_LONG).show();
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(),"Error:"+e,Toast.LENGTH_LONG).show();
//                            System.out.println("Error SMS " + e);
//                        }
                        j.y.clear();
                        j.x.clear();
                        j.checkForOrder = 0;
                        j.count = 0;
                        j.tot = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                startActivity(new Intent(getApplicationContext(), Order.class));

            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(Checkout.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Checkout.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Checkout.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(int otp,String email) {
        String mail=email;
        String msg="Your otp is "+otp;
        String sub="Order OTP";

        JavaMailAPI javaMailAPI=new JavaMailAPI(this,mail,sub,msg);
        javaMailAPI.execute();
    }


    private boolean isConnectionAvailable(Checkout checkout) {
        ConnectivityManager connectivityManager = (ConnectivityManager) checkout.getSystemService(Checkout.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable()) {
                return true;
            }
        }
        Toast.makeText(getApplicationContext(),"network not available",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onNoteClick(int position) {
        System.out.println("asddadada" + position);
        System.out.println("count value " + count);
        j.count--;
        //stem.out.println("menu posi "+menu.get(position).toString());
        menu.remove(position);
        //stem.out.println("menu posi after deletion"+menu.get(position).toString());
        j.tot = j.tot - Integer.parseInt(j.y.get(position));
        j.y.remove(position);
        j.x.remove(position);

        finish();
        startActivity(getIntent());
    }
}
