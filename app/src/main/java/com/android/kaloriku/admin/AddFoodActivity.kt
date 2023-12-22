package com.android.kaloriku.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.kaloriku.data.DataMakanan
import com.android.kaloriku.databinding.ActivityAddFoodBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val dataMakananCollectionRef  = firestore.collection("data_makanan")

    private fun addMakanan(dataMakanan: DataMakanan){
        dataMakananCollectionRef.add(dataMakanan).addOnFailureListener{
            Log.d("AddFoodActivity", "Error adding food : ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnAddFood.setOnClickListener {
                val namaMakanan = edtNamaMakanan.text.toString()
                val kalori = edtKaloriMakanan.text.toString()
                val jumlah = edtJumlahMakanan.text.toString()
                val satuan = edtSatuanMakanan.text.toString()

                if (namaMakanan.isNotEmpty() && kalori.isNotEmpty() && jumlah.isNotEmpty() && satuan.isNotEmpty()) {

                    val dataMakanan = DataMakanan(
                        namaMakanan =  namaMakanan,
                        kalori = kalori.toFloat(),
                        jumlah = jumlah.toFloat(),
                        satuan = satuan)
                    addMakanan(dataMakanan)

                    val intent = Intent(this@AddFoodActivity, AdminActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@AddFoodActivity, "Harap isi semua bidang.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
