package com.android.kaloriku.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.kaloriku.R
import com.android.kaloriku.authentication.TabAdapter
import com.android.kaloriku.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavigationView.setupWithNavController(navController)
        }
    }
}