package com.bai.psychedelic.psychat.observer.lifecycleObserver

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.util.NetUtils
import org.koin.core.KoinComponent
import org.koin.core.inject
import android.Manifest.permission.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bai.psychedelic.psychat.utils.*
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.api.HuaweiApiClient
import com.huawei.hms.common.ApiException
import java.lang.Exception


class MainActivityObserver constructor(context: AppCompatActivity) : LifecycleObserver,KoinComponent {
    val TAG = "MainActivityObserverconstructor"
    private val mContext: AppCompatActivity = context
    private lateinit var mConnectionListener: MyConnectionListener
    private val mEMClient:EMClient by inject()


    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setConnectionListener(){
        MyLog.d(TAG,"setConnectionListener()")
        mConnectionListener = MyConnectionListener(mContext)
        mEMClient.addConnectionListener(mConnectionListener)
        val permissions = arrayOf<String>(
            READ_PHONE_STATE,READ_SMS,READ_PHONE_NUMBERS,ACCESS_FINE_LOCATION,CHANGE_WIFI_STATE
            ,ACCESS_NETWORK_STATE,MODIFY_AUDIO_SETTINGS,WRITE_EXTERNAL_STORAGE,WAKE_LOCK,RECORD_AUDIO
            ,CAMERA,RECEIVE_BOOT_COMPLETED,VIBRATE,MOUNT_UNMOUNT_FILESYSTEMS,READ_EXTERNAL_STORAGE,SYSTEM_ALERT_WINDOW
        ,BROADCAST_STICKY,REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,SYSTEM_ALERT_WINDOW,READ_LOGS
        )
        requestPermission(permissions, REQUEST_PERMISSIONS)
        getToken()
    }
    private fun getToken() {
        object : Thread() {
            override fun run() {
                try {
                    // read from agconnect-services.json
                    val appId = AGConnectServicesConfig.fromContext(mContext)
                        .getString("client/app_id")
                    val token = HmsInstanceId.getInstance(mContext).getToken(appId, "HCM")
                    MyLog.d(TAG, "get token:$token")
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token)
                    }
                    MyLog.d(TAG,"get token:$token")
                } catch (e: ApiException) {
                    e.printStackTrace()
                    MyLog.d(TAG,"get token failed, $e")
                }
            }
        }.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun cleanBadgeNumber(){
        MyLog.d(TAG,"cleanBadgeNumber")
        try {
            val extra = Bundle()
            extra.putString("package", "com.bai.psychedelic.psychat")
            extra.putString("class", "com.bai.psychedelic.psychat.ui.activity.SplashActivity")
            extra.putInt("badgenumber", 0)
            mContext.contentResolver.call(
                Uri.parse("content://com.huawei.android.launcher.settings/badge/")
                , "change_badge", null, extra)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun sendRegTokenToServer(token: String) {
        MyLog.d(TAG, "sending token to server. token:$token")
        mEMClient.sendHMSPushTokenToServer(token)
    }

    private fun lackPermission(permissions: Array<String>):Boolean{
        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(mContext,permission)!=PackageManager.PERMISSION_GRANTED){
                return true
            }
        }
        return false
    }

    private fun requestPermission(permissions: Array<String>,requestId:Int){
        if (lackPermission(permissions)) {
            ActivityCompat.requestPermissions(
                mContext,
                permissions,
                requestId
            )
        }
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