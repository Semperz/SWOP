package com.example.swop.bids;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.swop.BaseActivity;
import com.example.swop.R;
import com.example.swop.data.remote.models.BidDto;
import com.example.swop.data.remote.models.ProductDto;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class BidsActivity extends BaseActivity {

    private TextView tvProductName, tvStartingPrice, tvHighestBid, tvTimer;
    private ImageView ivProductImage;
    private EditText etBidAmount;
    private Button btnPlaceBid;

    private BidsVM vm;

    private CountDownTimer auctionTimer;
    private ObjectAnimator colorAnimator;
    private final long totalTimeMillis = 60 * 60 * 1000 + 20; // 24h
    private final long warningTimeMillis = 60 * 60 * 1000; // 1 hora antes de finalizar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bids);

        // Views
        initializeViews();

        // Obtener ViewModel con AndroidViewModelFactory para pasar Application al constructor
        vm = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BidsVM.class);

        vm.getSelectedProduct().observe(this, product -> {
            if (product != null) {
                tvProductName.setText(product.getName().toLowerCase());
                tvStartingPrice.setText("Precio inicial: $" + product.getPrice());
                updateHighestBidDisplay();
                startCountdown();
                Glide.with(BidsActivity.this).
                load(product.getImage()).centerCrop().placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                .into(ivProductImage);
            }
        });

        vm.getHighestBid().observe(this, bidDto -> updateHighestBidDisplay());

        vm.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(BidsActivity.this, message, Toast.LENGTH_SHORT).show();
                vm.clearMessage();
            }
        });

        btnPlaceBid.setOnClickListener(v -> {
            String bidText = etBidAmount.getText().toString();
            if (bidText.isEmpty()) {
                Toast.makeText(BidsActivity.this, "Introduce un importe válido", Toast.LENGTH_SHORT).show();
                return;
            }
            BigDecimal bidAmount;
            try {
                bidAmount = new BigDecimal(bidText);
            } catch (NumberFormatException e) {
                Toast.makeText(BidsActivity.this, "Introduce un importe válido", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductDto product = vm.getSelectedProduct().getValue();
            if (product == null) {
                Toast.makeText(BidsActivity.this, "Producto no cargado", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal minBid = vm.getMinimumBidAmount();
            if (bidAmount.compareTo(minBid) < 0) {
                Toast.makeText(BidsActivity.this, "La puja debe ser al menos $" + minBid.setScale(2, RoundingMode.HALF_UP), Toast.LENGTH_SHORT).show();
                return;
            }

            btnPlaceBid.setEnabled(false);
            vm.placeOrUpdateBid(bidAmount, () -> runOnUiThread(() -> {
                btnPlaceBid.setEnabled(true);
                etBidAmount.setText("");
            }));
        });
    }

    private void initializeViews() {
        tvProductName = findViewById(R.id.tvProductName);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvStartingPrice = findViewById(R.id.tvStartingPrice);
        tvHighestBid = findViewById(R.id.tvHighestBid);
        tvTimer = findViewById(R.id.tvTimer);
        etBidAmount = findViewById(R.id.etBidAmount);
        btnPlaceBid = findViewById(R.id.btnPlaceBid);
    }

    private void updateHighestBidDisplay() {
        BidDto highestBid = vm.getHighestBid().getValue();
        ProductDto product = vm.getSelectedProduct().getValue();

        BigDecimal displayAmount;
        if (highestBid != null && highestBid.getBidAmount() != null) {
            displayAmount = highestBid.getBidAmount();
        } else if (product != null && product.getPrice() != null) {
            displayAmount = product.getPrice().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        } else {
            displayAmount = BigDecimal.ZERO;
        }

        tvHighestBid.setText("Puja más alta: $" + displayAmount.setScale(2, RoundingMode.HALF_DOWN));
    }

    private void startCountdown() {
        auctionTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerDisplay(millisUntilFinished);

                if (millisUntilFinished <= warningTimeMillis && colorAnimator == null) {
                    startWarningAnimation();
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("¡Tiempo finalizado!");
                stopWarningAnimation();
                tvTimer.setTextColor(getResources().getColor(R.color.red, null));
            }
        }.start();
    }

    private void updateTimerDisplay(long millisUntilFinished) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        String time = String.format("Tiempo restante: %02d:%02d:%02d", hours, minutes, seconds);
        tvTimer.setText(time);
    }

    private void startWarningAnimation() {
        int red = getResources().getColor(R.color.red, null);
        int yellow = getResources().getColor(R.color.brand_yellow, null);

        colorAnimator = ObjectAnimator.ofArgb(tvTimer, "textColor", red, yellow);
        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.start();
    }

    private void stopWarningAnimation() {
        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (auctionTimer != null) {
            auctionTimer.cancel();
        }
        stopWarningAnimation();
        super.onDestroy();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bids;
    }
}