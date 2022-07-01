package com.example.ramanpreet_sehmbi

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ramanpreet_sehmbi.Home.Companion.historyFragment


// Using class tutorial
class TabsFragmentStateAdapter(var activity: FragmentActivity, var fragmentList: ArrayList<Fragment>):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 1){
            historyFragment.refreshFragment(activity)
        }
        return super.getItemViewType(position)
    }

}