package com.example.layarkaryadev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layarkaryadev.Model.MovieUploadDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public TextView toolbarTitle;
    public ImageView toolbarBtn;
    public Uri movieUri;
    public EditText txtFileSelected;
    public String movieCategory;
    public String movieTitle;
    public String currentId;
    public StorageReference mStorageRef;
    public StorageTask mUploadTask;
    public DatabaseReference dbReference;
    public EditText movieDescription;
    public FirebaseAuth mAuth;
    public ImageView btnBrowse;
    public CardView btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_upload);
        txtFileSelected = findViewById(R.id.txtFileSelected);
        movieDescription = findViewById(R.id.movie_description);
        toolbarTitle = findViewById(R.id.navbar_global);
        toolbarTitle.setText(R.string.uploadMovie);
        toolbarBtn = findViewById(R.id.toolbar_back_arrow);
        dbReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("movies");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("movies");
        mAuth = FirebaseAuth.getInstance();
        btnBrowse = findViewById(R.id.btnBrowse);
        btnUpload = findViewById(R.id.btnUpload);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<>();
        categories.add("Action");
        categories.add("Adventure");
        categories.add("Comedy");
        categories.add("Romance");
        categories.add("Horror");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        toolbarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFiles(view);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUpload(view);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MovieUploadActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        movieCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected: " + movieCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openFiles(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @SuppressLint("Range")
    private String getFilename(Uri uri, Context context) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null) {
                res = uri.getPath();
                int cutt = res.lastIndexOf('/');
                if (cutt != -1) {
                    res = res.substring(cutt + 1);
                }
            }
        }

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            movieUri = data.getData();

            String path = null;
            Cursor cursor = null;
            int column_index_data;
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID
                    , MediaStore.Video.Thumbnails.DATA};
            final String orderby = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            cursor = MovieUploadActivity.this.getContentResolver().query(movieUri, projection, null, null, orderby);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                path = cursor.getString(column_index_data);
                movieTitle = FilenameUtils.getBaseName(getFilename(movieUri, getApplicationContext()));
            }
            txtFileSelected.setText(movieTitle);
        }
    }

    public void firebaseUpload(View v) {
        if (txtFileSelected.equals("No File Selected")) {
            Toast.makeText(this, "Please select a file!", Toast.LENGTH_SHORT).show();
        } else {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(this, "File is uploading...", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {
        if (movieUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Movie is still uploading...");
            progressDialog.show();
            final StorageReference storageReference = mStorageRef.child(movieTitle);
            mUploadTask = storageReference.putFile(movieUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String movie_url = uri.toString();
                            String uploaderId = mAuth.getCurrentUser().getUid();
                            MovieUploadDetails movieUploadDetails = new MovieUploadDetails("", "", "", movie_url, txtFileSelected.getText().toString(),
                                    movieDescription.getText().toString(), movieCategory, uploaderId);
                            String uploadId = dbReference.push().getKey();
                            dbReference.child(uploadId).setValue(movieUploadDetails);
                            currentId = uploadId;
                            progressDialog.dismiss();
                            if(currentId.equals(uploadId)){
                                startThumbnailActivity();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void startThumbnailActivity() {
        Intent intent = new Intent(MovieUploadActivity.this, ThumbnailUploadActivity.class);
        intent.putExtra("currentId", currentId);
        intent.putExtra("thumbnailsName", movieTitle);
        startActivity(intent);
        Toast.makeText(this, "Movie has been successfully uploaded, Please upload movie's thumbnail!", Toast.LENGTH_LONG).show();
    }
}




