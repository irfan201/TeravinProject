package com.example.teravinproject

import com.example.teravinproject.model.Movie
import com.example.teravinproject.model.MovieResponse
import com.example.teravinproject.network.ApiService
import retrofit2.Call

class MovieRepository(private val apiService: ApiService) {
     fun getFilms(): Call<MovieResponse> {
        return apiService.getFilms()
    }
}