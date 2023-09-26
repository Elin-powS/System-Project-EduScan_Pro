package com.example.eduscan_pro;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    ImageView imageMenu;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.teal_700));

        //recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager((this)));

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        imageMenu = findViewById(R.id.imageMenu);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainActivity.this,Log_In.class));
            finish();
        }

        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                if(item.getItemId() ==  R.id.m_Home)
                {
                    makeText(MainActivity.this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() ==  R.id.m_Profile) {
                    makeText(MainActivity.this, "Personal Information", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,MainActivity.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App) {
                    makeText(MainActivity.this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,MainActivity.class) ;
                    startActivity(intent);
                } else if ( item.getItemId() ==  R.id.mlog_out) {
                    firebaseAuth.signOut();
                    makeText(MainActivity.this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,Log_In.class) ;
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        imageMenu = findViewById(R.id.imageMenu);

        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }
}

  /*
                *  if(item.getItemId() ==  R.id.m_Home)
                {
                    makeText(MainActivity.this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() ==  R.id.m_Profile) {
                    makeText(MainActivity.this, "Personal Information", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,MainActivity.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App) {
                    makeText(MainActivity.this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,MainActivity.class) ;
                    startActivity(intent);
                } else if ( item.getItemId() ==  R.id.mlog_out) {
                    firebaseAuth.signOut();
                    makeText(MainActivity.this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this,Log_In.class) ;
                    startActivity(intent);
                    finish();
                }
                *
                *  switch (item.getItemId()) {


                    case R.id.m_Home:
                        makeText(MainActivity.this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.m_Profile:
                        makeText(MainActivity.this, "Personal Information", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,MainActivity.class) ;
                        startActivity(intent);
                        break;

                    case R.id.m_About_App:
                        makeText(MainActivity.this, "About App", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,MainActivity.class) ;
                        startActivity(intent);
                        break;


                    case R.id.mlog_out:
                        firebaseAuth.signOut();
                        makeText(MainActivity.this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,Log_In.class) ;
                        startActivity(intent);
                        finish();
                        break;
                }
                *
                * */