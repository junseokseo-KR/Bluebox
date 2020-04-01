package com.junseok.bluebox.SQLite.Object

data class History(var _type:String?, var _date:String?, var _time:String?) {
    companion object {
        const val table: String = "HistoryTable"
        const val type: String = "HistoryType"
        const val date: String = "HistoryDate"
        const val time: String = "HistoryTime"
    }
}
