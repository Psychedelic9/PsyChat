package com.bai.psychedelic.psychat.utils

import android.util.Log
import com.bai.psychedelic.psychat.BuildConfig
import java.lang.Exception

object MyLog {
    private val debug = BuildConfig.DEBUG
    fun d(tag: String, str: String) {
        if (debug) {
            Log.d(tag, str)
        }
    }
    fun e(tag: String, str: String) {
        if (debug){
            Log.e(tag, str)
        }
    }
    fun e(tag: String, str: String,e: Exception) {
        if (debug){
            Log.e(tag, str,e)
        }
    }
}