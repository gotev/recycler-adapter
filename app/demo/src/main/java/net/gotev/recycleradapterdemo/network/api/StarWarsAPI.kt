package net.gotev.recycleradapterdemo.network.api

import retrofit2.http.GET
import retrofit2.http.Url

interface StarWarsAPI {
    @GET("people/page1.json")
    suspend fun getPeople(): SWAPIPaginatedResponse<SWAPIPerson>

    @GET
    suspend fun getPeopleFromUrl(@Url url: String): SWAPIPaginatedResponse<SWAPIPerson>
}
