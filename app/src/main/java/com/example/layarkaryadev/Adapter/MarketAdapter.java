package com.example.layarkaryadev.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layarkaryadev.Model.ProductModel;
import com.example.layarkaryadev.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {
    public MarketAdapter(Context context, ArrayList<ProductModel> product) {
        this.context = context;
        this.product = product;
    }

    private ArrayList<ProductModel> product;
    private Context context;

    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_market, parent,false);

        return new MarketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, int position) {
        holder.productTitle.setText(product.get(position).getProduct_name());
        holder.productDesc.setText(product.get(position).getProduct_description());
        holder.productPrice.setText(String.valueOf(product.get(position).getProduct_price()));
        Picasso.get().load(product.get(position).getProduct_img()).resize(300, 0).into(holder.imgMedia, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productTitle;
        private TextView productDesc;
        private TextView productPrice;
        private ImageView imgMedia;
        private ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = (TextView) itemView.findViewById(R.id.productTitle);
            productDesc = (TextView) itemView.findViewById(R.id.productDesc);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            imgMedia = (ImageView) itemView.findViewById(R.id.productImg);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
