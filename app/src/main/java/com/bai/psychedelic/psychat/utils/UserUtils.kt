package com.bai.psychedelic.psychat.utils

import java.text.SimpleDateFormat
import java.util.*

class UserUtils {
    companion object{
        fun changeLongTimeToDateTime(time:Long?):String{
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(Date(time!!.toLong()))
        }
    }
}