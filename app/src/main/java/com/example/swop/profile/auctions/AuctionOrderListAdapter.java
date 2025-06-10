package com.example.swop.profile.auctions;

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

public class AuctionOrderListAdapter extends RecyclerView.Adapter<AuctionOrderCardViewHolder> {

    private final List<OrderDto> auctionOrderList = new ArrayList<>();
    private final OrderRepository orderRepository;
    private final Runnable onAuctionPaidCallback;


    public AuctionOrderListAdapter(OrderRepository orderRepository, Runnable onAuctionPaidCallback) {
        this.orderRepository = orderRepository;
        this.onAuctionPaidCallback = onAuctionPaidCallback;
    }

    @NonNull
    @Override
    public AuctionOrderCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_auction_card_view_holder, parent, false);
        return new AuctionOrderCardViewHolder(view, onAuctionPaidCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionOrderCardViewHolder holder, int position) {
        holder.bind(auctionOrderList.get(position), orderRepository);
    }

    @Override
    public int getItemCount() {
        return auctionOrderList.size();
    }

    public void submitList(List<OrderDto> orders) {
        auctionOrderList.clear();
        if (orders != null) {
            for (OrderDto order : orders) {
                if (order.isFromAuction()) {
                    auctionOrderList.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }
}