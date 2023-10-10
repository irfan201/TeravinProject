package com.example.teravinproject.network

import com.example.teravinproject.model.Movie
import com.example.teravinproject.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("discover/movie?api_key=f7b67d9afdb3c971d4419fa4cb667fbf")
     fun getFilms(): Call<MovieResponse>
}