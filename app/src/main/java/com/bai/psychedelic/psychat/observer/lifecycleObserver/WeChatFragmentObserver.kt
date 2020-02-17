package com.bai.psychedelic.psychat.observer.lifecycleObserver

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bai.psychedelic.psychat.ui.fragment.WeChatFragment
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import org.koin.core.KoinComponent
import org.koin.core.inject

class WeChatFragmentObserver constructor(context: WeChatFragment) : LifecycleObserver,
    KoinComponent {
    private val TAG = "WeChatFragmentObserver"
    private val mEMClient: EMClient by inject()
    private lateinit var mMsgListener: EMMessageListener
    private val mContext = context
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun addMsgListener(){
        mEMClient.chatManager().addMessageListener(mMsgListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeMsgListener(){
        mEMClient.chatManager().removeMessageListener(mMsgListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initMsgListener(){
        mMsgListener = object : EMMessageListener {
            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageChanged(message: EMMessage?, change: Any?) {

            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                MyLog.d(TAG,"onMessageReceived()")
                mContext.activity?.runOnUiThread {
                    mContext.refreshConversationList()
                }
            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {

            }
        }
    }
}