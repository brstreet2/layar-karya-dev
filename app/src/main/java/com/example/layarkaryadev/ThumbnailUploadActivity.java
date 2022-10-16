package com.example.layarkaryadev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;



public class ThumbnailUploadActivity extends AppCompatActivity {

    public TextView toolbarTitle;
    public Uri movieThumbnailUri;
    public String thumbnailUrl;
    public ImageView thumbnailImg;
    public StorageReference storageThumbnails;
    public DatabaseReference dbReference;
    public TextView txtFileSelected;
    public StorageTask mStorageTask;
    public RadioButton radioNo, radioLatest, radioPopular, radioSlider;
    public ImageView btnBrowse;
    public CardView btnUpload;
    public DatabaseReference updateDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail_upload);
        toolbarTitle = findViewById(R.id.navbar_global_no_btn);
        toolbarTitle.setText(R.string.uploadThumbnail);
        txtFileSelected = findViewById(R.id.txtFileSelected);
        thumbnailImg = findViewById(R.id.imageView);
        radioNo = findViewById(R.id.radioNo);
        radioLatest = findViewById(R.id.radioLatest);
        radioPopular = findViewById(R.id.radioPopular);
        radioSlider = findViewById(R.id.radioSlider);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnUpload = findViewById(R.id.btnUpload);
        dbReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("movies");
        storageThumbnails = FirebaseStorage.getInstance().getReference().child("movie_thumbnails");
        String currentId = getIntent().getExtras().getString("currentId");
        updateDb = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies").child(currentId);

        radioNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDb.child("movie_type").setValue("");
                updateDb.child("movie_slide").setValue("");
                Toast.makeText(ThumbnailUploadActivity.this, "No tags selected", Toast.LENGTH_SHORT).show();
            }
        });

        radioLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latestMovies = radioLatest.getText().toString();
                updateDb.child("movie_slide").setValue(latestMovies);
                Toast.makeText(ThumbnailUploadActivity.this, "Selected tag: " + latestMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String popularMovies = radioPopular.getText().toString();
                updateDb.child("movie_slide").setValue(popularMovies);
                Toast.makeText(ThumbnailUploadActivity.this, "Selected tag: " + popularMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slideMovies = radioPopular.getText().toString();
                updateDb.child("movie_slide").setValue(slideMovies);
                Toast.makeText(ThumbnailUploadActivity.this, "Selected tag: " + slideMovies, Toast.LENGTH_SHORT).show();
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

    public void openFiles(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == RESULT_OK && data.getData() != null) {
            movieThumbnailUri = data.getData();
            try {
                String thumbnailName = getFilename(movieThumbnailUri);
                txtFileSelected.setText(thumbnailName);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), movieThumbnailUri);
                thumbnailImg.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    private String getFilename(Uri uri) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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

    private void uploadFiles() {
        if (movieThumbnailUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Movie is still uploading...");
            progressDialog.show();
            String movieTitle = getIntent().getExtras().getString("thumbnailsName");
            final StorageReference storageReference = storageThumbnails.child(movieTitle + "." + getFileExtension(movieThumbnailUri));
            storageReference.putFile(movieThumbnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            thumbnailUrl = uri.toString();
                            updateDb.child("movie_thumbnail").setValue(thumbnailUrl);
                            progressDialog.dismiss();
                            Toast.makeText(ThumbnailUploadActivity.this, "Thumbnail Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ThumbnailUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");;
                }
            });
        }
    }

    public void firebaseUpload(View view){
        if (txtFileSelected.equals("No File Selected")) {
            Toast.makeText(this, "Please select a file!", Toast.LENGTH_SHORT).show();
        } else {
            if (mStorageTask != null && mStorageTask.isInProgress()) {
                Toast.makeText(this, "File is uploading...", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
}