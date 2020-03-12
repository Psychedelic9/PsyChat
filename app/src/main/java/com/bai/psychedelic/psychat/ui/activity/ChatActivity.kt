package com.bai.psychedelic.psychat.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bai.psychedelic.psychat.BR
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
import androidx.recyclerview.widget.GridLayoutManager
import com.bai.psychedelic.psychat.data.entity.ChatMoreEntity
import com.bai.psychedelic.psychat.ui.adapter.ChatMoreListRvAdapter
import android.provider.MediaStore
import android.view.Gravity
import android.widget.Toast
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.ui.custom.RecordButton
import java.io.File
import java.util.concurrent.ExecutorService


open class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    private lateinit var entity: WechatRvListItemEntity
    private val mViewModel: ChatViewModel by viewModel()
    private val mEMClient: EMClient by inject()
    private val mSingleExecutor: ExecutorService by inject()
    private lateinit var mBinding: ActivityChatBinding
    private lateinit var mChatListAdapter: ChatListRvAdapter
    private lateinit var mChatMoreAdapter: ChatMoreListRvAdapter
    private var mList = ArrayList<ChatItemEntity>()
    private var mMoreList = ArrayList<ChatMoreEntity>()
    private lateinit var mContext: Context
    private lateinit var mLifecycleObserver: ChatActivityObserver
    private lateinit var imm: InputMethodManager
    private var mKeyBoardHeight = 0
    private var isKeyBoardShow: Boolean = false
    private var mStatusBarHeight: Int = 0
    private var isNeedShowMore = false
    private var isNeedSmoothRv = true

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
        val linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.stackFromEnd = true
        mBinding.chatActivityRv.layoutManager = linearLayoutManager
        mBinding.chatActivityRvMore.layoutManager = GridLayoutManager(mContext, 4)
        mList = mViewModel.getChatList()
        mMoreList = getMoreList()
        mChatListAdapter = ChatListRvAdapter(mContext, mList, BR.item)
        mChatMoreAdapter = ChatMoreListRvAdapter(this, mMoreList)
        mBinding.chatActivityRv.adapter = mChatListAdapter
        mBinding.chatActivityRvMore.adapter = mChatMoreAdapter

        //弹出键盘
//        mBinding.chatActivityEt.requestFocus()

        mLifecycleObserver = ChatActivityObserver(this)
        lifecycle.addObserver(mLifecycleObserver)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setStatusBar()
        setListener()
        //只滑动初始化会有动画，只是用LinearLayoutManager的逆序排列不能滑动到最后一条
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)

    }

    private fun getMoreList(): java.util.ArrayList<ChatMoreEntity> {
        val list = ArrayList<ChatMoreEntity>()
        list.add(ChatMoreEntity("相册", R.drawable.icon_photo))
        list.add(ChatMoreEntity("拍摄", R.drawable.icon_camera))
        list.add(ChatMoreEntity("通话", R.drawable.icon_phone))
        list.add(ChatMoreEntity("位置", R.drawable.icon_location))
        list.add(ChatMoreEntity("语音", R.drawable.icon_voice))
        list.add(ChatMoreEntity("收藏", R.drawable.icon_collection))
        return list
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

    @SuppressLint("ClickableViewAccessibility")
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
                        scrollToEnd()
                    }
                }
                MyLog.d(TAG, "height = ${oldBottom - bottom}")

            }
        }
        SoftKeyBoardListener.setListener(this,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    MyLog.d(TAG, "键盘显示 高度:$height")
                    setIsNeedShowMore(false)
                    mBinding.chatActivityClMore.visibility = View.GONE
                    isKeyBoardShow = true
                    val sp = getSharedPreferences(SP_KEYBOARD_HEIGHT, Context.MODE_PRIVATE)
                    sp.edit().putString(SP_KEYBOARD_HEIGHT, height.toString()).apply()
                    mKeyBoardHeight = height
                    mViewModel.setKeyBoardHeight(mKeyBoardHeight)
                    MyLog.d(
                        TAG,
                        "mKeyBoardHeight = $mKeyBoardHeight + mStatusBarHeight = $mStatusBarHeight"
                    )
                    mBinding.chatActivityClMore.minHeight =
                        mKeyBoardHeight + mStatusBarHeight * 2 + 50
                }

                override fun keyBoardHide(height: Int) {
                    MyLog.d(TAG, "键盘隐藏 高度:$height")
                    isKeyBoardShow = false
                    if (getIsNeedShowMore()) {
                        mBinding.chatActivityClMore.visibility = View.VISIBLE
                        setIsNeedShowMore(false)
                    }
                }

            })
        mBinding.chatActivityEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                MyLog.d(TAG, "OnFocusChangeListener lose focus")
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
            mBinding.chatActivityClMore.visibility = View.GONE

            false
        }
        mBinding.chatActivityRecordButton.setVoiceRecorderCallback(object:RecordButton.VoiceRecorderCallback{
            override fun onVoiceRecordComplete(voiceFilePath: String, voiceTimeLength: Int) {
                mViewModel.sendVoiceMessage(voiceFilePath,voiceTimeLength)
                refreshChatList()
            }

        })
        mBinding.chatActivitySwipeRefreshLayout.setColorSchemeResources(R.color.orange,R.color.red,R.color.blue)
        mBinding.chatActivitySwipeRefreshLayout.setOnRefreshListener {
            mBinding.chatActivitySwipeRefreshLayout.isRefreshing = false
            mViewModel.getMoreLocalList()
            mChatListAdapter.refreshList(mViewModel.getLatestList())
        }
    }

    private fun scrollToEnd() {
        if (mBinding.chatActivityRv.adapter!!.itemCount > 0) {
            mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount)
        }
    }

    private fun setStatusBar() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        mStatusBarHeight = resources.getDimensionPixelSize(resourceId)
        MyLog.d(TAG, "mStatusBarHeight = $mStatusBarHeight")
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }

    fun refreshChatList() {
        mList = mViewModel.getChatList()
        MyLog.d(TAG, "refreshChatList() mList.size = ${mList.size}")
        mChatListAdapter.refreshList(mList)
        scrollToEnd()
    }

    fun sendMessageButtonClick(view: View) {
        mViewModel.sendTextMessage(mBinding.chatActivityEt.text.toString())
        refreshChatList()
        mBinding.chatActivityEt.setText("")
    }

    fun chatTitleBackClick(view: View) {
        onBackPressed()
    }

    fun moreButtonClick(view: View) {
        if (mBinding.chatActivityClMore.visibility == View.GONE) {
            if (isKeyBoardShow) {
                MyLog.d(TAG, "isKeyBoardShow")
                setIsNeedShowMore(true)
                imm.hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )
                isKeyBoardShow = false
            } else {
                MyLog.d(TAG, "isKeyBoard NOT Show")
                isKeyBoardShow = true
                mBinding.chatActivityClMore.visibility = View.VISIBLE
            }
        } else {
            mBinding.chatActivityClMore.visibility = View.GONE
            isKeyBoardShow = if (!isKeyBoardShow) {
                MyLog.d(TAG, "isKeyBoard NOT Show")
                mBinding.chatActivityEt.requestFocus()
                imm.showSoftInput(mBinding.chatActivityEt, 0)
                true
            } else {
                MyLog.d(TAG, "isKeyBoardShow")
                false
            }
        }
    }

    fun setIsNeedShowMore(need: Boolean) {
        isNeedShowMore = need
    }

    fun getIsNeedShowMore(): Boolean {
        return isNeedShowMore
    }

    fun setIsNeedSmoothRv(need: Boolean) {
        isNeedSmoothRv = need
    }

    fun getIsNeedSmoothRv(): Boolean {
        return isNeedSmoothRv
    }


    override fun onBackPressed() {
        if (mBinding.chatActivityClMore.visibility == View.VISIBLE) {
            mBinding.chatActivityClMore.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }



    private fun sendPicByUri(selectedImage: Uri) {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver
            .query(selectedImage, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            cursor = null

            if (picturePath == null || picturePath == "null") {
                val toast =
                    Toast.makeText(mContext, R.string.cant_find_pictures, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                return
            }
            MyLog.d(TAG, "start sendImageMessage $picturePath")
            mViewModel.sendImageMessage(picturePath.toString(),object : SendPictureCallback{
                override fun onSuccess() {
                    refreshChatList()
                }

                override fun onFailed() {

                }

            })
            MyLog.d(TAG, "sendImageMessage $picturePath")
        } else {
            val file = File(selectedImage.path!!)
            if (!file.exists()) {
                val toast =
                    Toast.makeText(mContext, R.string.cant_find_pictures, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                return

            }
            mViewModel.sendImageMessage(file.absolutePath.toString(),object:SendPictureCallback{
                override fun onSuccess() {
                    refreshChatList()
                }

                override fun onFailed() {

                }

            })
            MyLog.d(TAG, "sendImageMessage ${file.absolutePath}")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == START_ACTIVITY_IMAGE && resultCode == RESULT_OK && null != data) {
//            var imagePath: String? = null
            val uri = data.data
            if (uri!=null){
                sendPicByUri(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
    @Volatile
    private var abortQuickClick = false
    fun voiceTextSwitch(view: View) {
        //500ms延时操作防止快速点击
        if (!abortQuickClick){
            if (mBinding.chatActivityEt.visibility == View.VISIBLE){
                mBinding.chatActivityEt.visibility = View.GONE
                mBinding.chatActivityRecordButton.visibility = View.VISIBLE
                mBinding.chatActivityIvButtonSwitch.setImageResource(R.drawable.chatting_setmode_keyboard_btn)
                if ( mBinding.chatActivityIvButtonSend.visibility == View.VISIBLE){
                    mBinding.chatActivityIvButtonSend.visibility = View.GONE
                    mBinding.chatActivityIvButtonMore.visibility = View.VISIBLE
                }
            }else{
                mBinding.chatActivityEt.visibility = View.VISIBLE
                mBinding.chatActivityRecordButton.visibility = View.GONE
                mBinding.chatActivityRecordButton.visibility = View.GONE
                mBinding.chatActivityIvButtonSwitch.setImageResource(R.drawable.chatting_setmode_voice_btn)
                if (mBinding.chatActivityEt.text.isNotEmpty()){
                    mBinding.chatActivityIvButtonSend.visibility = View.VISIBLE
                    mBinding.chatActivityIvButtonMore.visibility = View.GONE
                }

            }
            mSingleExecutor.submit {
                abortQuickClick = true
                Thread.sleep(500)
                abortQuickClick = false
            }
        }
    }
    interface SendPictureCallback{
        fun onSuccess()
        fun onFailed()
    }
}
