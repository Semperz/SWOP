package com.example.swop.data.remote.models;


import androidx.annotation.NonNull;

public enum PaymentMethod {
    CREDIT_CARD, PAYPAL, BANK_TRANSFER, NOT_APPLICABLE;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case CREDIT_CARD:
                return "Tarjeta de Crédito";
            case PAYPAL:
                return "PayPal";
            case BANK_TRANSFER:
                return "Transferencia Bancaria";
            case NOT_APPLICABLE:
                return "No Aplicable";
            default:
                return super.toString();
        }
    }
}

