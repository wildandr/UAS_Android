package com.android.kaloriku

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.kaloriku.data.DataMakanan
import com.android.kaloriku.databinding.ActivityTambahMakananBinding
import com.android.kaloriku.roomDb.DataHarian
import com.android.kaloriku.roomDb.DataHarianDao
import com.android.kaloriku.roomDb.DataHarianDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TambahMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMakananBinding
    private lateinit var foodAdapter: AddFromListAdapter

    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao

    private val firestore = FirebaseFirestore.getInstance()
    private val roleCollectionRef = firestore.collection("data_makanan")
    private lateinit var auth: FirebaseAuth

    private val dataMakananLiveData: MutableLiveData<List<DataMakanan>>
            by lazy {
                MutableLiveData<List<DataMakanan>>()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTambahMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(this)
        dataHarians = db!!.dataHarianDao()

        getAllData()

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                val searchText = editable.toString().trim()

                fetchDataAndObserve(searchText)
            }
        })


        dataMakananLiveData.observe(this@TambahMakananActivity) { data ->
            foodAdapter.submitList(data)
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser

        foodAdapter = AddFromListAdapter(emptyList()) { selectedFood ->

            val uid = currentUser?.uid.toString()
            val namaMakanan = selectedFood.namaMakanan
            val kalori = selectedFood.kalori
            val jumlah = selectedFood.jumlah
            val satuan = selectedFood.satuan
            val tanggal = getTodayDate()
            val waktu = getTodayTime()

            val dataHarian = DataHarian(
                token = uid,
                namaMakanan = namaMakanan,
                kalori = kalori,
                jumlah = jumlah,
                satuan = satuan,
                waktu = waktu,
                tanggal = tanggal
            )

            insertDataHarian(dataHarian)

            val intent = Intent(this@TambahMakananActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rvMakanan.adapter = foodAdapter
        binding.rvMakanan.layoutManager = LinearLayoutManager(this@TambahMakananActivity)

        with(binding) {
            btnCustomMakanan.setOnClickListener {
                navigateToCustomMakananActivity()
            }
        }

    }

    private fun navigateToCustomMakananActivity() {
        val intent = Intent(this@TambahMakananActivity, CustomMakananActivity::class.java)
        startActivity(intent)
    }

    private fun getAllData() {
        roleCollectionRef.get()
            .addOnSuccessListener { result ->
                val dataList = mutableListOf<DataMakanan>()
                for (document in result) {
                    val data = DataMakanan(
                        document.id,
                        document.getString("namaMakanan") ?: "",
                        document.getLong("kalori")?.toFloat() ?: 0.0f,
                        document.getLong("jumlah")?.toFloat() ?: 0.0f,
                        document.getString("satuan") ?: "",
                    )
                    dataList.add(data)
                    foodAdapter.submitList(dataList)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun insertDataHarian(dataHarian: DataHarian) {
        CoroutineScope(Dispatchers.IO).launch {
            dataHarians.insert(dataHarian)
        }
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Ingat, bulan dimulai dari 0
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }

    fun getTodayTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("%02d:%02d", hour, minute)
    }

    private fun fetchDataAndObserve(searchQuery: String = "") {
        try {
            val makananCollection = firestore.collection("data_makanan")

            // Add a condition to filter data based on the search query
            val query = if (searchQuery.isNotEmpty()) {

                makananCollection.whereGreaterThanOrEqualTo("namaMakanan", searchQuery)
                    .whereLessThanOrEqualTo("namaMakanan", searchQuery + "\uf8ff")
            } else {
                makananCollection
            }

            // Observe Firestore changes
            query.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this@TambahMakananActivity, exception.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val makanans = mutableListOf<DataMakanan>()
                    for (document in documents) {
                        val bukuId = document.id
                        val makanan = document.toObject(DataMakanan::class.java).copy(id = bukuId)
                        makanans.add(makanan)
                    }

                    // Update the UI with the Firestore data
                    foodAdapter.submitList(makanans)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@TambahMakananActivity, e.message, Toast.LENGTH_LONG).show()
            Log.d("ERRORKU", e.toString())
        }
    }
}