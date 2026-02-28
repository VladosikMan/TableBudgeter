package com.vladgad.tablebudgeter.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun getDateOperationStr(time : Long) : String{
        val date = Date(time)
        val format = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
        return format.format(date)
    }
}