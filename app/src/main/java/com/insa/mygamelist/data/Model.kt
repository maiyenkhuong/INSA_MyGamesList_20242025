package com.insa.mygamelist.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cover(
    @SerialName("id") val id: Long,
    @SerialName("url") val url: String
)
@Serializable
data class Genre(
    val id: Long,
    @SerialName("name") val name: String
)
@Serializable
data class Logo(
    val id: Long,
    val url: String?=null
)
@Serializable
data class Platform(
    val id: Long,
    val name: String,
    val platform_logo: Long?=null
)

@Serializable
data class Game(
    @SerialName("id") val id: Long,
    @SerialName("cover") val cover: Long?=null,
    @SerialName("first_release_date") val first_release_date: Long?=null,
    @SerialName("genres") val genres: List<Long> = emptyList(),
    @SerialName("name") val name: String,
    @SerialName("platforms") val platforms: List<Long> = emptyList(),
    @SerialName("summary") val summary: String?=null,
    @SerialName("rating") val total_rating: Double?=null
)