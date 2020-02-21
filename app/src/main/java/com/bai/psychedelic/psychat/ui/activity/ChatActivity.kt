package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bai.psychedelic.psychat.BR
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.data.entity.WechatRvListItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.ChatViewModel
import com.bai.psychedelic.psychat.databinding.ActivityChatBinding
import com.bai.psychedelic.psychat.listener.SoftKeyBoardListener
import com.bai.psychedelic.psychat.observer.lifecycleObserver.ChatActivityObserver
import com.bai.psychedelic.psychat.ui.adapter.ChatListRvAdapter
import com.bai.psychedelic.psychat.utils.*
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import android.view.inputmethod.InputMethodManager


class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    private lateinit var entity: WechatRvListItemEntity
    private val mViewModel: ChatViewModel by viewModel()
    private lateinit var mBinding: ActivityChatBinding
    private lateinit var mAdapter: ChatListRvAdapter
    private var mList = ArrayList<ChatItemEntity>()
    private lateinit var mContext: Context
    private val mEMClient: EMClient by inject()
    private lateinit var mLifecycleObserver: ChatActivityObserver
    private lateinit var imm: InputMethodManager

    companion object {
        fun actionStart(context: Context) {
            MyLog.d("ChatActivity", "actionStart()")
            context.startActivity(Intent(context, ChatActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        val entity = intent.getParcelableExtra<WechatRvListItemEntity>(CONVERSATION_USER_ID)
        if (entity?.nickName != null) {
            mViewModel.setConversationUserId(entity.name)
            mViewModel.setConversationNickName(entity.nickName)
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        mBinding.model = mViewModel
        mBinding.chatActivityRv.layoutManager = LinearLayoutManager(mContext)
        mList = mViewModel.getChatList()
        mAdapter = ChatListRvAdapter(mContext, mList, BR.item)
        mBinding.chatActivityRv.adapter = mAdapter

        mLifecycleObserver = ChatActivityObserver(this)
        lifecycle.addObserver(mLifecycleObserver)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setStatusBar()
        setListener()
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)

    }

    override fun onResume() {
        super.onResume()
        mViewModel.getConversation().markAllMessagesAsRead()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.getConversation().markAllMessagesAsRead()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mLifecycleObserver)
    }

    private fun setListener() {
        mBinding.chatActivityEt.addTextChangedListener {
            MyLog.d(TAG, "text = $it")
            if (it.toString().trim() == "") {
                mBinding.chatActivityIvButtonSend.visibility = View.GONE
                mBinding.chatActivityIvButtonMore.visibility = View.VISIBLE
            } else {
                mBinding.chatActivityIvButtonMore.visibility = View.GONE
                mBinding.chatActivityIvButtonSend.visibility = View.VISIBLE
            }
        }
        mBinding.chatActivityRv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            MyLog.d(TAG, "onLayoutChanged")
            if (bottom < oldBottom) {
                mBinding.chatActivityRv.post {
                    if (mBinding.chatActivityRv.adapter!!.itemCount > 0) {
                        mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount - 1)
                    }
                }
            }
        }
        SoftKeyBoardListener.setListener(this,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    MyLog.d(TAG, "键盘显示 高度:$height")

                }

                override fun keyBoardHide(height: Int) {
                    MyLog.d(TAG, "键盘隐藏 高度:$height")
                }

            })
        mBinding.chatActivityEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                MyLog.d(TAG,"OnFocusChangeListener lose focus")
                imm.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
        mBinding.chatActivityRv.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
            imm.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            false
        }
    }

    private fun scrollToEnd() {
        if (mBinding.chatActivityRv.adapter!!.itemCount > 0) {
            mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount - 1)
        }
    }


    private fun setStatusBar() {
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }

    fun refreshChatList() {
        mList = mViewModel.getChatList()
        MyLog.d(TAG, "refreshChatList() mList.size = ${mList.size}")
        mAdapter.refreshList(mList)
        scrollToEnd()
    }

    fun sendMessageButtonClick(view: View) {
        mViewModel.sendTextMessage(mBinding.chatActivityEt.text.toString())
        refreshChatList()
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)
        mBinding.chatActivityEt.setText("")
    }

    fun chatTitleBackClick(view: View) {
        onBackPressed()
    }


}
