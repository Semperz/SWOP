package com.example.swop.profile.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.R;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderCardViewHolder> {

    private final List<OrderDto> orderList = new ArrayList<>();
    private final OrderRepository orderRepository;

    public OrderListAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    public OrderCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_order_card_view_holder, parent, false);
        return new OrderCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCardViewHolder holder, int position) {
        holder.bind(orderList.get(position), orderRepository);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void submitList(List<OrderDto> orders) {
        orderList.clear();
        if (orders != null) {
            for (OrderDto order : orders) {
                if (!order.isFromAuction()) {
                    orderList.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }
}
