package com.android.kaloriku.roomDb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_harian_table")
data class DataHarian (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "token")
    val token: String,
    @ColumnInfo(name = "namaMakanan")
    val namaMakanan: String,
    @ColumnInfo(name = "kalori")
    val kalori: Float,
    @ColumnInfo(name = "jumlah")
    val jumlah: Float,
    @ColumnInfo(name = "satuan")
    val satuan: String,
    @ColumnInfo(name = "tanggal")
    val tanggal: String,
    @ColumnInfo(name = "waktu")
    val waktu: String,
)