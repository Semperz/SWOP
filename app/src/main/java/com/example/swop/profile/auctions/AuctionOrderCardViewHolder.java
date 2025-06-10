package com.example.swop.profile.auctions;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.R;
import com.example.swop.data.remote.models.OrderDetailDto;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.repository.OrderRepository;
import com.example.swop.data.util.ApiCallback;
import com.example.swop.shop.PaymentMethodDialogUtil;

import java.util.List;


public class AuctionOrderCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView textProductName;
    private final TextView textAuctionPrice;
    private final Button btnPay;
    private final Runnable onAuctionPaidCallback;

    public AuctionOrderCardViewHolder(View itemView, Runnable onAuctionPaidCallback) {
        super(itemView);
        textProductName = itemView.findViewById(R.id.text_auction_product);
        textAuctionPrice = itemView.findViewById(R.id.text_auction_final_price);
        btnPay = itemView.findViewById(R.id.btn_pay_auction);
        this.onAuctionPaidCallback = onAuctionPaidCallback;
    }

    public void bind(OrderDto order, OrderRepository orderRepository) {
        // Solo hay un OrderDetail por pedido de subasta, se recibe lista por problemas de Gson con Strings
        if (!order.getOrderDetails().isEmpty()) {
            OrderDetailDto detail = order.getOrderDetails().iterator().next();
            orderRepository.getProductNameByOrderDetailId(detail.getId(), new ApiCallback<>() {
                @Override
                public void onSuccess(List<String> result) {
                    String productName = (result != null && !result.isEmpty()) ? result.get(0) : "Producto";
                    textProductName.setText(productName.toLowerCase());
                }

                @Override
                public void onFailure(Throwable t) {
                    textProductName.setText("Producto");
                }
            });
            textAuctionPrice.setText(detail.getPrice() + " €");
        } else {
            textProductName.setText("Producto");
            textAuctionPrice.setText("0 €");
        }

        btnPay.setOnClickListener(v -> {
            PaymentMethodDialogUtil.show(itemView.getContext(), LayoutInflater.from(itemView.getContext()), selected -> {
                OrderDto updatedOrder = new OrderDto();
                updatedOrder.setCustomer(order.getCustomer());
                updatedOrder.setOrderDetails(order.getOrderDetails());
                updatedOrder.setAmount(order.getAmount());
                updatedOrder.setShippingAddress(order.getShippingAddress());
                updatedOrder.setOrderAddress(order.getOrderAddress());
                updatedOrder.setOrderEmail(order.getOrderEmail());
                updatedOrder.setOrderStatus(order.getOrderStatus());
                updatedOrder.setOrderDate(order.getOrderDate());
                updatedOrder.setFromAuction(false);
                updatedOrder.setPaymentMethod(selected);

                orderRepository.update(order.getId(), updatedOrder, new ApiCallback<>() {
                    @Override
                    public void onSuccess(OrderDto result) {
                        Toast.makeText(itemView.getContext(), "Pago registrado correctamente", Toast.LENGTH_SHORT).show();
                        if (onAuctionPaidCallback != null) {
                            onAuctionPaidCallback.run();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(itemView.getContext(), "Error al registrar el pago", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

    }
}



