package com.example.swop.shop.productslist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.R;
import com.example.swop.data.remote.models.ProductDto;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private final List<ProductDto> productList = new ArrayList<>();
    private ProductCardViewHolder.OnItemClickListener listener;

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view_holder, parent, false);
        return new ProductCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        holder.bind(productList.get(position), listener);
        ProductDto product = productList.get(position);
        Log.d("Adapter", "Mostrando producto: " + product.getName());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnItemClickListener(ProductCardViewHolder.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProductDto> products) {
        productList.clear();
        if (products != null) {
            productList.addAll(products);
        }
        notifyDataSetChanged();
    }
}
