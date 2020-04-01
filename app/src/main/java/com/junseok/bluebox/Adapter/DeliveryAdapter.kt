package com.junseok.bluebox.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.junseok.bluebox.Fragment.DeliveryFragment
import com.junseok.bluebox.WebViewActivity
import com.junseok.bluebox.SQLite.Object.Delivery
import com.junseok.bluebox.R
import com.junseok.bluebox.SQLite.DBHelper.database
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.textColorResource

class DeliveryAdapter(val context: Context, val itemList: ArrayList<Delivery>) : BaseAdapter(){
    private var inflater : LayoutInflater
    init {
        this.inflater = LayoutInflater.from(context)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View
        val hold: Viewholder
        if(convertView == null){
            view = this.inflater.inflate(R.layout.delivery_list_item,null)
            hold = Viewholder()

            hold.itemName = view.find<TextView>(R.id.itemName)
            hold.itemNum = view.find<TextView>(R.id.itemNum)
            hold.companyName = view.find<ImageView>(R.id.companyName)
            hold.itemDate = view.find<TextView>(R.id.itemDate)
            hold.searchBtn = view.find<ImageButton>(R.id.searchBtn)
            hold.removeBtn = view.find<ImageButton>(R.id.removeBtn)

            view.tag = hold
        }else{
            hold = convertView.tag as Viewholder
            view = convertView
        }

        hold.itemName!!.setTextColor(Color.WHITE)
        hold.itemNum!!.textColorResource = R.color.textHalfColor
        hold.itemDate!!.textColorResource = R.color.textHalfColor
        parent!!.setBackgroundResource(R.color.listBackground)

        val item = itemList[position]
        hold.itemName?.text = item._itemName
        hold.itemNum?.text = item._itemNum.toString()
        hold.itemDate?.text = item._itemDate

        var company = item._company
        var src:Int
        when(company){
            "CJ대한통운" -> src = R.drawable.cjlogo
            "한진택배" -> src=R.drawable.hanjinlogo
            "우체국택배" -> src=R.drawable.epostlogo
            "로젠택배" -> src=R.drawable.logenlogo
            else -> src=R.drawable.warn_on
        }
        hold.companyName?.setImageResource(src)

        hold.searchBtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent:Intent = Intent(v?.context,WebViewActivity::class.java)
                intent.putExtra("company",item._company)
                intent.putExtra("number",item._itemNum)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        })

        hold.removeBtn?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                itemList.removeAt(position)
                notifyDataSetChanged()
                var df = DeliveryFragment()
                df.database.use {
                    delete(
                        Delivery.table,
                        "DeliveryNumber={number}",
                        "number" to item._itemNum.toString()
                    )
                }
            }
        })

        if (position%2!=1){
            view.setBackgroundResource(R.drawable.delivery_list_item1)
        }else{
            view.setBackgroundResource(R.drawable.delivery_list_item2)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getCount(): Int {
        return itemList.size
    }

    private class Viewholder{
        var itemName:TextView? = null
        var itemNum:TextView? = null
        var companyName:ImageView? = null
        var itemDate:TextView? = null
        var searchBtn: ImageButton? = null
        var removeBtn: ImageButton? = null
    }
}

