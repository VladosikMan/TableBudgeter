package com.vladgad.tablebudgeter.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object{
        fun String.encodeURLParameter(): String {
            return this
                .replace(" ", "%20")
                .replace("!", "%21")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("&", "%26")
                .replace("'", "%27")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("*", "%2A")
                .replace("+", "%2B")
                .replace(",", "%2C")
                .replace("/", "%2F")
                .replace(":", "%3A")
                .replace(";", "%3B")
                .replace("=", "%3D")
                .replace("?", "%3F")
                .replace("@", "%40")
                .replace("[", "%5B")
                .replace("]", "%5D")
        }
        fun formatDate(timestamp: Long): String {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return formatter.format(date)
        }
    }
}