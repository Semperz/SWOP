package com.example.swop.shop.categorywindow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.shop.detailwindow.ProductDetailActivity;
import com.example.swop.shop.productslist.ProductListAdapter;

public class CategoryActivity extends BaseActivity {
    private ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tvCategoryTitle = findViewById(R.id.category_title);
        RecyclerView recyclerView = findViewById(R.id.category_product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductListAdapter();
        recyclerView.setAdapter(adapter);

        // Set click listener si usas navegación a detalles
        adapter.setOnItemClickListener(product -> {
            Intent i = new Intent(this, ProductDetailActivity.class);
            i.putExtra("product_Id", product.getId());
            startActivity(i);
        });

        String categoryName = getIntent().getStringExtra("category");
        if (categoryName == null) {
            Toast.makeText(this, "Categoría no especificada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCategoryTitle.setText(categoryName.toLowerCase());

        CategoryVM viewModel = new ViewModelProvider(this).get(CategoryVM.class);
        viewModel.getProductsLiveData().observe(this, products -> {
            adapter.submitList(products);
        });

        viewModel.loadProductsByCategoryName(categoryName);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_category;
    }
}