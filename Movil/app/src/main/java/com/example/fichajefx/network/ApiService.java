package com.example.fichajefx.network;

import com.example.fichajefx.models.AuthResponse;
import com.example.fichajefx.models.ClockInRequest;
import com.example.fichajefx.models.ClockInResponse;
import com.example.fichajefx.models.LoginRequest;
import com.example.fichajefx.models.RegisterRequest;
import com.example.fichajefx.models.HistoryRecord;
import com.example.fichajefx.models.WorkTimeResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/fichajes/fichar")
    Call<ClockInResponse> registrarFichaje(@Body ClockInRequest request);

    @POST("api/empleados/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/empleados/registro")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @retrofit2.http.GET("api/fichajes/historial/{empleado_id}")
    Call<List<HistoryRecord>> getHistorial(@retrofit2.http.Path("empleado_id") int empleadoId);

    @retrofit2.http.GET("api/fichajes/tiempo-hoy/{empleado_id}")
    Call<WorkTimeResponse> getWorkTimeToday(@retrofit2.http.Path("empleado_id") int empleadoId);
}
