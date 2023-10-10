package com.example.teravinproject

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.example.teravinproject.databinding.ActivitySplashBinding
import com.example.teravinproject.local.MovieDatabase
import com.example.teravinproject.network.ApiConfig

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val Splash:Long = 3000
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            // Perangkat tidak terhubung ke jaringan
            checkLocalDataAndShowNotification()
        } else {
            // Perangkat terhubung ke jaringan, lanjutkan ke MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
            }, Splash)
        }
    }
    private fun checkLocalDataAndShowNotification() {
        val database = Room.databaseBuilder(applicationContext, MovieDatabase::class.java, "movie_database").build()

        viewModel = MainViewModel(MovieRepository(ApiConfig.getApiService()), database)

        viewModel.getLocalMovies()

        // Mengamati perubahan pada data di ViewModel
        viewModel.filmList.observe(this) { movies ->
            if (movies == null || movies.isEmpty()) {
                // Data lokal kosong, tampilkan notifikasi
                showNoDataNotification()
            } else {
                // Data lokal ada, lanjutkan ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun showNoDataNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("No Data Available")
            .setContentText("Device not connected to network and local data is empty.")
            .setAutoCancel(true) // Notifikasi akan otomatis hilang ketika ditekan

        // Intent yang akan dijalankan ketika notifikasi ditekan
        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(pendingIntent)

        // Tampilkan notifikasi
        notificationManager.notify(1, notificationBuilder.build())
    }

}
