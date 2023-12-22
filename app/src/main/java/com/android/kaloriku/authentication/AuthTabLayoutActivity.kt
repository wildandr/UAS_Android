package com.android.kaloriku.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.android.kaloriku.R
import com.android.kaloriku.databinding.ActivityAuthTabLayoutBinding

class AuthTabLayoutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthTabLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthTabLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        with(binding){

            viewPager.adapter = TabAdapter(supportFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        }

    }
}