package com.example.fichajefx.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fichajefx.LoginActivity;
import com.example.fichajefx.R;
import com.example.fichajefx.models.HistoryRecord;
import com.example.fichajefx.network.RetrofitClient;
import com.example.fichajefx.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView tvName, tvEmail, tvRol, tvEmpty;
    private LinearLayout containerHistory;
    private ProgressBar pbHistory;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);

        session = new SessionManager(requireContext());

        tvName = root.findViewById(R.id.tvProfileName);
        tvEmail = root.findViewById(R.id.tvProfileEmail);
        tvRol = root.findViewById(R.id.tvProfileRol);
        tvEmpty = root.findViewById(R.id.tvEmptyHistory);
        containerHistory = root.findViewById(R.id.containerHistory);
        pbHistory = root.findViewById(R.id.pbHistory);

        // Set Info
        tvName.setText(session.getUserName());
        tvEmail.setText(session.getUserEmail());
        tvRol.setText(session.getUserRol());

        // Buttons
        root.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        root.findViewById(R.id.btnSwitchAccount).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        loadHistory();

        return root;
    }

    private void loadHistory() {
        pbHistory.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        containerHistory.removeAllViews();

        RetrofitClient.getApiService().getHistorial(session.getUserId()).enqueue(new Callback<List<HistoryRecord>>() {
            @Override
            public void onResponse(Call<List<HistoryRecord>> call, Response<List<HistoryRecord>> response) {
                if (isAdded()) {
                    pbHistory.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        for (HistoryRecord record : response.body()) {
                            addHistoryItem(record);
                        }
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HistoryRecord>> call, Throwable t) {
                if (isAdded()) {
                    pbHistory.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Error al cargar historial");
                }
            }
        });
    }

    private void addHistoryItem(HistoryRecord record) {
        View view = getLayoutInflater().inflate(R.layout.item_history, containerHistory, false);
        TextView tvType = view.findViewById(R.id.tvHistoryType);
        TextView tvTime = view.findViewById(R.id.tvHistoryTime);
        TextView tvLocation = view.findViewById(R.id.tvHistoryLocation);

        String typeStr = record.getTipo();
        boolean esEntrada = typeStr != null && typeStr.equalsIgnoreCase("ENTRADA");
        tvType.setText(esEntrada ? "Entrada" : "Salida");
        tvType.setTextColor(ContextCompat.getColor(requireContext(),
                esEntrada ? R.color.accent_success : R.color.accent_danger));

        tvTime.setText(record.getTimestamp());
        tvLocation.setText(record.getUbicacion() != null ? record.getUbicacion() : "Sin ubicación");

        containerHistory.addView(view);
    }
}
