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
import com.android.kaloriku.admin.AdminActivity
import com.android.kaloriku.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.kaloriku.R

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    private val firestore = FirebaseFirestore.getInstance()
    private val roleCollectionRef  = firestore.collection("data_role")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkRole(currentUser.uid)
        }

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Log dan navigasi
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        user?.let {
                            Log.d(TAG, it.uid)
                            checkRole(it.uid)
                            showLoginSuccessNotification() // Menampilkan notifikasi
                            // Navigasi akan dipanggil dari dalam checkRole
                        }
                    } else {
                        // Jika login gagal, tampilkan pesan ke pengguna.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            requireContext(),
                            "login failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }
    }


    private fun showLoginSuccessNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ID dan nama channel yang digunakan untuk notifikasi (diperlukan untuk Oreo dan yang lebih baru)
        val channelId = "login_channel_id"
        val channelName = "Login Notifications"

        // Membuat channel untuk notifikasi (diperlukan untuk Android Oreo dan yang lebih baru)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Membangun notifikasi dan menetapkan teks serta aksi yang diinginkan
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_notification) // set icon notifikasi
            .setContentTitle("Login Berhasil")
            .setContentText("Anda telah berhasil login.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Memicu notifikasi
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun navigateToDashboard() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    private fun navigateToAdmin() {
        val intent = Intent(requireContext(), AdminActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun checkRole(uid: String) {
        roleCollectionRef.whereEqualTo("uid", uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!task.result?.isEmpty!!) {
                        for (document in task.result.documents) {

                            when (document.getString("role")) {
                                "user" -> {
                                    Log.d(TAG, "user")
                                    navigateToDashboard()
                                }
                                "admin" -> {
                                    Log.d(TAG, "admin")
                                    navigateToAdmin()
                                }
                                else -> {

                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "User not found in the role collection")
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.exception)
                }
            }
    }


}
