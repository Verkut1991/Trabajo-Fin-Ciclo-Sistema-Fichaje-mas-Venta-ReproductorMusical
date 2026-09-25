package com.mycompany.fichajefx.network;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Cliente HTTP centralizado configurado con HTTP/1.1 para compatibilidad.
 */
public class NetworkClient {
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpClient getClient() {
        return client;
    }
}
