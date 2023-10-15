package com.example.watertrak

import android.app.Application

class WaterTrakApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}