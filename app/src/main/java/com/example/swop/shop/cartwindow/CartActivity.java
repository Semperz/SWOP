package com.example.swop.shop.cartwindow;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.remote.models.PaymentMethod;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.util.ApiCallback;

import java.util.List;
import java.util.Map;

public class CartActivity extends BaseActivity {

    private CartVM cartVM;
    private CartProductListAdapter adapter;
    private TextView totalTextView;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.cart_product_list);
        totalTextView = findViewById(R.id.text_total_price);
        buyButton = findViewById(R.id.btn_buy);

        adapter = new CartProductListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cartVM = new ViewModelProvider(this).get(CartVM.class);

        // Observa los productos y cantidades del carrito
        cartVM.getCartItemsLiveData().observe(this, cartItems -> {
            adapter.submitList(cartItems);
            buyButton.setEnabled(cartItems != null && !cartItems.isEmpty());
        });

        // Observa el total
        cartVM.getTotalLiveData().observe(this, total -> {
            totalTextView.setText("Total: " + String.format("%.2f", total) + " €");
        });

        // Acciones de aumentar/disminuir cantidad
        adapter.setOnCartItemActionListener(new CartProductCardViewHolder.OnCartItemActionListener() {
            @Override
            public void onIncrease(ProductDto product) {
                cartVM.increaseQuantity(product);
            }

            @Override
            public void onDecrease(ProductDto product) {
                cartVM.decreaseQuantity(product);
            }
        });

        buyButton.setOnClickListener(v -> showPaymentMethodDialog());

        cartVM.loadCart();
    }

    private void showPaymentMethodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment_method, null);
        builder.setView(dialogView);

        Spinner spinner = dialogView.findViewById(R.id.spinner_payment);
        Button btnFinish = dialogView.findViewById(R.id.btn_finish);

        // Configura el spinner con los métodos de pago
        PaymentMethod[] methods = PaymentMethod.values();
        if (methods.length == 0) {
            Toast.makeText(this, "No hay métodos de pago disponibles", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] displayNames = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            displayNames[i] = getPaymentMethodDisplayName(methods[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_payment, displayNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_payment);
        spinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        btnFinish.setOnClickListener(v -> {
            int selectedPosition = spinner.getSelectedItemPosition();
            PaymentMethod selected = methods[selectedPosition];
            cartVM.createOrder(selected, new ApiCallback<>() {
                @Override
                public void onSuccess(OrderDto order) {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, "Compra realizada con éxito", Toast.LENGTH_SHORT).show();
                        cartVM.clearCart();
                        dialog.dismiss();
                    });
                }
                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> Toast.makeText(CartActivity.this, "Error al realizar la compra", Toast.LENGTH_SHORT).show());
                }
            });
        });

        dialog.show();
    }

    private String getPaymentMethodDisplayName(PaymentMethod method) {
        switch (method) {
            case BANK_TRANSFER: return "Transferencia bancaria";
            case PAYPAL: return "PayPal";
            case CREDIT_CARD: return "Tarjeta de crédito";
            default: return method.name();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_cart;
    }
}