package com.junseok.bluebox.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.junseok.bluebox.R
import com.junseok.bluebox.SQLite.Object.History
import org.jetbrains.anko.find

class HistoryAdapter(val context: Context, val historyList: ArrayList<History>) : BaseAdapter(){
    private var inflater : LayoutInflater
    init {
        this.inflater = LayoutInflater.from(context)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View
        val hold: Viewholder
        if(convertView == null){
            view = this.inflater.inflate(R.layout.history_list_item,null)
            hold = Viewholder()

            hold.historyType = view.find<TextView>(R.id.historyType)
            hold.historyDate = view.find<TextView>(R.id.historyDate)
            hold.historyTime = view.find<TextView>(R.id.historyTime)

            view.tag = hold
        }else{
            hold = convertView.tag as Viewholder
            view = convertView
        }

//        hold.historyTime!!.setTextColor(Color.WHITE)
//        hold.historyType!!.setTextColor(Color.WHITE)
//        hold.historyDate!!.setTextColor(Color.WHITE)
//        parent!!.setBackgroundResource(R.color.listBackground)

        val item = historyList[position]
        hold.historyType?.text = item._type
        hold.historyTime?.text = item._time
        hold.historyDate?.text = item._date

//        if (position%2!=1){
//            view.setBackgroundResource(R.color.itemBackground_1)
//        }else{
//            view.setBackgroundResource(R.color.itemBackground_2)
//        }

        return view
    }

    override fun getItem(position: Int): Any {
        return historyList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getCount(): Int {
        return historyList.size
    }

    private class Viewholder{
        var historyType:TextView? = null
        var historyDate:TextView? = null
        var historyTime:TextView? = null
    }
}