package com.junseok.bluebox.SQLite.DBHelper

import android.database.sqlite.SQLiteDatabase
import android.content.Context
import androidx.fragment.app.ListFragment
import com.junseok.bluebox.Fragment.DeliveryFragment
import com.junseok.bluebox.SQLite.Object.Delivery
import org.jetbrains.anko.db.*

class DeliveryDBHelper private constructor(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "BlueBoxDatabase", null,1) {
    init{
        instance = this
    }

    companion object{
        private var instance: DeliveryDBHelper? = null

        @Synchronized
        fun getInstance(df: DeliveryFragment): DeliveryDBHelper {
            if(instance ==null){
                instance == DeliveryDBHelper(
                    df.activity!!.applicationContext
                )
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //테이블 생성
        db!!.createTable(
            Delivery.table, true,
            //인덱스
            Delivery.index to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            //상품명
            Delivery.name to TEXT + NOT_NULL,
            //회사명
            Delivery.company to TEXT + NOT_NULL,
            //운송장번호
            Delivery.number to TEXT + NOT_NULL,
            //등록 날짜 및 시간
            Delivery.date to TEXT + NOT_NULL
            )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.dropTable(Delivery.table,true)
    }
}
val DeliveryFragment.database : DeliveryDBHelper
    get() = DeliveryDBHelper.getInstance(this)