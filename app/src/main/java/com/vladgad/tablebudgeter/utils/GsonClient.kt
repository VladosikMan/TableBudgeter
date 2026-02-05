package com.vladgad.tablebudgeter.utils

import com.google.gson.Gson

class GsonClient {

    companion object{
        @Volatile

        private var INSTANCE: Gson?=null

        fun getInstanceGson():Gson{
            return  INSTANCE?: synchronized(this){
                val client = Gson()
                INSTANCE = client
                client
            }
        }
    }
}