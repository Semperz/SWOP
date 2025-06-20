package com.example.swop.shop.mainwindow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.shop.detailwindow.ProductDetailActivity;
import com.example.swop.shop.productslist.ProductListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainWindow extends BaseActivity {

    private MainWindowVM viewModel;
    private ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this).get(MainWindowVM.class);

        setupRecyclerView();
        setupSearchInput();
        observeProducts();
        viewModel.loadRandomProducts();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductListAdapter();
        adapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(MainWindow.this, ProductDetailActivity.class);
            intent.putExtra("product_Id", product.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupSearchInput() {
        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lower = s.toString().toLowerCase();
                if (!lower.equals(s.toString())) {
                    searchInput.removeTextChangedListener(this);
                    searchInput.setText(lower);
                    searchInput.setSelection(lower.length());
                    searchInput.addTextChangedListener(this);
                }
                viewModel.searchProducts(lower);
            }
        });
    }

    private void observeProducts() {
        viewModel.getProductsLiveData().observe(this, this::updateProductList);

    }

    private void updateProductList(List<ProductDto> products) {
        Log.d("MainWindow", "Productos a mostrar: " + products.size());
        adapter.submitList(products);
    }



    @Override protected int getLayoutResId() {return R.layout.activity_main_window;}
}