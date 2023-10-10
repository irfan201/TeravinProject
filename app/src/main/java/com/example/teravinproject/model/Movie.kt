package com.example.teravinproject.model

import com.google.gson.annotations.SerializedName

data class Movie (

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("original_title")
    val title: String,

    @field:SerializedName("release_date")
    val tanggal: String,
)

data class MovieResponse(

    @field:SerializedName("results")
    val results: List<Movie>
)


