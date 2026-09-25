package com.example.fichajefx;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fichajefx.models.AuthResponse;
import com.example.fichajefx.models.RegisterRequest;
import com.example.fichajefx.network.RetrofitClient;
import com.example.fichajefx.utils.NifValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etNif, etPassword;
    private CheckBox cbPolicy;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etNif = findViewById(R.id.etNif);
        etPassword = findViewById(R.id.etPassword);
        cbPolicy = findViewById(R.id.cbPolicy);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String nif = etNif.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || nif.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NifValidator.isValidSpanishNIF(nif)) {
            Toast.makeText(this, "NIF/DNI no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbPolicy.isChecked()) {
            Toast.makeText(this, "Debes aceptar la política de privacidad", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        RegisterRequest request = new RegisterRequest(name, email, password, nif, "EMPLEADO", true);

        RetrofitClient.getApiService().register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "✅ Registro correcto. ¡Inicia sesión!", Toast.LENGTH_LONG)
                            .show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "❌ Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "❌ Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
