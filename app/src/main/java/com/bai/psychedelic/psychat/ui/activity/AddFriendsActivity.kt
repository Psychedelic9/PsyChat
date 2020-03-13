package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.AddFriendsViewModel
import com.bai.psychedelic.psychat.databinding.ActivityAddFriendsBinding
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import com.hyphenate.EMContactListener

import android.widget.Toast


class AddFriendsActivity : AppCompatActivity() {
    private val mEMClient:EMClient by inject()
    private val mViewModel:AddFriendsViewModel by viewModel()
    private lateinit var mBinding:ActivityAddFriendsBinding
    private lateinit var mContext:Context
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, AddFriendsActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_friends)
        mBinding.model = mViewModel
        setListener()
        setAddFriendListener()
    }

    private fun setListener(){
        mBinding.activityAddFriendEtSearch.addTextChangedListener {
            if (it.toString().trim() == ""){
                mBinding.activityAddFriendClConfirmSearch.visibility = View.INVISIBLE
                mBinding.activityAddFriendTvSearchContent.text = ""

            }else{
                mBinding.activityAddFriendClConfirmSearch.visibility = View.VISIBLE
                mBinding.activityAddFriendTvSearchContent.text = it.toString()
            }
        }

    }

    fun onSearchCancelClick(view: View) {
        onBackPressed()
    }

    private fun setAddFriendListener(){
        mEMClient.contactManager().setContactListener(object : EMContactListener {
            override fun onFriendRequestAccepted(username: String?) {
               Toast.makeText(mContext,"添加好友成功",Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onFriendRequestDeclined(username: String?) {
                Toast.makeText(mContext,"已被对方拒绝",Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onContactInvited(username: String, reason: String) {
                Toast.makeText(mContext,"收到好友邀请",Toast.LENGTH_LONG).show()
            }

            override fun onContactDeleted(username: String) {
                //被删除时回调此方法
            }


            override fun onContactAdded(username: String) {
                Toast.makeText(mContext,"联系人已添加",Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    fun searchConfirmClick(view: View) {
        val friendId = mBinding.activityAddFriendEtSearch.text.toString()
        mEMClient.contactManager().addContact(friendId,"123")
        val message = EMMessage.createTxtSendMessage("我已添加你为好友", friendId)
        mEMClient.chatManager().sendMessage(message)
    }

}
