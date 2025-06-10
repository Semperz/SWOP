package com.example.swop.shop.cartwindow;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swop.R;
import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.models.ProductDto;

public class CartProductCardViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageProduct;
    private final TextView textName;
    private final TextView textPrice;
    private final TextView textQuantity;
    private final Button btnIncrease;
    private final Button btnDecrease;

    public CartProductCardViewHolder(View itemView) {
        super(itemView);
        imageProduct = itemView.findViewById(R.id.image_product);
        textName = itemView.findViewById(R.id.text_product_name);
        textPrice = itemView.findViewById(R.id.text_product_price);
        textQuantity = itemView.findViewById(R.id.text_quantity);
        btnIncrease = itemView.findViewById(R.id.btn_increase);
        btnDecrease = itemView.findViewById(R.id.btn_decrease);
    }

    public void bind(ProductDto product, int quantity, OnCartItemActionListener listener) {
        textName.setText(product.getName().toLowerCase());
        textPrice.setText("$" + product.getPrice().toPlainString());
        textQuantity.setText(String.valueOf(quantity));
        String imageUrl = RetrofitClient.getBaseUrl() + "img/" + product.getImage();

        Glide.with(itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(imageProduct);

        btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onIncrease(product);
        });

        btnDecrease.setOnClickListener(v -> {
            if (listener != null) listener.onDecrease(product);
        });
    }

    public interface OnCartItemActionListener {
        void onIncrease(ProductDto product);
        void onDecrease(ProductDto product);
    }
}