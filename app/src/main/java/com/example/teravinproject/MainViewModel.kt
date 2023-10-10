package com.example.teravinproject

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.teravinproject.local.MovieDatabase
import com.example.teravinproject.local.MovieEntity
import com.example.teravinproject.model.Movie
import com.example.teravinproject.model.MovieResponse
import com.example.teravinproject.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainViewModel(private val movieRepository: MovieRepository, private val database: MovieDatabase) : ViewModel() {
    private val _filmList = MutableLiveData<List<Movie>?>()

    val filmList: MutableLiveData<List<Movie>?>
        get() = _filmList

    fun MovieEntity.toMovie(): Movie {
        return Movie(this.id, this.title, this.tanggal)
    }
    init {
        // Memeriksa ketersediaan data lokal saat ViewModel diinisialisasi
        viewModelScope.launch {
            val localMovieList = withContext(Dispatchers.IO) {
                database.movieDao().getMovies()
            }
            if (localMovieList.isEmpty()) {
                // Panggil API untuk mendapatkan data baru jika data lokal kosong
                val context: Context? = null
                context?.let { fetchFilms(it) }
            } else {
                // Tampilkan data lokal di UI jika data lokal tersedia
                _filmList.postValue(localMovieList.map { it.toMovie() })
            }
        }
    }

    fun getLocalMovies() {
        viewModelScope.launch {
            val localMovieList = withContext(Dispatchers.IO) {
                database.movieDao().getMovies()
            }
            _filmList.postValue(localMovieList.map { it.toMovie() })
        }
    }



    fun fetchFilms(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = movieRepository.getFilms().execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()?.results
                    if (responseBody != null) {
                        // Menyimpan data dari API ke database lokal
                        val movieEntities = responseBody.map {
                            MovieEntity(it.id, it.title, it.tanggal)
                        }
                        // Jalankan operasi database di latar belakang
                        withContext(Dispatchers.IO) {
                            database.movieDao().insertMovie(movieEntities)
                        }
                        val intent = Intent("ACTION_UPDATE_COMPLETED")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                        // Menampilkan data dari API di UI
                        _filmList.postValue(responseBody)
                    }
                }
            } catch (e: IOException) {
                // Handle error ketika gagal mendapatkan data dari API
            }
        }
    }

}
