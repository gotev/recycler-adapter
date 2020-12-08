package net.gotev.recycleradapterdemo.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface StarWarsAPI {
    @GET("people/page1.json")
    fun getPeople(): Single<SWAPIPaginatedResponse<SWAPIPerson>>

    @GET
    fun getPeopleFromUrl(@Url url: String): Single<SWAPIPaginatedResponse<SWAPIPerson>>
}
