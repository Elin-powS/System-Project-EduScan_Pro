package com.example.eduscan_pro;

import static android.widget.Toast.makeText;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Recycle_view_of_Batch extends AppCompatActivity {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    ImageView imageMenu;

    private FirebaseUser firebaseuser;
    private ProgressBar progressBar;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton add_semester;

    semester_model_Adapter show_adapter;
    ArrayList<semester_model> list;
    int check=0,x=0;
    EditText Degree,Year,Semester;
    TextView Tittle_add_semester;
    String degree,year,semester;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_of_batch);

        getWindow().setStatusBarColor(ContextCompat.getColor(Recycle_view_of_Batch.this,R.color.teal_700));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout = findViewById(R.id.drawer_layout_2);
        navigationView = findViewById(R.id.nav_View2);
        imageMenu = findViewById(R.id.imageMenu);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user == null) {
            startActivity(new Intent(Recycle_view_of_Batch.this,Log_In.class));
            finish();
        }




        toggle = new ActionBarDrawerToggle(Recycle_view_of_Batch.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                if(item.getItemId() ==  R.id.m_Home_Recycle)
                {
                    makeText(Recycle_view_of_Batch.this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() ==  R.id.m_Profile_Recycle) {
                    makeText(Recycle_view_of_Batch.this, "Personal Information", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Recycle_view_of_Batch.this,Recycle_view_of_Batch.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App_Recycle) {
                    makeText(Recycle_view_of_Batch.this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Recycle_view_of_Batch.this,Recycle_view_of_Batch.class) ;
                    startActivity(intent);
                } else if ( item.getItemId() ==  R.id.mlog_out_recycle) {
                    firebaseAuth.signOut();
                    makeText(Recycle_view_of_Batch.this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Recycle_view_of_Batch.this,Log_In.class) ;
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



        Auto_Semester_Add();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);




        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager").child(firebaseuser.getUid()).child("Semester Information");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        show_adapter = new semester_model_Adapter(this,list);
        recyclerView.setAdapter(show_adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        String child = dataSnapshot2.getKey();
                        if(child.equals("Dummy Data")) {
                            semester_model user = dataSnapshot2.getValue(semester_model.class);
                            //user.setKey(dataSnapshot2.getKey());
                            list.add(user);
                            progressBar.setVisibility(View.GONE);
                            System.out.println("");
                        }
                        }
                    }
                }

                show_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        semester_model_Adapter.setClickListener(new semester_model_Adapter.ClickListener(){
            @Override
            public View.OnClickListener onItemClick(int position, View view) {

                Toast.makeText(Recycle_view_of_Batch.this, "Semester Page", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Recycle_view_of_Batch.this, MainActivity.class);
                startActivity(intent);
                //intent.putExtra("Vehicle_Size",arrContact_model.get(position).vehicle_siz);intent.putExtra("Vehicle",arrContact_model.get(position).vehicle_nam);
                return null;
            }
        });






             add_semester = findViewById(R.id.Add_Semester);
        add_semester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(Recycle_view_of_Batch.this);
                dialog.setContentView(R.layout.add_semester_layout);

                Degree =(EditText) dialog.findViewById(R.id.degree);
                Year =(EditText) dialog.findViewById(R.id.year);
                Semester =(EditText)dialog.findViewById(R.id.semester);
                Tittle_add_semester = (TextView)dialog.findViewById(R.id.add_edit_semester);
                Tittle_add_semester.setText("Enter New Semester");
                Button insert_semester = dialog.findViewById(R.id.button_insert);

                insert_semester.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        x=1;

                        degree = Degree.getText().toString().trim();
                        year = Year.getText().toString().trim();
                        semester = Semester.getText().toString().trim();

                        if (degree.isEmpty()) {
                            Degree.setError("Required!");
                            Degree.requestFocus();
                            x=0;
                            return;
                        }
                        if (year.isEmpty()) {
                            Year.setError("Required!");
                            Year.requestFocus();
                            x=0;
                            return;
                        }
                        if(semester.isEmpty()){
                            Semester.setError("Required!");
                            Semester.requestFocus();
                            x=0;
                            return;
                        }



                        if(x==1)
                        {
                            Insert_Semester();
                        }
                    }

                });

                dialog.setCancelable(true);
                dialog.show();

            }
        });



    }

    private  void     Insert_Semester(){


        HashMap<String, Object> map = new HashMap<>();

        map.put("Degree",degree.trim());
        map.put("Year",year.trim());
        map.put("Semester",semester.trim());

        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child(degree).child( year + " & " + semester).child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(Recycle_view_of_Batch.this, "New Semester adding Successfully Done.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Recycle_view_of_Batch.this, Recycle_view_of_Batch.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Recycle_view_of_Batch.this, "New Semester adding failed, Try again!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @SuppressLint("SuspiciousIndentation")
    private  void Auto_Semester_Add(){

        HashMap<String, Object> map = new HashMap<>();

        map.put("Degree","BSC");
        map.put("Year","First Year");
        map.put("Semester","First Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
              databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("First Year & First Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });


        map.put("Degree","BSC");
        map.put("Year","First Year");
        map.put("Semester","Second Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("First Year Second Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        map.put("Degree","BSC");
        map.put("Year","Second Year");
        map.put("Semester","First Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Second Year & First Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });


        map.put("Degree","BSC");
        map.put("Year","Second Year");
        map.put("Semester","Second Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Second Year & Second Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });


        map.put("Degree","BSC");
        map.put("Year","Third Year");
        map.put("Semester","First Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Third Year & First Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });


        map.put("Degree","BSC");
        map.put("Year","Third Year");
        map.put("Semester","Second Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Third Year & Second Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        map.put("Degree","BSC");
        map.put("Year","Fourth Year");
        map.put("Semester","First Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Fourth Year & First Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        map.put("Degree","BSC");
        map.put("Year","Fourth Year");
        map.put("Semester","Second Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("BSC").child("Fourth Year & Second Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        map.put("Degree","MSC");
        map.put("Year","First Year");
        map.put("Semester","First Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("MSC").child("First Year & First Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        map.put("Degree","MSC");
        map.put("Year","First Year");
        map.put("Semester","Second Semester");

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager");
        databaseReference.child(firebaseuser.getUid()).child("Semester Information").child("MSC").child("First Year & Second Semester").child("Dummy Data")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!(task.isSuccessful())) {
                            check= 1;
                        }
                    }
                });

        if(check==1){
            Toast.makeText(Recycle_view_of_Batch.this, "Failed to show semester! Please check your credentials.", Toast.LENGTH_SHORT).show();
        }


    }
}