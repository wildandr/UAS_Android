package com.android.kaloriku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.kaloriku.databinding.ActivityEditBinding
import com.android.kaloriku.roomDb.DataHarian
import com.android.kaloriku.roomDb.DataHarianDao
import com.android.kaloriku.roomDb.DataHarianDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var dataHarians: DataHarianDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DataHarianDatabase.getDatabase(this@EditActivity)
        dataHarians = db!!.dataHarianDao()


        val id = intent.getStringExtra("id").toString()
        val namaMakanan = intent.getStringExtra("namaMakanan").toString()
        val token = intent.getStringExtra("token").toString()
        val kalori = intent.getStringExtra("kalori").toString()
        val jumlah = intent.getStringExtra("jumlah").toString()
        val satuan = intent.getStringExtra("satuan").toString()
        val tanggal = intent.getStringExtra("tanggal").toString()
        val waktu = intent.getStringExtra("waktu").toString()

        Log.d("Intent Data", "Intent Data $id $namaMakanan $token $kalori $jumlah $satuan $tanggal $waktu")

        with(binding) {
            edtNamaMakanan.setText(namaMakanan)
            edtKaloriMakanan.setText(kalori)
            edtJumlahMakanan.setText(jumlah)
            edtSatuanMakanan.setText(satuan)
            edtTime.setText(waktu)

            btnSimpan.setOnClickListener {
                val edtNamaMakanan = edtNamaMakanan.text.toString()
                val edtKalori = edtKaloriMakanan.text.toString()
                val edtJumlah = edtJumlahMakanan.text.toString()
                val edtSatuan = edtSatuanMakanan.text.toString()
                val edtWaktu = edtTime.text.toString()

                val dataHarian = DataHarian(
                    id = id.toInt(),
                    token = token,
                    namaMakanan = edtNamaMakanan,
                    kalori = edtKalori.toFloat(),
                    jumlah = edtJumlah.toFloat(),
                    satuan = edtSatuan,
                    tanggal = tanggal,
                    waktu = edtWaktu
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dataHarians.update(dataHarian)
                }

                finish()
            }
        }

    }
}