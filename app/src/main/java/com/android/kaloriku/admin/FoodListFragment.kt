package com.android.kaloriku.admin

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.kaloriku.data.DataMakanan
import com.android.kaloriku.databinding.FragmentFoodListBinding
import com.google.firebase.firestore.FirebaseFirestore


class FoodListFragment : Fragment() {

    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val dataMakananCollectionRef  = firestore.collection("data_makanan")

    private val dataMakananLiveData : MutableLiveData<List<DataMakanan>>
            by lazy {
                MutableLiveData<List<DataMakanan>>()
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
        binding = FragmentFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            getAllData()

            // Observe changes in dataMakananLiveData
            dataMakananLiveData.observe(viewLifecycleOwner) { data ->
                foodAdapter.submitList(data)
            }

            foodAdapter = FoodListAdapter(emptyList(),
            { selectedFood ->

            },
            { selectedFood ->
                val intent = Intent(activity, EditFoodActivity::class.java)

                intent.putExtra("id", selectedFood.id)
                intent.putExtra("namaMakanan", selectedFood.namaMakanan)
                intent.putExtra("kalori", selectedFood.kalori.toString())
                intent.putExtra("jumlah", selectedFood.jumlah.toString())
                intent.putExtra("satuan", selectedFood.satuan)

                startActivity(intent)
                requireActivity().finish()
            },
            { selectedFood ->

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        delete(selectedFood)
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            })

            rvFoodList.adapter = foodAdapter
            rvFoodList.layoutManager = LinearLayoutManager(requireContext())

            btnAddFood.setOnClickListener {
                val intent = Intent(activity, AddFoodActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
        }

    }

    private fun getAllData() {
        dataMakananCollectionRef.get()
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

                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    private fun delete(dataMakanan: DataMakanan){

        val id = dataMakanan.id

        dataMakananCollectionRef.document(id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting budget")
        }
        getAllData()
    }


}