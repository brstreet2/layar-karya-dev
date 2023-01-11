package com.example.layarkaryadev.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layarkaryadev.Model.MovieUploadDetails;
import com.example.layarkaryadev.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    public MovieAdapter(Context context, ArrayList<MovieUploadDetails> movie) {
        this.context = context;
        this.movie = movie;
    }

    private ArrayList<MovieUploadDetails> movie;
    private Context context;

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_movie, parent,false);

        return new MovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        holder.movieTitle.setText(movie.get(position).getMovie_name());
        holder.movieDescription.setText(movie.get(position).getMovie_description());
        Picasso.get().load(movie.get(position).getMovie_thumbnail()).resize(300, 0).into(holder.imgMedia, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

//        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new AlertDialog.Builder(context)
//                        .setTitle("Purchase Confirmation")
//                        .setMessage("Purchase this item for " + String.valueOf(product.get(position).getProduct_price()) + " coin?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                mAuth = FirebaseAuth.getInstance();
//                                databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
//                                        .child(mAuth.getCurrentUser().getUid());
//                                databaseReference.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        currCoin = dataSnapshot.child("coin").getValue(int.class);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        int calc = (currCoin - product.get(position).getProduct_price());
//                                        final int calcResult = calc;
//                                        if (calcResult < 0) {
//                                            new AlertDialog.Builder(context)
//                                                    .setTitle("Purchase Failed")
//                                                    .setMessage("Sorry, you don't have enough coin!")
//                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                        }
//                                                    })
//                                                    .show();
//                                        } else {
//                                            dataSnapshot.getRef().child("coin").setValue(calcResult).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    new AlertDialog.Builder(context)
//                                                            .setTitle("Purchase Success")
//                                                            .setMessage("Your code is: " + product.get(position).getProduct_item())
//                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                }
//                                                            })
//                                                            .show();
//                                                }
//                                            });
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//                            }
//                        }).setNegativeButton("No", null)
//                        .show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return movie.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView movieTitle;
        private TextView movieDescription;
        private ImageView imgMedia;
        private ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            movieDescription = (TextView) itemView.findViewById(R.id.movieDescription);
            imgMedia = (ImageView) itemView.findViewById(R.id.movieImg);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
//            buyBtn = (CardView) itemView.findViewById(R.id.btnBuy);
        }
    }
}
