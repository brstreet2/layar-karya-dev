package com.example.layarkaryadev;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadThumbnailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadThumbnailFragment extends Fragment {
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
    public View uploadThumbnailView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadThumbnailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadThumbnailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadThumbnailFragment newInstance(String param1, String param2) {
        UploadThumbnailFragment fragment = new UploadThumbnailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uploadThumbnailView = inflater.inflate(R.layout.fragment_upload_thumbnail, container, false);
        txtFileSelected = uploadThumbnailView.findViewById(R.id.txtFileSelected);
        thumbnailImg = uploadThumbnailView.findViewById(R.id.imageView);
        radioNo = uploadThumbnailView.findViewById(R.id.radioNo);
        radioLatest = uploadThumbnailView.findViewById(R.id.radioLatest);
        radioPopular = uploadThumbnailView.findViewById(R.id.radioPopular);
        radioSlider = uploadThumbnailView.findViewById(R.id.radioSlider);
        btnBrowse = uploadThumbnailView.findViewById(R.id.btnBrowse);
        btnUpload = uploadThumbnailView.findViewById(R.id.btnUpload);

        dbReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("movies");
        storageThumbnails = FirebaseStorage.getInstance().getReference().child("movie_thumbnails");
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        String currentId = mBundle.getString("currentId");
        updateDb = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies").child(currentId);

        radioNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDb.child("movie_type").setValue("");
                updateDb.child("movie_slide").setValue("");
                Toast.makeText(getContext(), "No tags selected", Toast.LENGTH_SHORT).show();
            }
        });

        radioLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latestMovies = radioLatest.getText().toString();
                updateDb.child("movie_slide").setValue(latestMovies);
                Toast.makeText(getContext(), "Selected tag: " + latestMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String popularMovies = radioPopular.getText().toString();
                updateDb.child("movie_slide").setValue(popularMovies);
                Toast.makeText(getContext(), "Selected tag: " + popularMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slideMovies = radioPopular.getText().toString();
                updateDb.child("movie_slide").setValue(slideMovies);
                Toast.makeText(getContext(), "Selected tag: " + slideMovies, Toast.LENGTH_SHORT).show();
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
        return uploadThumbnailView;
    }

    public void openFiles(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            movieThumbnailUri = data.getData();
            try {
                String thumbnailName = getFilename(movieThumbnailUri);
                txtFileSelected.setText(thumbnailName);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), movieThumbnailUri);
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
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Movie is still uploading...");
            progressDialog.show();
            Bundle mBundle = new Bundle();
            mBundle = getArguments();
            String movieTitle = mBundle.getString("thumbnailsName");
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
                            Toast.makeText(getContext(), "Thumbnail Uploaded", Toast.LENGTH_SHORT).show();
                            Fragment mainFragment = new MainFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                            fragmentTransaction.commit();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
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
            Toast.makeText(getContext(), "Please select a file!", Toast.LENGTH_SHORT).show();
        } else {
            if (mStorageTask != null && mStorageTask.isInProgress()) {
                Toast.makeText(getContext(), "File is uploading...", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
}