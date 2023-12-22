package com.android.kaloriku

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.kaloriku.databinding.FragmentHistoryBinding
import com.android.kaloriku.roomDb.DataHarian
import com.android.kaloriku.roomDb.DataHarianDao
import com.android.kaloriku.roomDb.DataHarianDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao
    private lateinit var foodAdapter: MakananListAdapter

    private lateinit var auth: FirebaseAuth



    private val dataMakananLiveData : MutableLiveData<List<DataHarian>>
            by lazy {
                MutableLiveData<List<DataHarian>>()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(requireContext())
        dataHarians = db!!.dataHarianDao()

        getAllData()

        foodAdapter = MakananListAdapter(emptyList(),
        {selectedFood ->

            Log.d("selectedFood", selectedFood.toString())

        },{selectedFood -> val intent = Intent(requireContext(), EditActivity::class.java)

                Log.d("id", selectedFood.id.toString())
                Log.d("token", selectedFood.token)
                Log.d("namaMakanan", selectedFood.namaMakanan)
                Log.d("kalori", selectedFood.kalori.toString())
                Log.d("jumlah", selectedFood.jumlah.toString())
                Log.d("satuan", selectedFood.satuan)
                Log.d("tanggal", selectedFood.tanggal)
                Log.d("waktu", selectedFood.waktu)

                intent.putExtra("id", selectedFood.id.toString())
                intent.putExtra("token", selectedFood.token)
                intent.putExtra("namaMakanan", selectedFood.namaMakanan)
                intent.putExtra("kalori", selectedFood.kalori.toString())
                intent.putExtra("jumlah", selectedFood.jumlah.toString())
                intent.putExtra("satuan", selectedFood.satuan)
                intent.putExtra("tanggal", selectedFood.tanggal)
                intent.putExtra("waktu", selectedFood.waktu)

                startActivity(intent)

        },{selectedFood ->

            Log.d("selectedFood", selectedFood.toString())

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { _, _ ->
                    deleteDataHarian(selectedFood)
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        })

        dataMakananLiveData.observe(viewLifecycleOwner) { data ->
            foodAdapter.submitList(data)
        }

        binding.rvMakanan.adapter = foodAdapter
        binding.rvMakanan.layoutManager = LinearLayoutManager(requireContext())

        with(binding) {

            btnTambahMakanan.setOnClickListener {
                navigateToTambahMakananFragment()
            }

        }
    }

    private fun navigateToTambahMakananFragment() {
        val intent = Intent(requireContext(), TambahMakananActivity::class.java)
        startActivity(intent)
    }

    private fun getAllData() {
        dataHarians.allDataHarian.observe(viewLifecycleOwner) { data ->

            auth = Firebase.auth
            val currentUserUid = auth.currentUser?.uid.toString()
            val today = getTodayDate()

            val dataList = mutableListOf<DataHarian>()
            for (document in data) {

                Log.d("data id", document.id.toString())

                if (currentUserUid == document.token && today == document.tanggal) {
                    val dataHarian = DataHarian(
                        id = document.id,
                        token = document.token,
                        namaMakanan = document.namaMakanan,
                        kalori = document.kalori,
                        jumlah = document.jumlah,
                        satuan = document.satuan,
                        tanggal = document.tanggal,
                        waktu = document.waktu
                    )
                    dataList.add(dataHarian)
                }
            }

            Log.d("dataList", dataList.toString())
            foodAdapter.submitList(dataList)
        }
    }

    private fun deleteDataHarian(dataHarian: DataHarian) {
        CoroutineScope(Dispatchers.IO).launch {
            dataHarians.delete(dataHarian)
        }
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Ingat, bulan dimulai dari 0
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }

}