package com.junseok.bluebox.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.junseok.bluebox.Listener.ItemDialogListener
import com.junseok.bluebox.R
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener


class ItemDialogFragment(context: Context) : DialogFragment(){
    //컴포넌트
    lateinit var editName:EditText
    lateinit var editNum:EditText
    lateinit var addBtn:Button
    lateinit var cancleBtn:Button
    lateinit var selectCompany:Spinner
    lateinit var customDialogListener: ItemDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var _view:View = activity!!.layoutInflater.inflate(R.layout.delivery_list_dialog,null)
        initView(_view)
        var alert = AlertDialog.Builder(activity)
        alert.setView(_view)

        val company_arr = resources.getStringArray(R.array.company_arr)
        val company_adapter = ArrayAdapter(this.activity!!.applicationContext,R.layout.delivery_spinner_item,company_arr)
        selectCompany.adapter = company_adapter


        this.addBtn!!.setOnClickListener {
            var _name:String = this.editName.text.toString()
            var _num:String = this.editNum.text.toString()
            var _company:String = this.selectCompany.selectedItem.toString()


            Log.d("msg","name is ${_name} and num is ${_num} and company is ${_company}")

            var intent:Intent = Intent()
            intent.putExtra("name", _name)
            intent.putExtra("number",_num)
            intent.putExtra("company",_company)
            targetFragment!!.onActivityResult(110, 326,intent)
            dismiss()
        }
        this.cancleBtn!!.setOnClickListener {
            dismiss()
        }
        return alert.create()
    }

    private fun initView(view:View){
        editName = view.find(R.id.editName)
        editNum = view.find(R.id.editNum)
        addBtn = view.find(R.id.addBtn)
        cancleBtn = view.find(R.id.cancleBtn)
        selectCompany = view.find(R.id.selectCompany)
    }

}