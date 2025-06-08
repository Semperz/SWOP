package com.example.swop.shop.productslist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swop.R;
import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.models.ProductDto;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageProduct;
    private final TextView textName;
    private final TextView textPrice;

    public ProductCardViewHolder(View itemView) {
        super(itemView);
        imageProduct = itemView.findViewById(R.id.image_product);
        textName = itemView.findViewById(R.id.text_product_name);
        textPrice = itemView.findViewById(R.id.text_product_price);
    }

    public void bind(ProductDto product, OnItemClickListener listener) {
        textName.setText(product.getName().toLowerCase());

        // Formatear precio
        String priceText = "$" + product.getPrice().toPlainString();
        textPrice.setText(priceText);
        String imageName = product.getImage();
        String imageUrl = RetrofitClient.getBaseUrl()+ "img/" + imageName;

        // Cargar imagen con Glide
        Glide.with(itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(imageProduct);

        itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(product);
        });
    }

    public interface OnItemClickListener {
        void onItemClick(ProductDto product);
    }
}
