package com.example.teravinproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.teravinproject.adapter.MovieAdapter
import com.example.teravinproject.databinding.ActivityMainBinding
import com.example.teravinproject.local.MovieDatabase
import com.example.teravinproject.model.Movie
import com.example.teravinproject.network.ApiConfig

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MovieAdapter
    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<Movie>()
    object Constants {
        const val ACTION_UPDATE_COMPLETED = "com.example.teravinproject.UPDATE_COMPLETED"
    }

    private val updateCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Constants.ACTION_UPDATE_COMPLETED) {
                // Tampilkan notifikasi bahwa pembaruan telah selesai
                Toast.makeText(context, "Update Completed", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Update Completed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MovieAdapter(list)



        val database = Room.databaseBuilder(applicationContext, MovieDatabase::class.java, "movie_database").build()
        viewModel = MainViewModel(MovieRepository(ApiConfig.getApiService()), database)
        binding.rvMovie.layoutManager = LinearLayoutManager(this)
        binding.rvMovie.adapter = adapter
        binding.rvMovie.setHasFixedSize(true)



        viewModel.getLocalMovies()

        // Mengamati perubahan pada data di ViewModel
        viewModel.filmList.observe(this) { movies ->
            if (movies != null) {
                Log.d("MainActivity", "Data dari ViewModel: $movies")
                adapter.setData(movies)
                showNotification()
            }
        }

        // Memperbarui data dari API jika diperlukan
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchFilms(this)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        val serviceIntent = Intent(this, MovieUpdateService::class.java)
        startService(serviceIntent)

        val intentFilter = IntentFilter("ACTION_UPDATE_COMPLETED")
        val receiver = MyReceiver()

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        // Ini bisa dilakukan dalam onCreate Activity atau dalam onCreate aplikasi Anda
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(Constants.ACTION_UPDATE_COMPLETED)
        LocalBroadcastManager.getInstance(this).registerReceiver(updateCompletedReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateCompletedReceiver)
    }

//    private fun sendUpdateCompletedBroadcast() {
//        val intent = Intent(Constants.ACTION_UPDATE_COMPLETED)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }

    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Data Available")
            .setContentText("Tap to view new data")
            .setAutoCancel(true) // Notifikasi akan otomatis hilang ketika ditekan

        // Intent yang akan dijalankan ketika notifikasi ditekan
        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(pendingIntent)

        // Tampilkan notifikasi
        notificationManager.notify(1, notificationBuilder.build())
    }



}