package com.android.kaloriku.authentication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.kaloriku.MainActivity
import com.android.kaloriku.data.DataRole
import com.android.kaloriku.data.DataUser
import com.android.kaloriku.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    private val firestore = FirebaseFirestore.getInstance()
    private val roleCollectionRef  = firestore.collection("data_role")
    private val dataUserCollectionRef  = firestore.collection("data_user")

    private fun addRole(dataRole: DataRole){
        roleCollectionRef.add(dataRole).addOnFailureListener{
            Log.d("SignUp", "Error adding role : ")
        }
    }
    private fun addDataUser(dataUser: DataUser){
        dataUserCollectionRef.add(dataUser).addOnFailureListener{
            Log.d("SignUp", "Error adding data user : ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        arguments?.let {

        }

        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val program = arguments?.getString("PROGRAM")
        val gender = arguments?.getString("GENDER")
        val usia = arguments?.getInt("USIA")
        val weight = arguments?.getFloat("WEIGHT")
        val height = arguments?.getFloat("HEIGHT")
        val targetWeight = arguments?.getFloat("TARGET_WEIGHT")
        val kaloriHarian = arguments?.getInt("KALORI_HARIAN")

        val logMessage =
            "Received Data: Program=$program, Gender=$gender, Usia=$usia, Weight=$weight, Height=$height, Target Weight=$targetWeight kaloriHarian=$kaloriHarian"
        Log.d("SignUpFragment", logMessage)

        binding.btnDaftar.setOnClickListener {
            val nama = binding.edtNama.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtPasswordKonfirmasi.text.toString()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser

                            addRole(
                                DataRole(
                                    uid = user?.uid.toString(),
                                    nama = nama,
                                    role = "user"
                                )
                            )

                            addDataUser(
                                DataUser(
                                    uid = user?.uid.toString(),
                                    nama = nama,
                                    gender = gender.toString(),
                                    usia = usia?: 0,
                                    program = program.toString(),
                                    height = height?: 0.0F,
                                    weight = weight?: 0.0F,
                                    targetWeight = targetWeight?: 0.0F,
                                    kaloriHarian = kaloriHarian?: 0
                                )
                            )

                            navigateToDashboard()

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                requireContext(),
                                "sign up failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Passwords do not match.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Optional: Finish the current activity
    }
}
