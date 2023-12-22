package com.android.kaloriku.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.kaloriku.R
import com.android.kaloriku.data.DataMakanan
import com.android.kaloriku.databinding.ActivityEditFoodBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFoodBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val dataMakananCollectionRef  = firestore.collection("data_makanan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val nama = intent.getStringExtra("namaMakanan")
        val kalori = intent.getStringExtra("kalori")
        val jumlah = intent.getStringExtra("jumlah")
        val satuan = intent.getStringExtra("satuan")

        with(binding){
            edtNamaMakanan.setText(nama)
            edtKaloriMakanan.setText(kalori)
            edtJumlahMakanan.setText(jumlah)
            edtSatuanMakanan.setText(satuan)

            btnSimpan.setOnClickListener {
                val namaMakanan = edtNamaMakanan.text.toString()
                val kaloriMakanan = edtKaloriMakanan.text.toString()
                val jumlahMakanan = edtJumlahMakanan.text.toString()
                val satuanMakanan = edtSatuanMakanan.text.toString()

                val dataMakanan = DataMakanan(
                    id = id.toString(),
                    namaMakanan = namaMakanan,
                    kalori = kaloriMakanan.toFloat(),
                    jumlah = jumlahMakanan.toFloat(),
                    satuan = satuanMakanan)

                update(dataMakanan)

                val intent = Intent(this@EditFoodActivity, AdminActivity::class.java)
                startActivity(intent)
            }


        }

    }

    private fun update(dataMakanan: DataMakanan){

        val id = dataMakanan.id

        dataMakananCollectionRef.document(id).set(dataMakanan).addOnFailureListener{
            Log.d("MainActivity", "Error updating budget : ")
        }
    }
}