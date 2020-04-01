package com.junseok.bluebox.Fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.junseok.bluebox.Adapter.HistoryAdapter
import com.junseok.bluebox.R
import com.junseok.bluebox.SQLite.DBHelper.database
import com.junseok.bluebox.SQLite.Object.History
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find

@Suppress("DEPRECATION")
class HistoryFragment: ListFragment() {
    private lateinit var history_listview:ListView
    private lateinit var history_adapter:HistoryAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("History")

    private val history = ArrayList<History>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Logger 설정
        val formatStrategy = PrettyFormatStrategy.newBuilder().methodCount(2).build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy){})

        var context = this.activity!!.applicationContext
        val view:View = inflater.inflate(R.layout.history_fagment,container,false)

        history_listview = view.find<ListView>(android.R.id.list)
        history_adapter = HistoryAdapter(context,history)
        history_listview.adapter = history_adapter

        collection.get().addOnFailureListener { exception -> Log.d("errorMsg",exception.toString()) }
            .addOnSuccessListener { querySnapshot ->
//                var initList = MutableList<String>()
                Log.d("queryMsg",querySnapshot.documents.toString())
                var arr = ArrayList<History>()
                for (document in querySnapshot){
                    val type:String? = document.data.get("Type").toString()
                    val date:String? = document.data.get("Date").toString()
                    val time:String? = document.data.get("Time").toString()
                    val ht = History(type, date, time)
                    arr.add(ht)
                    Logger.d("history is ${ht}")
                }
                history.addAll(arr.asReversed())
                history_adapter.notifyDataSetChanged()
            }
        collection.addSnapshotListener{
            querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
             Logger.d("${firebaseFirestoreException}")
            }else {
                var arr = ArrayList<History>()
                for (document in querySnapshot!!.documents){
                    val type:String? = document.data!!.get("Type").toString()
                    val date:String? = document.data!!.get("Date").toString()
                    val time:String? = document.data!!.get("Time").toString()
                    val ht = History(type, date, time)
                    arr.add(ht)
                }
                history.clear()
                history.addAll(arr.asReversed())
                history_adapter.notifyDataSetChanged()
            }
        }
        return view
    }
    fun handleNotification(): NotificationChannel {
        //알림채널 생성
        val notificationChannel = NotificationChannel(
            "myChannel","보안관련", NotificationManager.IMPORTANCE_DEFAULT
        )
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notificationChannel.description = "channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)   //진동
            notificationChannel.setVibrationPattern(
                longArrayOf(
                    100,
                    200,
                    100,
                    200
                )
            )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

//        var db:HistoryDBHelper = HistoryDBHelper.getInstance(this)
//        db.use {  }

        return notificationChannel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("msg","onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d("msg","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("msg","onResume")
    }

    fun showNotification(notificationChannel: NotificationChannel, warningState: Boolean){
        val notificationManager: NotificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        var contextTitle: String? = null
        var contextText: String? = null
        if (!warningState){
            contextText = "비상경보부저가 작동되고있는 중입니다."
            contextTitle = "강력한 충격이 감지되었습니다!"
        }
        val notification: Notification? = Notification.Builder(activity)
            .setContentTitle("${contextTitle}").setContentText("$contextText")
            .setSmallIcon(R.mipmap.ic_launcher).setChannelId("myChannel").build()
        notificationManager.notify(2,notification)
    }
}
