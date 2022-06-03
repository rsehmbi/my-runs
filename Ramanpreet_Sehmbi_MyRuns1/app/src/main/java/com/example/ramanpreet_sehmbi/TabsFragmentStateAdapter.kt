package com.example.ramanpreet_sehmbi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// Using class tutorial
class TabsFragmentStateAdapter(activity: FragmentActivity, var fragmentList: ArrayList<Fragment>):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}