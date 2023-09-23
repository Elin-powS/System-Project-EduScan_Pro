package com.example.eduscan_pro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

/*
* Sign up activity */
public class Sign_Up extends AppCompatActivity {

    private EditText Fullname, Email,username, password, confirm_password,University,Department;
    private Button B3;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView Exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setStatusBarColor(ContextCompat.getColor(Sign_Up.this,R.color.teal_700));


        firebaseAuth = FirebaseAuth.getInstance();

        Fullname = (EditText) findViewById(R.id.Fullname);
        University = (EditText) findViewById(R.id.university);
        Email = (EditText) findViewById(R.id.Email);
        Department = (EditText) findViewById(R.id.department);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        Exit=(TextView) findViewById(R.id.exit);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Sign_Up.this, "Exit", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(Sign_Up.this,Log_In.class);
                startActivity(intent);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        B3 = (Button) findViewById(R.id.B3);
        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = Fullname.getText().toString().trim();
                String email = Email.getText().toString().trim();
                String university  = University.getText().toString().trim();
                String Username = username.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Confirm_password = confirm_password.getText().toString().trim();
                String department = Department.getText().toString().trim();

                if (name.isEmpty()) {
                    Fullname.setError("Required");
                    Fullname.requestFocus();
                    return;
                }
                if (university.isEmpty()) {
                    University.setError("Required");
                    University.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    Email.setError("Required");
                    Email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Email.setError("Please Provide valid email!");
                    Email.requestFocus();
                    return;
                }
                if (department.isEmpty()) {
                    Department.setError("Required");
                    Department.requestFocus();
                    return;
                }
                if (Username.isEmpty()) {
                    username.setError("Required");
                    username.requestFocus();
                    return;
                }
                if (Password.isEmpty()) {
                    password.setError("Required");
                    password.requestFocus();
                    return;
                }
                if (Password.length() < 6) {
                    password.setError("Minimum password length should be 6 character!");
                    password.requestFocus();
                    return;
                }
                if (Confirm_password.isEmpty() || !Password.equals((Confirm_password))) {
                    confirm_password.setError("Not Matched");
                    confirm_password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Sign_In(email, Password);
            }
        });
    }

    private void Sign_In(String Email, String Password) {
        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();
                            updateUi(uid, Email);
                        } else {
                            Toast.makeText(Sign_Up.this, "Sign In Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUi(String uid, String email) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("name", Fullname.getText().toString().trim());
        map.put("email", Email.getText().toString().trim());
        map.put("University", University.getText().toString().trim());
        map.put("Department",Department.getText().toString().trim());
        map.put("Username", username.getText().toString().trim());



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Department Manager");
        reference.child(uid).child("Manager's information")
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Sign_Up.this, "Sign Up Successfully Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Sign_Up.this, MainActivity.class));
                            finish();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Exception e = task.getException();
                            if(e == null) {
                                Toast.makeText(Sign_Up.this, "Sign Up Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(Sign_Up.this, "Sign Up Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}