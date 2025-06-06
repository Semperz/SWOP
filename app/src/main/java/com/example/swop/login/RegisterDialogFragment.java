package com.example.swop.login;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.swop.R;

import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.repository.CustomerRepository;
import com.example.swop.data.util.ApiCallback;


public class RegisterDialogFragment extends DialogFragment {

    private EditText inputEmail, inputPassword, inputFullName, inputBilling, inputShipping, inputCountry, inputPhone;
    private Button btnRegister;
    private TextView errorText;
    private CustomerRepository customerRepo;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        customerRepo = new CustomerRepository(requireContext());

        initializeViews(dialog);

        btnRegister.setOnClickListener(v -> register());

        return dialog;
    }

    private void initializeViews(Dialog dialog) {
        inputEmail    = dialog.findViewById(R.id.input_email);
        inputPassword = dialog.findViewById(R.id.input_password);
        inputFullName = dialog.findViewById(R.id.input_full_name);
        inputBilling  = dialog.findViewById(R.id.input_billing);
        inputShipping = dialog.findViewById(R.id.input_shipping);
        inputCountry  = dialog.findViewById(R.id.input_country);
        inputPhone    = dialog.findViewById(R.id.input_phone);
        btnRegister   = dialog.findViewById(R.id.btn_register);
        errorText     = dialog.findViewById(R.id.register_error);
    }

    private void register() {
        String email     = inputEmail.getText().toString().trim();
        String password  = inputPassword.getText().toString().trim();
        String fullName  = inputFullName.getText().toString().trim();
        String billing   = inputBilling.getText().toString().trim();
        String shipping  = inputShipping.getText().toString().trim();
        String country   = inputCountry.getText().toString().trim();
        String phone     = inputPhone.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
                billing.isEmpty() || shipping.isEmpty() || country.isEmpty() || phone.isEmpty()) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("Por favor completa todos los campos.");
            return;
        }

        CustomerDto dto = new CustomerDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setFullName(fullName);
        dto.setBillingAddress(billing);
        dto.setDefaultShippingAddress(shipping);
        dto.setCountry(country);
        dto.setPhone(phone);

        customerRepo.create(dto, new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto result) {
                dismiss();
                new LoginDialogFragment().show(getParentFragmentManager(), "LoginDialog");
            }

            @Override
            public void onFailure(Throwable t) {
                errorText.setVisibility(View.VISIBLE);
                if (t.getMessage() != null && t.getMessage().contains("HTTP 409")) {
                    errorText.setText("Este correo ya está en uso.");
                } else {
                    errorText.setText("Error al registrar. Intenta de nuevo.");
                }
            }
        });
    }
}
