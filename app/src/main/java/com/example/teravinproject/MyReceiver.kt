package com.example.teravinproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle pesan yang diterima dari Local Broadcast Manager di sini
        if (intent?.action == "ACTION_UPDATE_COMPLETED") {
            // Local Broadcast Manager telah berjalan, Anda dapat melakukan tindakan yang sesuai di sini
        }
    }
}
