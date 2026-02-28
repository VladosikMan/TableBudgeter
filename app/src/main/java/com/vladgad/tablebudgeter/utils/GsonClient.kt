package com.vladgad.tablebudgeter.utils

import com.google.gson.Gson

class GsonClient {
    companion object {
        val INSTANCE_GSON: Gson by lazy { Gson() }
    }
}
