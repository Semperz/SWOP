package com.example.swop.profile;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.swop.R;
import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.util.ApiCallback;

import java.util.Objects;

public class EditProfileDialogFragment extends DialogFragment {

    private EditText inputEmail, inputPassword, inputFullName, inputBilling, inputShipping, inputCountry, inputPhone;
    private Button btnSave;
    private TextView errorText;
    private final CustomerDto customer;
    private ProfileVM profileVM;

    public EditProfileDialogFragment(CustomerDto customer) {
        this.customer = customer;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        profileVM = new ViewModelProvider(requireActivity()).get(ProfileVM.class);

        initializeViews(dialog);
        fillFields();

        btnSave.setText("Guardar cambios");
        btnSave.setOnClickListener(v -> save());

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
        btnSave       = dialog.findViewById(R.id.btn_register);
        errorText     = dialog.findViewById(R.id.register_error);
    }

    private void fillFields() {
        if (customer == null) return;
        inputEmail.setText(customer.getEmail());
        inputPassword.setText(""); // No mostrar la contraseña
        inputFullName.setText(customer.getFullName());
        inputBilling.setText(customer.getBillingAddress());
        inputShipping.setText(customer.getDefaultShippingAddress());
        inputCountry.setText(customer.getCountry());
        inputPhone.setText(customer.getPhone());
    }

    private void save() {
        String email     = inputEmail.getText().toString().trim();
        String password  = inputPassword.getText().toString().trim();
        String fullName  = inputFullName.getText().toString().trim();
        String billing   = inputBilling.getText().toString().trim();
        String shipping  = inputShipping.getText().toString().trim();
        String country   = inputCountry.getText().toString().trim();
        String phone     = inputPhone.getText().toString().trim();

        if (email.isEmpty() || fullName.isEmpty() ||
                billing.isEmpty() || shipping.isEmpty() || country.isEmpty() || phone.isEmpty()) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("Por favor completa todos los campos.");
            return;
        }

        CustomerDto updated = new CustomerDto();
        updated.setId(customer.getId());
        updated.setEmail(email);
        updated.setFullName(fullName);
        updated.setBillingAddress(billing);
        updated.setDefaultShippingAddress(shipping);
        updated.setCountry(country);
        updated.setPhone(phone);
        // Solo actualiza la contraseña si se ha escrito algo
        if (!password.isEmpty()) {
            updated.setPassword(password);
        } else {
            updated.setPassword(customer.getPassword());
        }

        profileVM.updateCustomer(updated, new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto result) {
                dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Error al actualizar. Intenta de nuevo.");
            }
        });
    }
}
