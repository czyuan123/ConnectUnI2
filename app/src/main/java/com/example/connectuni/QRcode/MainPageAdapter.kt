package com.example.connectuni.QRcode

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.connectuni.QRcode.ScanHistory

@Suppress("DEPRECATION")
class MainPageAdapter (fm: FragmentManager): FragmentStatePagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        //which fragment needed according to the menu icon that we select
        return when(position){
            0-> {
                QRfragment1.newInstance()}
            1-> {
                ScanHistory.newInstance(
                    ScanHistory.ResultListType.CHECKIN_RESULT)}
            2-> {
                ScanHistory.newInstance(
                    ScanHistory.ResultListType.CHECKOUT_RESULT)}
            else->{
                QRfragment1()
            }
        }
    }

    override fun getCount(): Int {
        //Total 3 fragment so return 3
        return 3
    }
}