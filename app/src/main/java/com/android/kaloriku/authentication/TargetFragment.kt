package com.android.kaloriku.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.kaloriku.R
import com.android.kaloriku.databinding.FragmentTargetBinding


class TargetFragment : Fragment() {

    private lateinit var binding: FragmentTargetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            binding = FragmentTargetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            spinnerTarget.adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.target_array,
                android.R.layout.simple_spinner_dropdown_item
            )
            spinnerGender.adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_array,
                android.R.layout.simple_spinner_dropdown_item
            )

            btnNext.setOnClickListener {
                navigateToSignUpFragment()
            }
        }

    }

    private fun navigateToSignUpFragment() {

        val selectedProgram = binding.spinnerTarget.selectedItem.toString()
        val selectedGender = binding.spinnerGender.selectedItem.toString()
        val usia = binding.edtUsia.text.toString().toInt()
        val weight = binding.edtWeight.text.toString().toFloat()
        val height = binding.edtHeight.text.toString().toFloat()
        val targetWeight = binding.edtTargetWeight.text.toString().toFloat()
        val kaloriHarian = binding.edtKaloriHarian.text.toString().toInt()


        val bundle = Bundle().apply {
            putString("PROGRAM", selectedProgram)
            putString("GENDER", selectedGender)
            putInt("USIA", usia)
            putFloat("WEIGHT", weight)
            putFloat("HEIGHT", height)
            putFloat("TARGET_WEIGHT", targetWeight)
            putInt("KALORI_HARIAN", kaloriHarian)
        }

        val signUpFragment = SignUpFragment()
        signUpFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_signup, signUpFragment) // Assuming R.id.container is the container layout in AuthTabLayoutActivity
            .commit()
    }
}