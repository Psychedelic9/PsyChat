package com.bai.psychedelic.psychat.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bai.psychedelic.psychat.R
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import com.hyphenate.exceptions.EMServiceNotReadyException
import com.hyphenate.exceptions.EMNoActiveCallException
import com.hyphenate.chat.EMCallStateChangeListener.CallError
import com.hyphenate.chat.EMCallStateChangeListener.CallState













class VoiceCallActivity : AppCompatActivity() {
    private val mEMClient:EMClient by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)
        val userId = intent.getStringExtra("username")
        val isComingCall = intent.getBooleanExtra("isComingCall",true)
        val textView = findViewById<TextView>(R.id.voice_call_text)
        if (isComingCall){
            textView.text = "接听来自 $userId 的电话"
        }else{
            makeVoiceCall(userId)
            textView.text = "打电话给 $userId "
        }

        setListener()

    }

    private fun setListener(){
        EMClient.getInstance().callManager().addCallStateChangeListener { callState, error ->
            when (callState) {
                CallState.CONNECTING // 正在连接对方
                -> {
                }
                CallState.CONNECTED // 双方已经建立连接
                -> {
                }

                CallState.ACCEPTED // 电话接通成功
                -> {
                }
                CallState.DISCONNECTED // 电话断了
                -> if (error == CallError.ERROR_UNAVAILABLE) {
                    // 对方不在线
                }
                CallState.NETWORK_UNSTABLE //网络不稳定
                -> if (error == CallError.ERROR_NO_DATA) {
                    //无通话数据
                } else {
                }
                CallState.NETWORK_NORMAL //网络恢复正常
                -> {
                }
                CallState.NETWORK_DISCONNECTED //通话中对方断网会执行
                -> {
                }
                else -> {
                }
            }
        }
    }

    private fun makeVoiceCall(username: String?) {
        try {//单参数
            mEMClient.callManager().makeVoiceCall(username)
        } catch (e: EMServiceNotReadyException) {
            e.printStackTrace()
        }

    }

    fun answerCall(view: View) {
        try {
            mEMClient.callManager().answerCall()
        } catch (e: EMNoActiveCallException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }


    fun rejectCall(view: View) {
        try {
            mEMClient.callManager().rejectCall()
        } catch (e: EMNoActiveCallException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    fun endCall(view: View) {
        mEMClient.callManager().endCall()
    }
}
