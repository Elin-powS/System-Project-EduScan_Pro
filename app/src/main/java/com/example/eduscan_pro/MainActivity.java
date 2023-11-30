package com.example.eduscan_pro;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    ImageView imageMenu;

    FloatingActionButton camera,erase,copy;
    EditText recogText;
    Uri imageUri;
    Button Convert_PDF_button;
    TextRecognizer textRecognizer;
    private FirebaseAuth firebaseAuth;
    private String recognizerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.mint_700));

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
        Convert_PDF_button = findViewById(R.id.Convert_PDF);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this,"There is no Text to Copy!",Toast.LENGTH_SHORT).show();
                }
                else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",recogText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(MainActivity.this,"Text Copy to Clipborad",Toast.LENGTH_SHORT).show();
                }
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recogText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this,"There is no Text to Erase!",Toast.LENGTH_SHORT).show();
                }
                else {
                    recogText.setText("");
                }
            }
        });

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

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
                    intent = new Intent(MainActivity.this,Recycle_view_of_Batch.class) ;
                    startActivity(intent);

                }
                else if (item.getItemId() ==  R.id.m_About_App) {
                    makeText(MainActivity.this, "About App", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, MainActivity.class);
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

        Convert_PDF_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pdfInput = recogText.getText().toString().trim();
                if (!pdfInput.isEmpty()) {
                    createAndSavePdf(MainActivity.this, "example.pdf", pdfInput);
                } else {
                    Toast.makeText(MainActivity.this, "There is no text to make PDF!", Toast.LENGTH_SHORT).show();
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
                InputImage inputImage = InputImage.fromFilePath(MainActivity.this,imageUri);

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
                                Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private final ActivityResultLauncher<Intent> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            try {
                                PdfDocument document = new PdfDocument();
                                // Decrease page size slightly and add a margin at the top
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(550, 800, 1).create(); // Slightly smaller size (550x800)
                                PdfDocument.Page page = document.startPage(pageInfo);
                                Canvas canvas = page.getCanvas();
                                Paint paint = new Paint();
                                paint.setColor(Color.BLACK);
                                paint.setTextSize(12);

                                String content = recogText.getText().toString().trim();
                                // Split content into lines to fit on the page
                                String[] lines = content.split("\n");
                                int y = 100; // Initial Y position for text (added margin at the top)

                                for (String line : lines) {
                                    canvas.drawText(line, 50, y, paint);
                                    y += 20; // Increase Y position for next line
                                }

                                document.finishPage(page);

                                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                                document.writeTo(outputStream);
                                document.close();
                                outputStream.close();
                                recogText.setText("");
                                Toast.makeText(this, "PDF created and saved to Downloads", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    private void createAndSavePdf(Context context, String fileName, String content) {
        // Use Storage Access Framework to save the PDF file to Downloads directory
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));


        // Launch the createPdfLauncher with 'content'
        createPdfLauncher.launch(intent);

    }

}

