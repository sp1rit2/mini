package com.example.cc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.example.cc.R.id.findpath;
import static com.example.cc.R.id.nav_view;

public class StudentDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerAdapter.OnNoteListner {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private DrawerLayout drawer;
    List<shops> l;
    List<String> u;
    List<Shopkeeper> verificationStatus;
    long status[];
    ItemnPrice i = new ItemnPrice();
    TextView t;
    int temp=0;
    ImageView img;
    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        builder=new AlertDialog.Builder(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView;
        navigationView = findViewById(nav_view);
        System.out.println("nav_view " + navigationView);
        navigationView.setNavigationItemSelectedListener(StudentDashboard.this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(StudentDashboard.this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        temp=1;
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Shopkeeper");

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("temp test "+temp);
                l = new ArrayList<>();
                u = new ArrayList<>();
                status = new long[11];
                int i = 0;
                verificationStatus = new ArrayList<>();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //System.out.println(d);
                    u.add(d.getKey());
                    System.out.println("asdasd "+d.child("sname").getValue().toString());
                    //verificationStatus.add(new Shopkeeper(d.child("statusOfVerification").getValue().toString()));
                    int flag = Integer.parseInt(d.child("flag").getValue().toString());
                    String sname = d.child("sname").getValue().toString();
                    System.out.println("sname " + d.child("sname").getValue());
                    status[i] = Integer.parseInt(d.child("statusOfVerification").getValue().toString());
                    System.out.println("status " + i + " " + status[i]);
                    i++;

                    img=findViewById(R.id.img);
                    if (flag == 2) {
                        l.add(new shops(sname));
                    }
                }
                System.out.println(dataSnapshot.getChildrenCount());
                System.out.println("here " + dataSnapshot.getChildren().iterator().toString());


//                System.out.println("uid list " + u.get(0));


//                l.add(new shops("asd", R.drawable.common_full_open_on_phone));
//                l.add(new shops("asd", R.drawable.common_full_open_on_phone));


                RecyclerView rc = findViewById(R.id.rv);
                RecyclerAdapter ra = new RecyclerAdapter(getApplicationContext(), l, StudentDashboard.this);
                rc.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                rc.setAdapter(ra);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();


        DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference("Student").child(uid).child("uname");
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                View headerView = navigationView.getHeaderView(0);
                System.out.println("headerView "+headerView);
                TextView p = headerView.findViewById(R.id.user);
                p.setText(dataSnapshot.getValue().toString());
                TextView t;
                t = headerView.findViewById(R.id.email);
                t.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                i.y.clear();
                i.x.clear();
                i.checkForOrder = 0;
                i.count=0;
                i.tot=0;
                Toast.makeText(getApplicationContext(),"You can order from any shop",Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

//            case R.id.linearLayout:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoplistFragment()).commit();
//                break;
            case R.id.nav_order:
                startActivity(new Intent(getApplicationContext(), Order.class));
                break;
            case R.id.nav_location:
                startActivity(new Intent(getApplicationContext(), LocationFragment.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(), SettingFragment.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    long backButtonCount = 0;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long t = System.currentTimeMillis();
            if (t - backButtonCount < 1500) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                backButtonCount = t;
                Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.help:

                help();
                return true;

            case R.id.logout:

                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void help()
    {
        builder.setMessage("xyz@gmail.com......................................................" +
                "\n................................." +
                "\n..................................................." +
                "\n...............................................................") .setTitle("Contact us!").setCancelable(true);
        AlertDialog alert=builder.create();
        alert.show();
    }
    public void logout() {


        Toast.makeText(this, "Bye!!!", Toast.LENGTH_SHORT).show();
        SharedPreferences mPreferences = getSharedPreferences(uid, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove("UserName");
        editor.remove("PassWord");
        editor.commit();
        i.y.clear();
        i.x.clear();
        i.checkForOrder = 0;
        i.count=0;
        i.tot=0;
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }


    @Override
    public void onNoteClick(int position) {
        System.out.println(position);
        Intent intent;

        if (status[position] == 0) {
            startActivity(new Intent(getApplicationContext(), ComingSoon.class));
        } else {
            i.shoplist12[i.checkForOrder] = u.get(position);
            i.shoplist.add(u.get(position));
            i.checkForOrder++;
            System.out.println("i.tot " + i.tot);
//            if (i.checkForOrder > 1 && i.shoplist12[0] != u.get(position))                         //can order from 1 shop at a time
//            {
//                Toast.makeText(getApplicationContext(), "Can order from 1 shop at a time bro", Toast.LENGTH_LONG).show();
//                return;
//            }
            intent = new Intent(getApplicationContext(), shopProfile.class);
            intent.putExtra("uidValue", u.get(position));
            startActivity(intent);
        }
//        switch (position)
//        {
//            case 0:
//                if(status[0]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 1:
//                if(status[1]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(1));
//                    startActivity(intent);
//                }
//                break;
//            case 2:
//                if(status[2]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(2));
//                    startActivity(intent);
//                }
//                break;
//            case 3:
//                if(status[3]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 4:
//                if(status[4]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 5:
//                if(status[5]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 6:
//                if(status[6]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 7:
//                if(status[7]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 8:
//                if(status[8]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 9:
//                if(status[9]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//            case 10:
//                if(status[10]==0)
//                {
//                    startActivity(new Intent(getApplicationContext(),ComingSoon.class));
//                }
//                else {
//                    intent = new Intent(getApplicationContext(), shopProfile.class);
//                    intent.putExtra("uidValue", u.get(0));
//                    startActivity(intent);
//                }
//                break;
//
//        }
    }


}


