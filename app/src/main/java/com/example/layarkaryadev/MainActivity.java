package com.example.layarkaryadev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Button btnLogOut;
    public Button btnFormUpload;
    public FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        btnLogOut = findViewById(R.id.btnLogout);
//        btnFormUpload = findViewById(R.id.btnFormUpload);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_discover);
        }

//        btnLogOut.setOnClickListener(view -> {
//            mAuth.signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        });
//
//        btnFormUpload.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, MovieUploadActivity.class));
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_discover:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                toolbar.setTitle(getString(R.string.my_content));
                break;

            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                toolbar.setTitle(getString(R.string.my_profile));
                break;

            case R.id.nav_upload:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UploadMovieFragment()).commit();
                toolbar.setTitle(getString(R.string.uploadMovie));
                break;

            case R.id.nav_market:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MarketFragment()).commit();
                toolbar.setTitle(getString(R.string.marketplace));
                break;

            case R.id.nav_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Sign Out Confirmation")
                        .setMessage("Proceed to Sign Out?")
                        .setIcon(R.drawable.ic_baseline_arrow_back_24)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}