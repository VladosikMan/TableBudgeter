package com.vladgad.tablebudgeter

import android.app.Application

class BudgeterApp : Application() {
    companion object {
        lateinit var instance: BudgeterApp
            private set
    }
}