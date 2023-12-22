package com.android.kaloriku.data

import com.google.firebase.firestore.Exclude

data class DataUser(
    @get:Exclude @set:Exclude var id: String = "",
    var uid: String = "",
    var nama: String = "",
    var gender: String = "",
    var program: String = "",
    var usia: Int = 0,
    var height: Float = 0.0F,
    var weight: Float = 0.0F,
    var targetWeight: Float = 0.0F,
    var kaloriHarian: Int = 0,
)
