package com.junseok.bluebox.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.junseok.bluebox.Fragment.DeliveryFragment
import com.junseok.bluebox.Fragment.HistoryFragment


class BlueBoxViewPagerAdapter: FragmentPagerAdapter {
    val items = arrayListOf<Fragment>() //프레그먼트 리스트
    private val titleList = mutableListOf<String>("배송","기록")

    //생성자함수 - 프레그먼트 추가
    constructor(fm: FragmentManager):super(fm){
        items.add(DeliveryFragment())
        items.add(HistoryFragment())
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> DeliveryFragment()
            1 -> HistoryFragment()
            else -> null!!
        }
    }

    override fun getCount(): Int {
        return items.size
    }

    /*
    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
     */
}