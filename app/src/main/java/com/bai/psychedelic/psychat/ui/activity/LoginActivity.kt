package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.LoginViewModel
import com.bai.psychedelic.psychat.databinding.ActivityLoginBinding
import com.bai.psychedelic.psychat.databinding.ActivitySplashBinding
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.PhoneUtils
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mContext: Context

    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    private val mEMClient: EMClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        StatusBarUtil.setStatusTextColor(true, this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mContext = this
        val viewModel: LoginViewModel by viewModel()
        mBinding.model = viewModel
    }

    override fun onBackPressed() {
        //返回后台，不结束App
        moveTaskToBack(true)
    }

    private fun getInputPhone(): String {
        return mBinding.loginActivityEtLoginPhone.text.toString()
    }

    private fun getInputPassword(): String {
        return mBinding.loginActivityEtLoginPassword.text.toString()
    }

    private fun showMessage(str: String) {
        runOnUiThread {
            Toast.makeText(mContext, str, Toast.LENGTH_LONG).show()
        }
    }

    fun loginOnClick(view: View) {

        if (!PhoneUtils.isValidChinesePhone(getInputPhone())) {
            showMessage("手机号不合法！")
        } else {
            thread(true) {
                mEMClient
                    .login(
                        getInputPhone(),
                        getInputPassword(),
                        object : EMCallBack {
                            //回调
                            override fun onSuccess() {
                                mEMClient.groupManager().loadAllGroups()
                                mEMClient.chatManager().loadAllConversations()
                                MyLog.d("main", "登录聊天服务器成功！")
                                showMessage("登录成功！")
                                MainActivity.actionStart(mContext)
                                finish()
                            }

                            override fun onProgress(progress: Int, status: String) {}
                            override fun onError(code: Int, message: String) {
                                MyLog.d("main", "登录聊天服务器失败！")
                                showMessage("登录失败！$code $message")
                            }
                        })
            }

        }

    }

    fun registerClick(view: View) {

    }
}
