package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.SplashViewModel
import com.bai.psychedelic.psychat.databinding.ActivitySplashBinding
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.SP_SPLASH_TEXT
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.util.concurrent.ExecutorService




class SplashActivity : AppCompatActivity() {
    companion object{
        @Volatile
        private var skipped:Boolean = false
    }
    private val mEMClient: EMClient by inject()
    private val mSingleExecutor: ExecutorService by inject()
    private lateinit var mContext: Context
    private val TAG = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        StatusBarUtil.setStatusTextColor(true, this)
        mContext = this
        skipped = false
        val binding: ActivitySplashBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_splash)
        val viewModel: SplashViewModel by viewModel()
        binding.model = viewModel
        val sp = getSharedPreferences(SP_SPLASH_TEXT, Context.MODE_PRIVATE)
        val splashText = sp.getString(SP_SPLASH_TEXT, "聊个鸡巴")
        viewModel.setSplashText(splashText)
        val splashTextObserver = Observer<String> { newValue ->
            binding.splashActivityText.text = newValue
        }
        viewModel.splashText.observe(this, splashTextObserver)
        if (mEMClient.isLoggedInBefore){
            mEMClient.chatManager().loadAllConversations()
            mEMClient.groupManager().loadAllGroups()
        }
    }

    override fun onStart() {
        super.onStart()
        mSingleExecutor.submit {
            try {
                Thread.sleep(3000)
                if (!skipped){
                    jumpToNextActivity()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        skipped = true
    }

    override fun onBackPressed() {
        //Splash还想退？
    }

    private fun jumpToNextActivity() {
        if (mEMClient.isLoggedInBefore) {
            MainActivity.actionStart(mContext)
            finish()
        }else{
            LoginActivity.actionStart(mContext)
            finish()
        }
    }

    fun skipClick(view: View) {
        skipped = true
        jumpToNextActivity()
    }

}
