package com.example.teravinproject

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.example.teravinproject.local.MovieDatabase
import com.example.teravinproject.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class MovieUpdateService : Service() {
    private lateinit var viewModel: MainViewModel

    private val updateIntervalMillis = 60_000 // Setiap 1 menit


    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi ViewModel di sini
        val database = Room.databaseBuilder(applicationContext, MovieDatabase::class.java, "movie_database").build()
        viewModel = MainViewModel(MovieRepository(ApiConfig.getApiService()), database)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MovieUpdateService", "Service started")
        startUpdateTask()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdateTask()
    }

    private fun startUpdateTask() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Lakukan pembaruan data ke API di sini
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.fetchFilms(this@MovieUpdateService)
                }
            }
        }, 0, updateIntervalMillis.toLong())
    }

    private fun stopUpdateTask() {
        timer?.cancel()
        timer = null
    }
}