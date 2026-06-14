package com.example.uesanapp.data.remote.apifootball

data class Country (
    val name: String,
    val code: String,
    val flag: String
)

data class CountryResponse (
    val response: List<Country>
)

data class Team(
    val id: Int,
    val name: String,
    val logo: String,
    val code: String,
    val country: String,
    val founded: Int,
    val national: Boolean
)

data class TeamWrapper(
    val team: Team
)

data class TeamResponse(
    val response: List<TeamWrapper>
)
