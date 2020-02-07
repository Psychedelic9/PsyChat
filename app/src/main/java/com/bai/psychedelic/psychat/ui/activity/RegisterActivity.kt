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
import com.bai.psychedelic.psychat.databinding.ActivityRegisterBinding
import com.bai.psychedelic.psychat.utils.PhoneUtils
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutorService

class RegisterActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mContext: Context
    private val executor: ExecutorService by inject()
    private val emClient:EMClient by inject()

    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        window.statusBarColor = Color.TRANSPARENT
        StatusBarUtil.setStatusTextColor(true, this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
    }

    fun onRegisterClick(view: View) {
        if (!PhoneUtils.isValidChinesePhone(getRegisterPhone())) {
            showMessage("手机号不合法！")
        } else if (getRegisterConfirmPassword() != getRegisterPassword()) {
            showMessage("两次密码输入不相同！请检查")
        } else {
//                repository.register(mView?.getRegisterPhone(),mView?.getRegisterPassword(),{
//                    if (it.success=="true"){
//                        SharedPrefUtil.put(SP_IS_LOGIN, true)
//                        SharedPrefUtil.put(SP_USER,mView?.getRegisterPhone().toString())
//                        mView?.jumpToMainActivity()
//                    }
//                    MyLog.d("555","MSG = "+it.message)
//                    mView?.showMessage(it.message)
//                },{
//                    mView?.showMessage("注册失败，请重新注册")
//                    it.printStackTrace()
//                })
            executor.submit{

                try {
                    EMClient.getInstance()
                        .createAccount(getRegisterPhone(), getRegisterPassword())
                    showMessage("注册成功！")
                    finish()

                } catch (e: HyphenateException) {
                    showMessage("注册失败，请重新注册")
                    e.printStackTrace()
                }
            }
        }

    }
    private fun jumpToMainActivity() {
        MainActivity.actionStart(mContext)
    }
    private fun getRegisterPassword(): String? {
        return mBinding.registerActivityEtRegisterPassword.text.toString()
    }

    private fun getRegisterConfirmPassword(): String? {
        return mBinding.registerActivityEtRegisterConfirmPassword.text.toString()
    }

    private fun getRegisterPhone(): String? {
        return mBinding.registerActivityEtRegisterPhone.text.toString()
    }

    private fun showMessage(str: String) {
        runOnUiThread {
            Toast.makeText(mContext, str, Toast.LENGTH_LONG).show()
        }
    }
}
