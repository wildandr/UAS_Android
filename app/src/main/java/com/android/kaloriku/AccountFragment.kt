package com.android.kaloriku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.android.kaloriku.authentication.AuthTabLayoutActivity
import com.android.kaloriku.authentication.EditAccountActivity
import com.android.kaloriku.data.DataUser
import com.android.kaloriku.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

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
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener{
            Firebase.auth.signOut()
            requireActivity().finish()

            val intent = Intent(requireContext(), AuthTabLayoutActivity::class.java)
            startActivity(intent)
        }

        binding.btnEdit.setOnClickListener{
            val intent = Intent(requireContext(), EditAccountActivity::class.java)
            startActivity(intent)
        }

        getDataUser()

        var uid = ""
        var id_item = ""

        dataUserLiveData.observe(viewLifecycleOwner) { dataUserList ->
            id_item = dataUserList[0].id
            uid = dataUserList[0].uid
            val nama = dataUserList[0].nama
            val gender = dataUserList[0].gender
            val program = dataUserList[0].program
            val usia = dataUserList[0].usia
            val weight = dataUserList[0].weight
            val height = dataUserList[0].height
            val targetWeight = dataUserList[0].targetWeight
            val kaloriHarian = dataUserList[0].kaloriHarian

            with(binding){
                txtNama.text = nama
                txtGender.text = gender
                txtProgram.text = program
                txtUsia.text = usia.toString()
                txtBeratBadan.text = weight.toString()
                txtTinggiBadan.text = height.toString()
                txtTargetBeratBadan.text = targetWeight.toString()
                txtTargetKalori.text = kaloriHarian.toString()

            }

        }
    }

    private fun getDataUser() {

        auth = Firebase.auth
        val currentUserUid = auth.currentUser?.uid.toString()

        dataUserCollectionRef.whereEqualTo("uid", currentUserUid).get().addOnSuccessListener { snapshot ->
            val dataUser = snapshot.documents.map { document ->

                Log.d("id", document.id)
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
                    document.getString("gender") ?: "",
                    document.getString("program") ?: "",
                    document.getLong("usia")?.toInt() ?: 0,
                    document.getLong("height")?.toFloat() ?: 0.0f,
                    document.getLong("weight")?.toFloat() ?: 0.0f,
                    document.getLong("targetWeight")?.toFloat() ?: 0.0f,
                    document.getLong("kaloriHarian")?.toInt() ?: 0,
                )
            }
            dataUserLiveData.postValue(dataUser)

            Log.d("Data User", "Retrieved data: ${dataUser}")

        }.addOnFailureListener { exception ->
            Log.e("Data User", "Error getting data: $exception")
        }
    }

}