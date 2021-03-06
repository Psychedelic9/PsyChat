package com.bai.psychedelic.psychat.observer.lifecycleObserver

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bai.psychedelic.psychat.ui.activity.ChatActivity
import com.bai.psychedelic.psychat.ui.custom.ChatVoicePlayer
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import org.koin.core.KoinComponent
import org.koin.core.inject

class ChatActivityObserver constructor(activity:ChatActivity):
    LifecycleObserver, KoinComponent {
    private val TAG = "ChatActivityObserver"
    private lateinit var mMsgListener: EMMessageListener
    private val mEMClient:EMClient by inject()
    private val mActivity:ChatActivity = activity
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initMsgListener(){
        mMsgListener = object :EMMessageListener{
            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageChanged(message: EMMessage?, change: Any?) {

            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                MyLog.d(TAG,"onMessageReceived()")
                mActivity.runOnUiThread {
                    mActivity.refreshChatList()
                }
            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {

            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun addMsgListener(){
        mEMClient.chatManager().addMessageListener(mMsgListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeMsgListener(){
        mEMClient.chatManager().removeMessageListener(mMsgListener)
        //如果在播放语音停止播放
        if (ChatVoicePlayer.get(mActivity).isPlaying){
            ChatVoicePlayer.get(mActivity).stop()
        }
    }

}