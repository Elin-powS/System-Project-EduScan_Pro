package com.example.eduscan_pro;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.IOException;
import java.io.OutputStream;

public class Word_File_Creation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    ImageView imageMenu;

    FloatingActionButton camera,erase,copy;
    Button Convert_DOC_button;
    EditText recogText;
    Uri imageUri;
    TextRecognizer textRecognizer;
    private FirebaseAuth firebaseAuth;
    private String recognizerText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_file_creation);

        getWindow().setStatusBarColor(ContextCompat.getColor(Word_File_Creation.this,R.color.teal_700));

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
        Convert_DOC_button = findViewById(R.id.Convert_Doc);


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(Word_File_Creation.this,"There is no Text to Copy!",Toast.LENGTH_SHORT).show();
                }
                else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Word_File_Creation.this.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",recogText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(Word_File_Creation.this,"Text Copy to Clipborad",Toast.LENGTH_SHORT).show();
                }
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(Word_File_Creation.this,"There is no Text to Copy!",Toast.LENGTH_SHORT).show();
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
                ImagePicker.Companion.with(Word_File_Creation.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

        if (user == null) {
            startActivity(new Intent(Word_File_Creation.this,Log_In.class));
            finish();
        }

        toggle = new ActionBarDrawerToggle(Word_File_Creation.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                if(item.getItemId() ==  R.id.m_Home)
                {
                    makeText(Word_File_Creation.this, "Clicked to Home", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() ==  R.id.m_Profile) {
                    makeText(Word_File_Creation.this, "Personal Information", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Word_File_Creation.this,Recycle_view_of_Batch.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App) {
                    makeText(Word_File_Creation.this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Word_File_Creation.this, Word_File_Creation.class);
                    startActivity(intent);
                } else if ( item.getItemId() ==  R.id.mlog_out) {
                    firebaseAuth.signOut();
                    makeText(Word_File_Creation.this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Word_File_Creation.this,Log_In.class) ;
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

        Convert_DOC_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docInput = recogText.getText().toString().trim();
                if (!docInput.isEmpty()) {
                    createAndSaveDocxFile(docInput);
                    Toast.makeText(Word_File_Creation.this, "DOCX creation initiated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Word_File_Creation.this, "There is no text to create DOCX!", Toast.LENGTH_SHORT).show();
                }
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
                InputImage inputImage = InputImage.fromFilePath(Word_File_Creation.this,imageUri);

                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {

                                recognizerText= text.getText();
                                recogText.setText(recognizerText);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Word_File_Creation.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    private final ActivityResultLauncher<Intent> createDocxLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            try {
                                String content = recogText.getText().toString().trim();
                                createAndSaveDocx(this, uri, content);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to save DOCX", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    private void createAndSaveDocx(Context context, Uri uri, String content) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Check if content is not empty before processing
        if (!content.isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
        }

        // Write the document to the provided URI
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        document.write(outputStream);
        document.close();
        outputStream.close();
        recogText.setText("");

        Toast.makeText(context, "DOCX created and saved to Downloads", Toast.LENGTH_SHORT).show();
    }

    private void createAndSaveDocxFile(String content) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        intent.putExtra(Intent.EXTRA_TITLE, "example.docx"); // Change the file name as needed
        createDocxLauncher.launch(intent);

    }


}