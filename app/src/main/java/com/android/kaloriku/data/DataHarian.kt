package com.android.kaloriku.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.util.Date

data class DataHarian(
    @set:Exclude var id: String = "",
    var nama: String = "",
    var jumlah: Int = 0,
    var kaloriTotal: Int = 0,
    var waktu: Date = Date()
) {
    // Add a custom getter and setter for the 'waktu' property
    fun getWaktuTimestamp(): Timestamp {
        return Timestamp(waktu)
    }

    fun setWaktuTimestamp(timestamp: Timestamp) {
        waktu = timestamp.toDate()
    }
}
