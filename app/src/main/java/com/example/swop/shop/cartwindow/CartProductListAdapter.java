package com.example.swop.shop.cartwindow;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.R;

import java.util.ArrayList;

import java.util.List;

public class CartProductListAdapter extends RecyclerView.Adapter<CartProductCardViewHolder> {

    private final List<CartItem> cartItems = new ArrayList<>();
    private CartProductCardViewHolder.OnCartItemActionListener listener;

    @NonNull
    @Override
    public CartProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_product_card_view_holder, parent, false);
        return new CartProductCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductCardViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item.getProduct(), item.getQuantity(), listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setOnCartItemActionListener(CartProductCardViewHolder.OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CartItem> items) {
        cartItems.clear();
        if (items != null) {
            cartItems.addAll(items);
        }
        notifyDataSetChanged();
    }
}
