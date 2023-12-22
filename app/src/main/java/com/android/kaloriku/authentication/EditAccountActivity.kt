package com.android.kaloriku.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.android.kaloriku.R
import com.android.kaloriku.data.DataUser
import com.android.kaloriku.databinding.ActivityEditAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EditAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAccountBinding

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    private val dataUserCollectionRef  = firestore.collection("data_user")
    private val dataUserLiveData : MutableLiveData<List<DataUser>>
            by lazy {
                MutableLiveData<List<DataUser>>()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataUser()

        with(binding){
            spinnerTarget.adapter = ArrayAdapter.createFromResource(
                this@EditAccountActivity,
                R.array.target_array,
                android.R.layout.simple_spinner_dropdown_item
            )
            spinnerGender.adapter = ArrayAdapter.createFromResource(
                this@EditAccountActivity,
                R.array.gender_array,
                android.R.layout.simple_spinner_dropdown_item
            )

            var uid = ""
            var id_item = ""

            dataUserLiveData.observe(this@EditAccountActivity) { dataUserList ->
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

                edtNama.setText(nama)
                edtUsia.setText(usia.toString())
                edtWeight.setText(weight.toString())
                edtHeight.setText(height.toString())
                edtTargetWeight.setText(targetWeight.toString())
                edtKaloriHarian.setText(kaloriHarian.toString())

            }

            btnSimpan.setOnClickListener {
                val selectedProgram = spinnerTarget.selectedItem.toString()
                val gender = spinnerGender.selectedItem.toString()
                val name = edtNama.text.toString()
                val usia = edtUsia.text.toString().toInt()
                val weight = edtWeight.text.toString().toFloat()
                val height = edtHeight.text.toString().toFloat()
                val targetWeight = edtTargetWeight.text.toString().toFloat()
                val kaloriHarian = edtKaloriHarian.text.toString().toInt()

                val dataUser = DataUser(
                    id = id_item,
                    uid = uid,
                    nama = name,
                    gender = gender,
                    program = selectedProgram,
                    usia = usia,
                    height = height,
                    weight = weight,
                    targetWeight = targetWeight,
                    kaloriHarian = kaloriHarian
                )

                Log.d("dataUser to Edit", dataUser.toString())

                update(dataUser)
                finish()
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

    private fun update(dataUser: DataUser){

        val id = dataUser.id
        Log.d("id", id)

        dataUserCollectionRef.document(id).set(dataUser).addOnFailureListener{
            Log.d("MainActivity", "Error updating budget : ")
        }
    }
}