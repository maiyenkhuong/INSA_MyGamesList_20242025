package com.insa.mygamelist.data

import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Response

interface IGDBApi {
    @Headers(
        "Client-ID: xjdeh4gfqz97ptv99c24k03vldgomw",
        "Authorization: Bearer ly94njrmj0wqsmo9mv4ms9hkc7az1a",
        "Accept: application/json"
    )
    @POST("games")
    suspend fun getGames(@Body requestBody: RequestBody): Response<List<Game>>
    @Headers(
        "Client-ID: xjdeh4gfqz97ptv99c24k03vldgomw",
        "Authorization: Bearer ly94njrmj0wqsmo9mv4ms9hkc7az1a",
        "Accept: application/json"
    )
    @POST("covers")
    suspend fun getCovers(@Body requestBody: RequestBody): Response<List<Cover>>
    @Headers(
        "Client-ID: xjdeh4gfqz97ptv99c24k03vldgomw",
        "Authorization: Bearer ly94njrmj0wqsmo9mv4ms9hkc7az1a",
        "Accept: application/json"
    )
    @POST("genres")
    suspend fun getGenres(@Body requestBody: RequestBody): Response<List<Genre>>
    @Headers(
        "Client-ID: xjdeh4gfqz97ptv99c24k03vldgomw",
        "Authorization: Bearer ly94njrmj0wqsmo9mv4ms9hkc7az1a",
        "Accept: application/json"
    )
    @POST("platforms")
    suspend fun getPlatforms(@Body requestBody: RequestBody): Response<List<Platform>>
    @Headers(
        "Client-ID: xjdeh4gfqz97ptv99c24k03vldgomw",
        "Authorization: Bearer ly94njrmj0wqsmo9mv4ms9hkc7az1a",
        "Accept: application/json"
    )
    @POST("platform_logos")
    suspend fun getPlatformLogos(@Body requestBody: RequestBody): Response<List<Logo>>
}
