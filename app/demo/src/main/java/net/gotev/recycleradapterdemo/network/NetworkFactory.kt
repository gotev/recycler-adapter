package net.gotev.recycleradapterdemo.network

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class NetworkFactory(
    private val connectTimeoutSeconds: Long = 15,
    private val readTimeoutSeconds: Long = 30,
    private val writeTimeoutSeconds: Long = 20
) {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            .build()
    }

    fun newClientBuilder() = httpClient.newBuilder()

    fun <T> newRetrofit(
        service: Class<T>,
        httpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        baseUrl: String
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .client(httpClient)
            .build()
            .create(service)
    }
}
