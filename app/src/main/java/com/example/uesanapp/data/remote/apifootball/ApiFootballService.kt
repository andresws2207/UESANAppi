package com.example.uesanapp.data.remote.apifootball

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiFootballService {

    @GET("countries")
    suspend fun getCountries(): CountryResponse

    @GET("teams")
    suspend fun getTeamsByCountry(@Query("country") country: String): TeamResponse

}