package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bai.psychedelic.psychat.BR
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.ChatViewModel
import com.bai.psychedelic.psychat.databinding.ActivityChatBinding
import com.bai.psychedelic.psychat.listener.SoftKeyBoardListener
import com.bai.psychedelic.psychat.ui.adapter.ChatListRvAdapter
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import org.koin.android.viewmodel.ext.android.viewModel

class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"

    private val mViewModel:ChatViewModel by viewModel()
    private lateinit var mBinding:ActivityChatBinding
    private lateinit var mAdapter:ChatListRvAdapter
    private var mList = ArrayList<ChatItemEntity>()
    private lateinit var mContext:Context

    companion object {
        fun actionStart(context: Context) {
            MyLog.d("ChatActivity","actionStart()")
            //TODO:传递参数
            context.startActivity(Intent(context, ChatActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        mBinding.model = mViewModel
        mBinding.chatActivityRv.layoutManager = LinearLayoutManager(mContext)
        mList = mViewModel.getChatList()
        mAdapter = ChatListRvAdapter(mContext,mList, BR.model)
        mBinding.chatActivityRv.adapter = mAdapter

        setStatusBar()
        setListener()
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)

    }
    private fun setListener(){
        mBinding.chatActivityEt.addTextChangedListener {
                MyLog.d(TAG, "text = $it")
                if (it.toString().trim() == ""){
                    mBinding.chatActivityIvButtonSend.visibility = View.GONE
                    mBinding.chatActivityIvButtonMore.visibility = View.VISIBLE
                }else{
                    mBinding.chatActivityIvButtonMore.visibility = View.GONE
                    mBinding.chatActivityIvButtonSend.visibility = View.VISIBLE
                }
        }
        mBinding.chatActivityRv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            MyLog.d(TAG,"onLayoutChanged")
            if (bottom < oldBottom) {
                mBinding.chatActivityRv.post {
                    if (mBinding.chatActivityRv.adapter!!.itemCount > 0) {
                        mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount-1)
                    }
                }
            }
        }
        SoftKeyBoardListener.setListener(this,object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardShow(height: Int) {
                MyLog.d(TAG,"键盘显示 高度:$height")

            }

            override fun keyBoardHide(height: Int) {
                MyLog.d(TAG,"键盘隐藏 高度:$height")
            }

        })
    }

    private fun setStatusBar(){
        val resources = resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        val statusBarFill = mBinding.chatActivityStatusBarFill
        val layoutParams = statusBarFill.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = height
        statusBarFill.layoutParams = layoutParams
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true,this)
    }

    fun sendMessageButtonClick(view: View) {
        val entity = ChatItemEntity().apply {
            id = 12346
            name = "祁门路搅屎王"
            content = mBinding.chatActivityEt.text.toString()
            type = CHAT_TYPE_SEND_TXT
        }
        mAdapter.addData(entity)
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)
        mBinding.chatActivityEt.setText("")
    }
}
