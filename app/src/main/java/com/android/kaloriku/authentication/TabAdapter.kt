package com.android.kaloriku.authentication


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment()
            1 -> PenghubungFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
    override fun getCount(): Int {
        return 2
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Masuk"
            1 -> "Daftar"
            else -> null
        }
    }
}
