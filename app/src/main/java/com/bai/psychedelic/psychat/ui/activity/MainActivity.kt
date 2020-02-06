package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bai.psychedelic.psychat.R

class MainActivity : AppCompatActivity() {
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onBackPressed() {
        //返回后台，不结束App
        moveTaskToBack(true)
    }
}
