package com.android.kaloriku.data

import com.google.firebase.firestore.Exclude

data class DataMakanan(
    @get:Exclude @set:Exclude var id: String = "",
    var namaMakanan: String = "",
    var kalori: Float = 0.0F,
    var jumlah: Float = 0.0F,
    var satuan: String = "",
)
