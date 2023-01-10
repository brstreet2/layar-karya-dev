package com.example.layarkaryadev;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadMovieFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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
    public View uploadMovieView;
    private DatabaseReference databaseReference;
    private int contentCount;
    public static int count;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadMovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadMovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadMovieFragment newInstance(String param1, String param2) {
        UploadMovieFragment fragment = new UploadMovieFragment();
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
        uploadMovieView =  inflater.inflate(R.layout.fragment_upload_movie, container, false);
        txtFileSelected = uploadMovieView.findViewById(R.id.txtFileSelected);
        movieDescription = uploadMovieView.findViewById(R.id.movie_description);
        dbReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("movies");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("movies");
        mAuth = FirebaseAuth.getInstance();
        btnBrowse = uploadMovieView.findViewById(R.id.btnBrowse);
        btnUpload = uploadMovieView.findViewById(R.id.btnUpload);
        Spinner spinner = uploadMovieView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<>();
        categories.add("Action");
        categories.add("Adventure");
        categories.add("Comedy");
        categories.add("Romance");
        categories.add("Horror");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

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
        return uploadMovieView;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        movieCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getContext(), "Selected: " + movieCategory, Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            movieUri = data.getData();

            String path = null;
            Cursor cursor = null;
            int column_index_data;
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID
                    , MediaStore.Video.Thumbnails.DATA};
            final String orderby = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            cursor = getActivity().getContentResolver().query(movieUri, projection, null, null, orderby);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                path = cursor.getString(column_index_data);
                movieTitle = FilenameUtils.getBaseName(getFilename(movieUri, getActivity().getApplicationContext()));
            }
            txtFileSelected.setText(movieTitle);
        }
    }

    public void firebaseUpload(View v) {
        if (txtFileSelected.equals("No File Selected")) {
            Toast.makeText(getContext(), "Please select a file!", Toast.LENGTH_SHORT).show();
        } else {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getContext(), "File is uploading...", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {
        if (movieUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                                startThumbnailUpload();
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
            Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void startThumbnailUpload() {
        Fragment thumbnailUpload = new UploadThumbnailFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("currentId", currentId);
        bundle.putString("thumbnailsName", movieTitle);
        thumbnailUpload.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, thumbnailUpload);
        fragmentTransaction.commit();
        addContentCount();
        Toast.makeText(getContext(), "Movie has been successfully uploaded, Please upload movie's thumbnail!", Toast.LENGTH_LONG).show();
    }

    private void addContentCount() {
        databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
                .child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contentCount = dataSnapshot.child("contentCount").getValue(int.class);
                count = contentCount + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("contentCount").setValue(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}