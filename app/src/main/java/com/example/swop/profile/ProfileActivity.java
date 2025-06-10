package com.example.swop.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.WelcomeActivity;
import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.repository.OrderRepository;
import com.example.swop.data.util.ApiCallback;
import com.example.swop.profile.auctions.AuctionOrderListAdapter;
import com.example.swop.profile.orders.OrderListAdapter;

import java.util.List;

public class ProfileActivity extends BaseActivity {

    private ProfileVM profileVM;
    private TextView textName, textEmail, textBilling, textShipping, textCountry, textPhone;
    private ImageButton btnEdit, btnLogout;
    private RecyclerView recyclerOrders, recyclerAuctions;
    private OrderListAdapter orderListAdapter;
    private AuctionOrderListAdapter auctionOrderListAdapter;
    private OrderRepository orderRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileVM = new ViewModelProvider(this).get(ProfileVM.class);
        orderRepository = new OrderRepository(this);

        initializeViews();

        orderListAdapter = new OrderListAdapter(orderRepository);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerOrders.setAdapter(orderListAdapter);

        auctionOrderListAdapter = new AuctionOrderListAdapter(orderRepository, this::loadOrders);
        recyclerAuctions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerAuctions.setAdapter(auctionOrderListAdapter);

        profileVM.getCustomerLiveData().observe(this, customer -> {
            if (customer != null) {
                updateProfileView(customer);
                loadOrders();
            }
        });

        btnEdit.setOnClickListener(v -> {
            CustomerDto customer = profileVM.getCustomerLiveData().getValue();
            if (customer != null) {
                new EditProfileDialogFragment(customer).show(getSupportFragmentManager(), "edit_profile");
            }
        });

        btnLogout.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Quieres cerrar la sesión?")
                    .setPositiveButton("Sí", (dialogInterface, which) -> {
                        profileVM.logout();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.BLACK);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK);
        });
    }

    private void initializeViews() {
        textName = findViewById(R.id.text_user_name);
        textEmail = findViewById(R.id.text_user_email);
        textBilling = findViewById(R.id.text_user_billing);
        textShipping = findViewById(R.id.text_user_shipping);
        textCountry = findViewById(R.id.text_user_country);
        textPhone = findViewById(R.id.text_user_phone);
        btnEdit = findViewById(R.id.btn_edit_profile);
        btnLogout = findViewById(R.id.btn_logout);
        recyclerOrders = findViewById(R.id.recycler_orders);
        recyclerAuctions = findViewById(R.id.recycler_auctions);
    }

    private void updateProfileView(CustomerDto customer) {
        textName.setText(customer.getFullName());
        textEmail.setText(customer.getEmail());
        textBilling.setText(customer.getBillingAddress());
        textShipping.setText(customer.getDefaultShippingAddress());
        textCountry.setText(customer.getCountry());
        textPhone.setText(customer.getPhone());
    }

    private void loadOrders() {
        orderRepository.myOrders(new ApiCallback<>() {
            @Override
            public void onSuccess(List<OrderDto> result) {
                orderListAdapter.submitList(result);
                auctionOrderListAdapter.submitList(result);
            }

            @Override
            public void onFailure(Throwable t) {
                orderListAdapter.submitList(null);
                auctionOrderListAdapter.submitList(null);
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }

}