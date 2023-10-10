package com.example.teravinproject.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "original_title")
    val title: String,

    @ColumnInfo(name = "release_date")
    val tanggal: String,
)