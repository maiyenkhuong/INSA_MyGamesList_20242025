package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R

/*
object IGDB {

    lateinit var covers: List<Cover>
    // This variable will be initialised later
    lateinit var genres: List<Genre>
    lateinit var logos: List<Logo>
    lateinit var platforms : List<Platform>
    lateinit var games : List<Game>


    fun load(context: Context) {
        val coversFromJson: List<Cover> = Gson().fromJson(
            context.resources.openRawResource(R.raw.covers).bufferedReader(),
            object : TypeToken<List<Cover>>() {}.type
        )

        // Gson().fromJson() : is a principal method of library Gson to convert Json data to instances Kotlin
        // It has 2 param as input : Json data that we have already read with bufferedReader() and the output type
        // For ex : Genres here
        val genresFromJson: List<Genre> = Gson().fromJson(
            context.resources.openRawResource(R.raw.genres).bufferedReader(),
            object : TypeToken<List<Genre>>() {}.type
        )

        Log.d("affichage","genres "+genresFromJson.size.toString())

        val logosFromJson: List<Logo> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platform_logos).bufferedReader(),
            object : TypeToken<List<Logo>>() {}.type
        )
        Log.d("affichage","logo "+logosFromJson.size.toString())

        val platformsFromJson: List<Platform> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platforms).bufferedReader(),
            object : TypeToken<List<Platform>>() {}.type
        )
        Log.d("affichage","platforms "+platformsFromJson.size.toString())

        val gamesFromJson: List<Game> = Gson().fromJson(
            context.resources.openRawResource(R.raw.games).bufferedReader(),
            object : TypeToken<List<Game>>() {}.type
        )
        Log.d("affichage", "games "+gamesFromJson.size.toString())

        covers = coversFromJson
        genres = genresFromJson
        logos = logosFromJson
        platforms = platformsFromJson
        games = gamesFromJson
    }

}
*/
