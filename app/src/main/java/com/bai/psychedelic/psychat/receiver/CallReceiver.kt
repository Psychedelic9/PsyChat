package com.bai.psychedelic.psychat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bai.psychedelic.psychat.ui.activity.VideoCallActivity
import com.bai.psychedelic.psychat.ui.activity.VoiceCallActivity
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.chat.EMClient
import org.koin.core.KoinComponent
import org.koin.core.inject

class CallReceiver : BroadcastReceiver(),KoinComponent {
    private val mEMClient:EMClient by inject()
    override fun onReceive(context: Context, intent: Intent) {
        if (!mEMClient.isLoggedInBefore)
            return
        //username
        val from = intent.getStringExtra("from")
        //call type
        val type = intent.getStringExtra("type")
        if ("video" == type) { //video call
            context.startActivity(
                Intent(
                    context,
                    VideoCallActivity::class.java
                ).putExtra("username", from).putExtra(
                    "isComingCall",
                    true
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } else { //voice call
            context.startActivity(
                Intent(
                    context,
                    VoiceCallActivity::class.java
                ).putExtra("username", from).putExtra(
                    "isComingCall",
                    true
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        MyLog.d("CallReceiver", "app received a incoming call")
    }

}