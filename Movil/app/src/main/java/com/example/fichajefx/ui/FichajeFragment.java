package com.example.fichajefx.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fichajefx.R;
import com.example.fichajefx.models.ClockInRequest;
import com.example.fichajefx.models.ClockInResponse;
import com.example.fichajefx.models.WorkTimeResponse;
import com.example.fichajefx.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichajeFragment extends Fragment {

    private static final String ESTADO_TRABAJANDO = "TRABAJANDO";
    private static final String ESTADO_DESCANSANDO = "DESCANSANDO";

    private TextView tvUbicacion, tvWorkTime, tvStatus, tvStatusHint;
    private View statusDot;
    private LinearLayout statusChip;
    private ProgressBar progressBar;
    private MaterialButton btnEntrada, btnSalida;
    private com.example.fichajefx.utils.SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fichaje, container, false);

        session = new com.example.fichajefx.utils.SessionManager(getContext());
        btnEntrada = view.findViewById(R.id.btnEntrada);
        btnSalida = view.findViewById(R.id.btnSalida);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        tvWorkTime = view.findViewById(R.id.tvWorkTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvStatusHint = view.findViewById(R.id.tvStatusHint);
        statusDot = view.findViewById(R.id.statusDot);
        statusChip = view.findViewById(R.id.statusChip);
        progressBar = view.findViewById(R.id.progressBar);

        btnEntrada.setOnClickListener(v -> registrarFichaje("ENTRADA"));
        btnSalida.setOnClickListener(v -> registrarFichaje("SALIDA"));

        tvUbicacion.setText("Ubicación: Oficina Central (SIM)");

        loadJornadaState();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJornadaState();
    }

    // Carga tiempo trabajado y estado actual desde la API
    private void loadJornadaState() {
        RetrofitClient.getApiService().getWorkTimeToday(session.getUserId()).enqueue(new Callback<WorkTimeResponse>() {
            @Override
            public void onResponse(Call<WorkTimeResponse> call, Response<WorkTimeResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    WorkTimeResponse data = response.body();
                    tvWorkTime.setText(data.getFormatted());
                    applyEstado(data.getEstado());
                }
            }

            @Override
            public void onFailure(Call<WorkTimeResponse> call, Throwable t) {
                if (isAdded()) {
                    tvStatus.setText(R.string.status_loading);
                }
            }
        });
    }

    // Actualiza chip, texto de ayuda y habilitacion de botones segun el estado
    private void applyEstado(String estado) {
        if (estado == null) estado = "PENDIENTE";

        switch (estado) {
            case ESTADO_TRABAJANDO:
                statusChip.setBackgroundResource(R.drawable.bg_status_chip_working);
                statusDot.setBackgroundResource(R.drawable.bg_status_dot_working);
                tvStatus.setText(R.string.status_working);
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_success));
                tvStatusHint.setText(R.string.status_hint_working);
                btnEntrada.setEnabled(false);
                btnEntrada.setAlpha(0.45f);
                btnSalida.setEnabled(true);
                btnSalida.setAlpha(1f);
                break;
            case ESTADO_DESCANSANDO:
                statusChip.setBackgroundResource(R.drawable.bg_status_chip_rest);
                statusDot.setBackgroundResource(R.drawable.bg_status_dot_rest);
                tvStatus.setText(R.string.status_resting);
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                tvStatusHint.setText(R.string.status_hint_resting);
                btnEntrada.setEnabled(true);
                btnEntrada.setAlpha(1f);
                btnSalida.setEnabled(false);
                btnSalida.setAlpha(0.45f);
                break;
            default:
                statusChip.setBackgroundResource(R.drawable.bg_status_chip_pending);
                statusDot.setBackgroundResource(R.drawable.bg_status_dot_pending);
                tvStatus.setText(R.string.status_pending);
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_light));
                tvStatusHint.setText(R.string.status_hint_pending);
                btnEntrada.setEnabled(true);
                btnEntrada.setAlpha(1f);
                btnSalida.setEnabled(false);
                btnSalida.setAlpha(0.45f);
                break;
        }
    }

    private void registrarFichaje(String tipo) {
        progressBar.setVisibility(View.VISIBLE);
        btnEntrada.setEnabled(false);
        btnSalida.setEnabled(false);

        int empleadoId = session.getUserId();
        int clienteId = session.getClienteId();
        ClockInRequest request = new ClockInRequest(empleadoId, clienteId, tipo, "Oficina Central (Android)");

        RetrofitClient.getApiService().registrarFichaje(request).enqueue(new Callback<ClockInResponse>() {
            @Override
            public void onResponse(Call<ClockInResponse> call, Response<ClockInResponse> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    loadJornadaState();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    loadJornadaState();
                }
            }

            @Override
            public void onFailure(Call<ClockInResponse> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadJornadaState();
            }
        });
    }
}
