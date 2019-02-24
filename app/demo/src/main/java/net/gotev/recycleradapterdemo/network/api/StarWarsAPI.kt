package net.gotev.recycleradapterdemo.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface StarWarsAPI {
    @GET("starships")
    fun getStarships(): Single<SWAPIPaginatedResponse<SWAPIStarship>>

    @GET
    fun getStarhipsFromUrl(@Url url: String): Single<SWAPIPaginatedResponse<SWAPIStarship>>
}
