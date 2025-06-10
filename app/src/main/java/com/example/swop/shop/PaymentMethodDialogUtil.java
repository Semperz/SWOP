package com.example.swop.shop;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swop.R;
import com.example.swop.data.remote.models.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDialogUtil {

    public interface OnPaymentMethodSelectedListener {
        void onPaymentMethodSelected(PaymentMethod method);
    }

    public static void show(Context context, LayoutInflater inflater, OnPaymentMethodSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.dialog_payment_method, null);
        builder.setView(dialogView);

        Spinner spinner = dialogView.findViewById(R.id.spinner_payment);
        Button btnFinish = dialogView.findViewById(R.id.btn_finish);

        List<PaymentMethod> validMethods = new ArrayList<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method != PaymentMethod.NOT_APPLICABLE) {
                validMethods.add(method);
            }
        }
        if (validMethods.isEmpty()) {
            Toast.makeText(context, "No hay métodos de pago disponibles", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] displayNames = new String[validMethods.size()];
        for (int i = 0; i < validMethods.size(); i++) {
            displayNames[i] = getPaymentMethodDisplayName(validMethods.get(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item_payment, displayNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_payment);
        spinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        btnFinish.setOnClickListener(v -> {
            int selectedPosition = spinner.getSelectedItemPosition();
            PaymentMethod selected = validMethods.get(selectedPosition);
            listener.onPaymentMethodSelected(selected);
            dialog.dismiss();
        });

        dialog.show();
    }

    private static String getPaymentMethodDisplayName(PaymentMethod method) {
        switch (method) {
            case BANK_TRANSFER: return "Transferencia bancaria";
            case PAYPAL: return "PayPal";
            case CREDIT_CARD: return "Tarjeta de crédito";
            default: return method.name();
        }
    }
}
