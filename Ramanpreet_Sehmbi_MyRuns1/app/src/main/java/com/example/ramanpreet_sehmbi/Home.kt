package com.example.ramanpreet_sehmbi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Home : AppCompatActivity() {

    private lateinit var startFragment: Start
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment

    private lateinit var tabs: ArrayList<Fragment>
    private lateinit var hometabs: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var tabLayoutStateAdapter: TabsFragmentStateAdapter;
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private val TAB_TEXT = arrayOf("Start", "History", "Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        startFragment = Start()
        historyFragment = HistoryFragment()
        settingsFragment = SettingsFragment()
        tabs = ArrayList()


        tabs.add(startFragment)
        tabs.add(historyFragment)
        tabs.add(settingsFragment)

        hometabs = findViewById(R.id.home_tabs_id)
        viewPager = findViewById(R.id.home_view_pager_id)

        tabLayoutStateAdapter = TabsFragmentStateAdapter(this,tabs)
        viewPager.adapter = tabLayoutStateAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy(){
                tab: TabLayout.Tab, position:Int ->
            tab.text = TAB_TEXT[position]
        }
        tabLayoutMediator = TabLayoutMediator(hometabs, viewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}