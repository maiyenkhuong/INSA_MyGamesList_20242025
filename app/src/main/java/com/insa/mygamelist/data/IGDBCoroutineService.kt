package com.insa.mygamelist.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

object IGDBCoroutineService {
    val igdbApi: IGDBApi = IGDBInstance.getInstance().create(IGDBApi::class.java)
    suspend fun fetchGames(limit: Int = 10): List<Game>? {
        try {
            val query = "fields name, cover, first_release_date, genres, platforms, summary, rating; limit $limit;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())

            val response: Response<List<Game>> = igdbApi.getGames(requestBody)

            val games = response.body()
            if (!games.isNullOrEmpty()) {
                Log.d("games", "nb games: ${games.size}")
            } else {
                Log.d("games", "No games found")
            }
            return games
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun fetchCovers(coverId: Long): List<Cover>? {
        try {
            val query = "fields url; where id = $coverId;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response: Response<List<Cover>>  = igdbApi.getCovers(requestBody)
            if (response.isSuccessful) {
                val covers = response.body()
                return covers
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun fetchGenres(listGenreId: List<Long>): List<Genre>? {
        try {
            val listGenre = "(${listGenreId.joinToString(", ")})"
            val query = "fields name; where id = $listGenre;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = igdbApi.getGenres(requestBody)
            if(response.isSuccessful){
                val genres = response.body()
                return genres
            } else {
                return null
            }
        } catch (e : Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun fetchPlatforms(listPlatformId: List<Long>): List<Platform>? {
        try {
            val listPlatform = "(${listPlatformId.joinToString(", ")})"
            val query = "fields name, platform_logo; where id = $listPlatform;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = igdbApi.getPlatforms(requestBody)
            if(response.isSuccessful){
                val platforms = response.body()
                return platforms
            } else {
                return null
            }
        } catch (e : Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun fetchLogos(logoId: Long): List<Logo>? {
        try {
            val query = "fields url; where id = $logoId;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = igdbApi.getPlatformLogos(requestBody)
            if(response.isSuccessful){
                val logos = response.body()
                return logos
            } else {
                return null
            }
        } catch (e : Exception) {
            e.printStackTrace()
            return null
        }
    }

}


