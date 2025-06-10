package com.example.swop.shop.detailwindow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.session.SessionManager;
import com.example.swop.login.LoginDialogFragment;
import com.example.swop.shop.cartwindow.CartPreferences;

public class ProductDetailActivity extends BaseActivity {

    private ImageView imageView;
    private TextView nameTextView, priceTextView, descriptionTextView, stockWarningTextView;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Views
        initalizeViews();

        // ViewModel
        ProductDetailVM viewModel = new ViewModelProvider(this).get(ProductDetailVM.class);

        // Recibir ID producto del intent
        Long productId = getIntent().getLongExtra("product_Id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Producto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observar producto
        viewModel.getProductLiveData().observe(this, product -> {
            if (product != null) {
                bindProduct(product);
            } else {
                Toast.makeText(this, "No se pudo cargar el producto", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Cargar producto
        viewModel.loadProductById(productId);

        // Listener botón
        addToCartButton.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);

            if (!sessionManager.isLoggedIn()) {
                // Mostrar login y reintentar al volver
                LoginDialogFragment.showIfNeeded(this, result -> {
                    if (result.success) {
                        addToCart();
                    }
                });
            } else {
                addToCart();
            }
        });
    }

    private void addToCart() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserEmail();
        long productId = getIntent().getLongExtra("product_Id", -1);

        if (CartPreferences.containsProduct(this, userId, productId)) {
            Toast.makeText(this, "El producto ya está en el carrito. Modifica la cantidad desde el carrito.", Toast.LENGTH_LONG).show();
        } else {
            CartPreferences.addProduct(this, userId, productId, 1);
            Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_LONG).show();
        }
    }

    private void initalizeViews() {
        imageView = findViewById(R.id.product_image);
        nameTextView = findViewById(R.id.product_name);
        priceTextView = findViewById(R.id.product_price);
        descriptionTextView = findViewById(R.id.product_description);
        addToCartButton = findViewById(R.id.btn_add_to_cart);
        stockWarningTextView = findViewById(R.id.product_stock_warning);
    }

    private void bindProduct(ProductDto product) {
        nameTextView.setText(product.getName().toLowerCase());
        priceTextView.setText(String.format("$ %.2f", product.getPrice()));
        descriptionTextView.setText(product.getDescriptions().toLowerCase());

        String imageName = product.getImage();
        String imageUrl = RetrofitClient.getBaseUrl()+ "img/" + imageName;
        // Cargar imagen con Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(imageView);

        // Mostrar advertencia de stock
        Integer stock = product.getStock();
        if (stock != null && stock > 0 && stock < 5) {
            stockWarningTextView.setText("¡Solo "+ (stock == 1 ? "queda " : "quedan ") + stock + " unidad" + (stock == 1 ? "" : "es") + "!");
            stockWarningTextView.setVisibility(View.VISIBLE);
        } else if (stock != null && stock <= 0) {
            stockWarningTextView.setText("Producto sin stock");
            stockWarningTextView.setVisibility(View.VISIBLE);
            addToCartButton.setEnabled(false); // Deshabilitar botón si no hay stock
        } else {
            stockWarningTextView.setVisibility(View.GONE);
        }

    }

    @Override protected int getLayoutResId() {return R.layout.activity_product_detail;}

}