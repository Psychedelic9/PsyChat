package com.bai.psychedelic.psychat.application

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import android.util.Log
import com.bai.psychedelic.psychat.database.MyMigration
import com.bai.psychedelic.psychat.koin.*
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.FileNotFoundException
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.bai.psychedelic.psychat.receiver.CallReceiver
import com.bai.psychedelic.psychat.utils.AppManager
import com.hyphenate.push.EMPushConfig
import com.hyphenate.push.EMPushHelper
import com.hyphenate.push.EMPushType


class MyApplication : Application() {
    companion object{
        private val TAG = "MyApplication"
    }

    private var mCallReceiver:CallReceiver ?= null
    private lateinit var mContext: Context

    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        MyLog.d(TAG, "onCreate")
        AppManager.register(this)
        Realm.init(applicationContext)

        val realmConfig = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(2)
            .migration(MyMigration())
            .build()
        Realm.setDefaultConfiguration(realmConfig)
        try {
            Realm.migrateRealm(realmConfig)
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(viewModelModule, netModule, repositoryModule,appModule)
        }

        val options = EMOptions()
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.acceptInvitationAlways = false
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)

        val pid = Process.myPid()
        val processAppName = getAppName(pid)
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equals(
                applicationContext.packageName,
                ignoreCase = true
            )
        ) {
            Log.e(TAG, "enter the service process!")
            // 则此application::onCreate 是被service 调用的，直接返回
            return
        }

        //开启第三方推送
        val builder = EMPushConfig.Builder(mContext)
//        builder.enableVivoPush() // 推送证书相关信息配置在AndroidManifest.xml中
//            .enableMeiZuPush(String appId, String appKey)
//            .enableMiPush(String appId, String appKey)
//            .enableOppoPush(String appKey, String appSecret)
            .enableHWPush() //开发者需要调用该方法来开启华为推送
//            .enableFCM(String senderId); //开发者需要调用该方法来开启FCM推送
        options.pushConfig = builder.build()

        //初始化
        EMClient.getInstance().init(applicationContext, options)
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true)
        registerReceiver()


    }


    private fun registerReceiver() {
        val callFilter =
            IntentFilter(EMClient.getInstance().callManager().incomingCallBroadcastAction)
        if (mCallReceiver == null){
            mCallReceiver = CallReceiver()
        }
        registerReceiver(mCallReceiver, callFilter)
    }

    private fun getAppName(pID: Int): String? {
        var processName: String? = null
        val am: ActivityManager =
            this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.runningAppProcesses
        val i = l.iterator()
        val pm: PackageManager = this.packageManager
        while (i.hasNext()) {
            val info: ActivityManager.RunningAppProcessInfo =
                i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid === pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) { // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName
    }
}