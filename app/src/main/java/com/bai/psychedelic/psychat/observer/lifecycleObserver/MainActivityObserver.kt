package com.bai.psychedelic.psychat.observer.lifecycleObserver

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bai.psychedelic.psychat.ui.activity.MainActivity
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.util.NetUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainActivityObserver constructor(context: AppCompatActivity) : LifecycleObserver,KoinComponent {
    val TAG = "MainActivityObserverconstructor"
    private val mContext: Context = context
    private lateinit var mConnectionListener: MyConnectionListener
    private val mEMClient:EMClient by inject()


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setConnectionListener(){
        MyLog.d(TAG,"setConnectionListener()")
        mConnectionListener = MyConnectionListener(mContext)
        mEMClient.addConnectionListener(mConnectionListener)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeConnectionListener(){
        MyLog.d(TAG,"removeConnectionListener()")
        mEMClient.removeConnectionListener(mConnectionListener)
    }

    inner class MyConnectionListener(context: Context) : EMConnectionListener {
        val context = context
        override fun onConnected() {

        }

        override fun onDisconnected(errorCode: Int) {
            var str = ""

            str = when (errorCode) {
                EMError.USER_REMOVED -> "账号已被移除"
                EMError.USER_LOGIN_ANOTHER_DEVICE -> "账号已在其他设备登录"
                else -> {
                    if (NetUtils.hasNetwork(context)) {
                        "连接不到聊天服务器"
                    } else {
                        "当前网络不可用，请检查网络设置"
                    }
                }
            }
//                mEMClient.logout(true)
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }

    }

}