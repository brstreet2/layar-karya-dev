package com.example.layarkaryadev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public Button btnLogOut;
    public Button btnFormUpload;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogOut = findViewById(R.id.btnLogout);
        btnFormUpload = findViewById(R.id.btnFormUpload);
        mAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnFormUpload.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MovieUploadActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}