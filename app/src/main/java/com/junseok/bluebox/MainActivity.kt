package com.junseok.bluebox

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.junseok.bluebox.Fragment.DeliveryFragment
import com.junseok.bluebox.Fragment.HistoryFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.junseok.bluebox.Adapter.BlueBoxViewPagerAdapter
import com.orhanobut.logger.Logger
import com.google.firebase.iid.FirebaseInstanceId
import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.InstanceIdResult
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import org.jetbrains.anko.find


class MainActivity : AppCompatActivity() {
    var deliveryFragment: DeliveryFragment = DeliveryFragment()
    var historyFragment: HistoryFragment = HistoryFragment()
    var pagerAdapter: BlueBoxViewPagerAdapter =
        BlueBoxViewPagerAdapter(supportFragmentManager)
    private var tablayout_icon = intArrayOf(R.drawable.tab_delivery_selector,R.drawable.tab_history_selector)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val formatStrategy = PrettyFormatStrategy.newBuilder().methodCount(2).build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy){})
        //PagerAdapter 프레그먼트 추가
        //Adapter 설정
        viewPager.adapter = pagerAdapter
        tab.setupWithViewPager(viewPager)
        setTabIcons()
    }

    fun setTabIcons(){
        tab.getTabAt(0)?.setIcon(tablayout_icon[0])
        tab.getTabAt(1)?.setIcon(tablayout_icon[1])
    }
}
