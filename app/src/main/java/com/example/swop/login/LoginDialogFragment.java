package com.example.swop.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.swop.R;
import com.example.swop.data.remote.models.AuthRequest;
import com.example.swop.data.remote.models.LoginResponse;
import com.example.swop.data.session.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDialogFragment extends DialogFragment {

    private LoginCallback callback;

    public static void showIfNeeded(Context context, LoginCallback callback) {
        if (!(context instanceof androidx.fragment.app.FragmentActivity)) return;
        LoginDialogFragment dialog = new LoginDialogFragment();
        dialog.setCallback(callback);
        dialog.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "LoginDialog");
    }

    public void setCallback(LoginCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, null);

        EditText inputEmail = view.findViewById(R.id.input_email);
        EditText inputPassword = view.findViewById(R.id.input_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView linkRegister = view.findViewById(R.id.link_register);

        SessionManager sessionManager = new SessionManager(requireContext());

        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthRequest authRequest = new AuthRequest(email, password);
            sessionManager.getAuthApi().login(authRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        sessionManager.saveCredentials(email, password, response.body().getToken());
                        Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                        dismiss();
                        if (callback != null) callback.onLoginResult(LoginResult.success());
                    } else {
                        Toast.makeText(getContext(), "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onLoginResult(LoginResult.failure("Credenciales inválidas"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                    Log.e("LoginDialog", "Error al iniciar sesión", t);
                    if (callback != null) callback.onLoginResult(LoginResult.failure("Error de red"));
                }
            });
        });

        linkRegister.setOnClickListener(v -> {
            dismiss();
            new RegisterDialogFragment().show(getParentFragmentManager(), "RegisterDialog");
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();
    }
}
