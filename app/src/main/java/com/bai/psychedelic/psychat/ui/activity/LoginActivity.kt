package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bai.psychedelic.psychat.R

class LoginActivity : AppCompatActivity() {
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onBackPressed() {
        //返回后台，不结束App
        moveTaskToBack(true)
    }
}
