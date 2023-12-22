package com.android.kaloriku

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.android.kaloriku.data.DataUser
import com.android.kaloriku.databinding.FragmentDashboardBinding
import com.android.kaloriku.roomDb.DataHarian
import com.android.kaloriku.roomDb.DataHarianDao
import com.android.kaloriku.roomDb.DataHarianDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao

    private lateinit var auth: FirebaseAuth


    private val firestore = FirebaseFirestore.getInstance()

    private val dataHarianLiveData : MutableLiveData<List<DataHarian>>
            by lazy {
                MutableLiveData<List<DataHarian>>()
            }

    private val dataUserCollectionRef  = firestore.collection("data_user")
    private val dataUserLiveData : MutableLiveData<List<DataUser>>
            by lazy {
                MutableLiveData<List<DataUser>>()
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
        binding = FragmentDashboardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(requireContext())
        dataHarians = db!!.dataHarianDao()

        getAllData()
        getDataUser()

        with(binding){

            dataHarianLiveData.observe(viewLifecycleOwner) { dataHarianList ->
                dataUserLiveData.observe(viewLifecycleOwner) { dataUserList ->
                    val consumedCalories = dataHarianList.sumOf { it.kalori.toInt() }
                    val totalCalories = dataUserList.sumOf { it.kaloriHarian }

                    val progress = (consumedCalories.toFloat() / totalCalories.toFloat()) * 100

                    txtNama.text = dataUserList[0].nama
                    txtTargetKalori.text = totalCalories.toString()
                    txtKaloriMasuk.text = consumedCalories.toString()
                    sisaKalori.text = (totalCalories - consumedCalories).toString()
                    progressCircularIndicator.setProgress(progress.toInt(), true)

                    Log.d("dataHarian", dataHarianLiveData.value.toString())
                    Log.d("dataUser", dataUserLiveData.value.toString())

                    Log.d("consumedCalories", consumedCalories.toString())
                    Log.d("totalCalories", totalCalories.toString())
                }
            }
        }

    }

    private fun getDataUser() {

        auth = Firebase.auth
        val currentUserUid = auth.currentUser?.uid.toString()

        dataUserCollectionRef.whereEqualTo("uid", currentUserUid).get().addOnSuccessListener { snapshot ->
            val dataUser = snapshot.documents.map { document ->

                Log.d("uid", document.getString("uid") ?: "")
                Log.d("nama", document.getString("nama") ?: "")
                Log.d("program", document.getString("program") ?: "")
                Log.d("usia", document.getLong("usia")?.toInt().toString())
                Log.d("height", document.getLong("height")?.toFloat().toString())
                Log.d("weight", document.getLong("weight")?.toFloat().toString())
                Log.d("target_weight", document.getLong("targetWeight")?.toFloat().toString())
                Log.d("kalori_harian", document.getLong("kaloriHarian")?.toInt().toString())

                DataUser(
                    document.id,
                    document.getString("uid") ?: "",
                    document.getString("nama") ?: "",
                    document.getString("gender")?: "",
                    document.getString("program") ?: "",
                    document.getLong("usia")?.toInt() ?: 0,
                    document.getLong("height")?.toFloat() ?: 0.0f,
                    document.getLong("weight")?.toFloat() ?: 0.0f,
                    document.getLong("targetWeight")?.toFloat() ?: 0.0f,
                    document.getLong("kaloriHarian")?.toInt() ?: 0,
                )
            }
            dataUserLiveData.postValue(dataUser)

            Log.d("Data User", "Retrieved data: $dataUserLiveData")

        }.addOnFailureListener { exception ->
            Log.e("Data User", "Error getting data: $exception")
        }
    }

    private fun getAllData() {
        dataHarians.allDataHarian.observe(viewLifecycleOwner) { data ->

            auth = Firebase.auth
            val currentUserUid = auth.currentUser?.uid.toString()
            val today = getTodayDate()

            val dataList = mutableListOf<DataHarian>()
            for (document in data) {
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
            dataHarianLiveData.postValue(dataList)

            Log.d("dataList", dataList.toString())

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