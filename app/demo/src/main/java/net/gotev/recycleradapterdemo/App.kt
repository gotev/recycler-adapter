package net.gotev.recycleradapterdemo

import android.app.Application
import net.gotev.recycleradapterdemo.network.NetworkFactory
import net.gotev.recycleradapterdemo.network.api.StarWarsAPI
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        lateinit var starWarsClient: StarWarsAPI
    }

    override fun onCreate() {
        super.onCreate()

        val network = NetworkFactory()
        starWarsClient = network.newRetrofit(
                service = StarWarsAPI::class.java,
                httpClient = network.newClientBuilder().build(),
                converterFactory = GsonConverterFactory.create(),
                baseUrl = "https://swapi.co/api/"
        )
    }
}
