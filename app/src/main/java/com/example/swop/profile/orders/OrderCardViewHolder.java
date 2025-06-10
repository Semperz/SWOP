package com.example.swop.profile.orders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.R;
import com.example.swop.data.remote.models.OrderDetailDto;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.repository.OrderRepository;
import com.example.swop.data.util.ApiCallback;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView textOrderDate;
    private final TextView textOrderProducts;
    private final TextView textOrderPaymentMethod;
    private final TextView textOrderTotal;

    public OrderCardViewHolder(View itemView) {
        super(itemView);
        textOrderDate = itemView.findViewById(R.id.text_order_date);
        textOrderProducts = itemView.findViewById(R.id.text_order_products);
        textOrderPaymentMethod = itemView.findViewById(R.id.text_order_payment_method);
        textOrderTotal = itemView.findViewById(R.id.text_order_total);
    }

    public void bind(OrderDto order, OrderRepository orderRepository) {
        textOrderDate.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        textOrderPaymentMethod.setText(order.getPaymentMethod().toString());
        textOrderTotal.setText(order.getAmount() + " €");

        List<OrderDetailDto> details = new ArrayList<>(order.getOrderDetails());
        if (details.isEmpty()) {
            textOrderProducts.setText("Sin productos");
            return;
        }

        List<String> productos = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < details.size(); i++) {
            productos.add(""); // placeholder
            final int index = i;
            OrderDetailDto detail = details.get(i);
            orderRepository.getProductNameByOrderDetailId(detail.getId(), new ApiCallback<>() {
                @Override
                public void onSuccess(List<String> result) {
                    String productName = (result != null && !result.isEmpty()) ? result.get(0) : "Producto";
                    productos.set(index, productName.toLowerCase() + " x" + detail.getQuantity());
                    if (counter.incrementAndGet() == details.size()) {
                        textOrderProducts.setText(String.join("\n", productos));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    productos.set(index, "Producto x" + detail.getQuantity());
                    if (counter.incrementAndGet() == details.size()) {
                        textOrderProducts.setText(String.join("\n", productos));
                    }
                }
            });
        }
    }
}
