package com.example.eduscan_pro;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;


public class Attendance_Activity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    ImageView imageMenu;

    FloatingActionButton camera,erase,copy;
    EditText recogText;
    Uri imageUri;
    TextRecognizer textRecognizer;
    private FirebaseAuth firebaseAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        getWindow().setStatusBarColor(ContextCompat.getColor( Attendance_Activity .this,R.color.mint_700));

        //recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager((this)));

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        imageMenu = findViewById(R.id.imageMenu);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //camera part

        erase = findViewById(R.id.Erase);
        camera = findViewById(R.id.Camera);
        copy = findViewById(R.id.Copy);
        recogText=findViewById(R.id.showdata);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText( Attendance_Activity .this,"There is no Text to Copy!",Toast.LENGTH_SHORT).show();
                }
                else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService( Attendance_Activity .this.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",recogText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText( Attendance_Activity .this,"Text Copy to Clipborad",Toast.LENGTH_SHORT).show();
                }
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText( Attendance_Activity .this,"There is no Text to Copy!",Toast.LENGTH_SHORT).show();
                }
                else {
                    recogText.setText("-");
                }
            }
        });

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(Attendance_Activity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

        if (user == null) {
            startActivity(new Intent(Attendance_Activity.this,Log_In.class));
            finish();
        }

        toggle = new ActionBarDrawerToggle(Attendance_Activity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                if(item.getItemId() ==  R.id.m_Home)
                {
                    makeText( Attendance_Activity .this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() ==  R.id.m_Profile) {
                    makeText( Attendance_Activity .this, "Personal Information", Toast.LENGTH_SHORT).show();
                    intent = new Intent( Attendance_Activity .this,Recycle_view_of_Batch.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App) {
                    makeText( Attendance_Activity .this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent( Attendance_Activity .this,  Attendance_Activity .class);
                    startActivity(intent);
                } else if ( item.getItemId() ==  R.id.mlog_out) {
                    firebaseAuth.signOut();
                    makeText( Attendance_Activity .this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                    intent = new Intent( Attendance_Activity .this,Log_In.class) ;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == Activity.RESULT_OK){
            if(data!=null){
                imageUri = data.getData();

                Toast.makeText(this,"image is Selected",Toast.LENGTH_SHORT).show();

                recognizeText();
            }
        }
        else {
            Toast.makeText(this,"image is not Selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText() {
        if(imageUri!=null){
            try {
                InputImage inputImage = InputImage.fromFilePath( Attendance_Activity .this,imageUri);

                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {

                                String recognizerText= text.getText();
                                recogText.setText(recognizerText);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( Attendance_Activity .this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}