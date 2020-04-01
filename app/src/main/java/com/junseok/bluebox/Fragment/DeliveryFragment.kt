package com.junseok.bluebox.Fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.ListFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.junseok.bluebox.Adapter.DeliveryAdapter
import com.junseok.bluebox.R
import com.junseok.bluebox.SQLite.Object.Delivery
import com.junseok.bluebox.SQLite.DBHelper.database
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.android.synthetic.main.delivery_fagment.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class DeliveryFragment: ListFragment(),View.OnClickListener{
    //Date 변수
    private var now = LocalDateTime.now()

    //history type
    private val CHANGE_PASSWORD:String = "비밀번호 변경"

    private val BOX_ON:String = "물품 보관"
    private val BOX_OFF:String = "물품 찾음"
    private val DOOR_ON:String = "문 열림"
    private val DOOR_OFF:String = "문 닫힘"

    private val LOCK_ON:String = "잠금 설정"
    private val LOCK_OFF:String = "잠금 해제"
    private val WARN_ON:String = "경보 발생"
    private val WARN_OFF:String = "경보 해제"

    //컴포넌트 변수
    private lateinit var deliveryItem_listview:ListView
    private lateinit var no_list:TextView

    private lateinit var boxIcon:ImageView
    private lateinit var doorIcon:ImageView
    private lateinit var lockBtn:ImageButton
    private lateinit var warningBtn:ImageButton

    private lateinit var callDialogBtn:Button

    //배송목록 관련 변수
    val firebase = FirebaseDatabase.getInstance()
    val dvRef = firebase.getReference("Lock")
    private lateinit var adapter: DeliveryAdapter

    //보관함 관련 변수
    var passwordRef = dvRef.child("Password")
    var lockStateRef = dvRef.child("isLock")
    var doorStateRef = dvRef.child("isDoor")
    var sensStateRef = dvRef.child("isSens")
    var warnStateRef = firebase.getReference("Waring/isWarn")

    private lateinit var passwordVal:String

    var sensStateVal = false    //초기값 = false
    var doorStateVal = false    //초기값 = false
    var lockStateVal = false    //초기값 = false
    var warnStateVal = false    //초기값 = false

    //알림 관련 변수
    val channel = handleNotification()

    //DB 관련 변수
    private var items = ArrayList<Delivery>()
    private var firestore = FirebaseFirestore.getInstance()
    private var history_db = firestore.collection("History")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Logger 설정
        val formatStrategy = PrettyFormatStrategy.newBuilder().methodCount(2).build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy){})

        //context,view 변수
        var context = this.activity!!.applicationContext
        val view:View = inflater.inflate(R.layout.delivery_fagment,container,false)

        //view 초기화
        initView(view)



        //버튼 애니메이션
        val callBtnAnimation = callDialogBtn.background as AnimationDrawable
        callBtnAnimation.setEnterFadeDuration(10)
        callBtnAnimation.setExitFadeDuration(1500)
        callBtnAnimation.start()

        //리스트 어댑터
        adapter = DeliveryAdapter(context, items)
        deliveryItem_listview.adapter = adapter

        refreshList(deliveryItem_listview, no_list)

        sensStateRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Logger.d("lockStateRef error is ${p0.message} [${p0.code}]")
                Log.e("error",p0.toException().toString())
            }
            override fun onDataChange(p0: DataSnapshot) {
                sensStateVal = p0.getValue(Boolean::class.java)!!
                stateOnOff(sensStateVal, boxIcon, R.drawable.box_on, R.drawable.box_off,"BOX")
            }
        })

        doorStateRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Logger.d("lockStateRef error is ${p0.message} [${p0.code}]")
                Log.e("error",p0.toException().toString())
            }
            override fun onDataChange(p0: DataSnapshot) {
                doorStateVal = p0.getValue(Boolean::class.java)!!
                stateOnOff(doorStateVal, doorIcon, R.drawable.door_on, R.drawable.door_off,"DOOR")
            }
        })

        lockStateRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Logger.d("lockStateRef error is ${p0.message} [${p0.code}]")
                Log.e("error",p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                lockStateVal = p0.getValue(Boolean::class.java)!!
                if (lockStateVal){
                    lockBtn.setImageResource(R.drawable.lock_on)
                    passwordRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            passwordVal = p0.getValue(String::class.java).toString()
                            Logger.d("password = %s", p0.getValue(String::class.java).toString())
                            showNotification(channel,lockStateVal,passwordVal)
                            passwordView.text = passwordVal
                            passwordView.textSize = 20.0F
                            passwordView.letterSpacing = 0.5F
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            passwordView.text = "데이터를 불러올 수 없습니다."
                            passwordView.textSize = 12.0F
                            passwordView.letterSpacing = 0.0F
                        }
                    })
                }else{
                    passwordView.text = "보관함이 열려있음"
                    passwordView.textSize = 15.0F
                    passwordView.letterSpacing = 0.0F

                    lockBtn.setImageResource(R.drawable.lock_off)
                    showNotification(channel,lockStateVal)
                }
            }
        })

        warnStateRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Logger.d("lockStateRef error is ${p0.message} [${p0.code}]")
                Log.e("error",p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                warnStateVal = p0.getValue(Boolean::class.java)!!
                stateOnOff(warnStateVal, warningBtn, R.drawable.warn_on, R.drawable.warn_off,"WARN")
            }

        })


        callDialogBtn.setOnClickListener(this)
        warningBtn.setOnClickListener(this)
        lockBtn.setOnClickListener(this)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==110){
            var name:String = data!!.getStringExtra("name").toString()
            var num:Long = data.getStringExtra("number").toString().toLong()
            var company:String = data.getStringExtra("company").toString()

            insetItemData(name, num, company, now)
            refreshList(deliveryItem_listview,no_list)
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.callDialogBtn -> callDialog(this.activity!!.applicationContext)
            R.id.warningBtn -> warnOnOff()
            R.id.lockBtn -> lockOnOff()
        }
    }

    fun stateOnOff(
        stateVal: Boolean,
        icon: ImageView,
        onImg: Int,
        offImg: Int,
        state: String
    ) {
        var flag:String = ""
        if(stateVal){
            icon.setImageResource(onImg)

            if(state.equals("DOOR")){
                flag=DOOR_ON
            }else if(state.equals("BOX")){
                flag=BOX_ON
            }else if(state.equals("WARN")){
                flag=WARN_ON
            }
        }else{
            icon.setImageResource(offImg)
            if(state.equals("DOOR")){
                flag=DOOR_OFF
            }else if(state.equals("BOX")){
                flag=BOX_OFF
            }else if(state.equals("WARN")){
            flag=WARN_OFF
        }
    }
        historyAdd(flag,now)
    }

    private fun lockOnOff(){
        var flag:String = ""
        //열려있음
        if (!lockStateVal){
            lockBtn.setImageResource(R.drawable.lock_on)
            lockStateVal = true
            flag = LOCK_ON
        }else{
            lockBtn.setImageResource(R.drawable.lock_off)
            lockStateVal = false
            flag=LOCK_OFF
        }
        historyAdd(flag,now)
        lockStateRef.setValue(lockStateVal)
    }

    private fun warnOnOff(){
        var flag:String
        //열려있음
        if (!warnStateVal){
            warningBtn.setImageResource(R.drawable.warn_on)
            warnStateVal = true
            flag = WARN_ON
        }else{
            warningBtn.setImageResource(R.drawable.warn_off)
            warnStateVal = false
            flag = WARN_OFF
        }
        historyAdd(flag,now)
        warnStateRef.setValue(warnStateVal)
    }

    fun callDialog(context:Context){
        val custom = ItemDialogFragment(context)
        val fm = this@DeliveryFragment.fragmentManager
        custom.setTargetFragment(this, 326)
        custom.show(fm!!, "add")
    }

    fun handleNotification(): NotificationChannel {
        //알림채널 생성
        val notificationChannel = NotificationChannel(
            "myChannel","배송관련",NotificationManager.IMPORTANCE_DEFAULT
        )
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
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
        return notificationChannel
    }

    fun showNotification(notificationChannel:NotificationChannel, lockState: Boolean, password: String){
        val notificationManager:NotificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        var contextTitle: String? = null
        var contextText: String? = null
        if (lockState){
            contextText = "비밀번호 : ${password}"
            contextTitle = "보관함이 잠금되었습니다."
        }
        val notification: Notification? = Notification.Builder(activity)
            .setContentTitle("${contextTitle}").setContentText("$contextText")
            .setSmallIcon(R.mipmap.ic_launcher).setChannelId("myChannel").build()
        notificationManager.notify(1,notification)
    }
    fun showNotification(notificationChannel:NotificationChannel, lockState: Boolean){
        val notificationManager:NotificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        var contextTitle: String? = null
        var contextText: String? = null
        if (!lockState){
            contextTitle = "보관함이 열렸습니다."
            contextText = "잠기면 비밀번호가 부여됩니다."
        }
        val notification: Notification? = Notification.Builder(activity)
            .setContentTitle("${contextTitle}").setContentText("$contextText")
            .setSmallIcon(R.mipmap.ic_launcher).setChannelId("myChannel").build()
        notificationManager.notify(1,notification)
    }

    fun insetItemData(
        name: String,
        num: Long,
        company: String,
        date: LocalDateTime
    ){
        var date_format = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        database.use {
            insert(Delivery.table, Delivery.name to name,  Delivery.company to company, Delivery.number to num, Delivery.date to date_format)
        }
    }

    fun historyAdd(
        type:String,
        now:LocalDateTime
    ){
        val date_format = "yyyy년 MM월 dd일"
        val date = now.format(DateTimeFormatter.ofPattern(date_format))
        val time = now.format(DateTimeFormatter.ofPattern(timeFormat(now.hour)))

        val history = hashMapOf(
            "Type" to type,
            "Date" to date,
            "Time" to time
        )
        val document_title = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    }

    fun refreshList(listView: ListView, no_list: TextView) {
        database.use {
            var result = select(
                Delivery.table,
                Delivery.index,
                Delivery.name,
                Delivery.number,
                Delivery.company,
                Delivery.date)
            var dataItem = result.parseList(classParser<Delivery>())
            if(dataItem.isEmpty()){
                setEmpList()
            }else{
                items.clear()
                items.addAll(dataItem.asReversed())
                Logger.d("isisis ${dataItem}")
                for(item in items){
                    Logger.d("list is ${item._itemIndex} ${item._itemName} / ${item._itemNum}")
                }
                Logger.d("list size is ${items.size}")
            }
            adapter.notifyDataSetChanged()
        }
    }

    fun initView(view: View){
        deliveryItem_listview = view.find<ListView>(android.R.id.list)
        no_list = view.find<TextView>(R.id.noList)

        boxIcon = view.find<ImageView>(R.id.boxIcon)
        doorIcon = view.find<ImageView>(R.id.doorIcon)
        lockBtn = view.find<ImageButton>(R.id.lockBtn)
        warningBtn = view.find<ImageButton>(R.id.warningBtn)

        callDialogBtn = view.findViewById<Button>(R.id.callDialogBtn)
    }

    fun timeFormat(hour: Int): String {
        lateinit var timeFormat:String
        if(hour < 13){
            timeFormat = "(오전)HH시 mm분 ss초"
        }else{
            timeFormat = "(오후)${hour-12}시 mm분 ss초"
        }
        return timeFormat
    }

    fun setEmpList(){
        deliveryItem_listview.emptyView = no_list
    }

}