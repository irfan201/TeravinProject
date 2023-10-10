package com.example.teravinproject.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teravinproject.R
import com.example.teravinproject.model.Movie

class MovieAdapter(private var listMovie: ArrayList<Movie>): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle : TextView = itemView.findViewById(R.id.judul_film)
        val tvTanggal : TextView = itemView.findViewById(R.id.tgl_film)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<Movie>) {
        listMovie.clear()
        listMovie.addAll(newData)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieAdapter.MovieViewHolder, position: Int) {
        val movie = listMovie[position]
        holder.tvTitle.text = movie.title
        holder.tvTanggal.text = movie.tanggal

        Log.d("MovieAdapter", "Binding item at position $position: ${movie.title} - ${movie.tanggal}")
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }
}