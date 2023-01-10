package com.example.layarkaryadev;

import android.widget.ImageView;

import com.example.layarkaryadev.Model.ProductModel;

public interface ProductClickListener {
    void onProductClick(ProductModel productModel, ImageView imageView);
}
