package com.example.connectuni.QRcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.connectuni.R
import kotlinx.android.synthetic.main.activity_main_scan.*

class MainScan : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan)

        setpageadapter()
        setBottomNavigation()
        setViewPageListener()
    }

    private fun setpageadapter(){
        val bundle : Bundle? = intent.extras
        val string : String? = intent.getStringExtra("Fragment")
        if(string == "Fragment A"){
            setViewPage1()
        }

        else{
            setViewPager2()
        }
    }

    private fun setViewPage1(){
        viewPager.adapter =
            MainPageAdapter(supportFragmentManager)
    }

    private fun setViewPager2(){
        viewPager.adapter =
            MainPageAdapter2(supportFragmentManager)
        viewPager.offscreenPageLimit = 2
    }
    //link the bottom navigation icon to the view page
    private fun setBottomNavigation(){
        bottomNavigationView2.setOnNavigationItemSelectedListener {
           viewPager.currentItem = when(it.itemId){
                R.id.scanMenuId -> 0
                R.id.recentScannedMenuId -> 1
                R.id.favouritesMenuId -> 2
               else -> 0
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
    //link the current selected menu item when swiping the fragment
    private fun setViewPageListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
               bottomNavigationView2.selectedItemId = when(position){
                    0-> R.id.scanMenuId
                    1-> R.id.recentScannedMenuId
                    2-> R.id.favouritesMenuId
                   else -> R.id.scanMenuId
                }
            }
        })
    }
}