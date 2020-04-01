package com.junseok.bluebox.SQLite.Object

data class Delivery(var _itemIndex:Long?, var _itemName:String?, var _itemNum:String?, var _company:String?,var _itemDate:String?){
    companion object{
        const val table:String = "DeliveryTable"
        const val index:String = "DeliveryIndex"
        const val name:String = "DeliveryName"
        const val company:String = "DeliveryCompany"
        const val number:String = "DeliveryNumber"
        const val date:String = "DeliveryDate"
    }
}