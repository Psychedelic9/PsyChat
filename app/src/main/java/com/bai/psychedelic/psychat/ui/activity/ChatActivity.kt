package com.bai.psychedelic.psychat.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import android.content.ContentUris
import android.provider.DocumentsContract
import android.view.Gravity
import android.widget.Toast
import com.bai.psychedelic.psychat.R
import java.io.File


class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    private lateinit var entity: WechatRvListItemEntity
    private val mViewModel: ChatViewModel by viewModel()
    private lateinit var mBinding: ActivityChatBinding
    private lateinit var mChatListAdapter: ChatListRvAdapter
    private lateinit var mChatMoreAdapter: ChatMoreListRvAdapter
    private var mList = ArrayList<ChatItemEntity>()
    private var mMoreList = ArrayList<ChatMoreEntity>()
    private lateinit var mContext: Context
    private val mEMClient: EMClient by inject()
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
        mBinding.chatActivityRv.layoutManager = LinearLayoutManager(mContext)
        mBinding.chatActivityRvMore.layoutManager = GridLayoutManager(mContext, 4)
        mList = mViewModel.getChatList()
        mMoreList = getMoreList()
        mChatListAdapter = ChatListRvAdapter(mContext, mList, BR.item)
        mChatMoreAdapter = ChatMoreListRvAdapter(this, mMoreList)
        mBinding.chatActivityRv.adapter = mChatListAdapter
        mBinding.chatActivityRvMore.adapter = mChatMoreAdapter

        mBinding.chatActivityEt.requestFocus()

        mLifecycleObserver = ChatActivityObserver(this)
        lifecycle.addObserver(mLifecycleObserver)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setStatusBar()
        setListener()
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
                        mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount - 1)
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
    }

    private fun scrollToEnd() {
        if (mBinding.chatActivityRv.adapter!!.itemCount > 0) {
            mBinding.chatActivityRv.smoothScrollToPosition(mBinding.chatActivityRv.adapter!!.itemCount - 1)
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
        mBinding.chatActivityRv.smoothScrollToPosition(mList.size)
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

    private fun getImagePath(uri: Uri, selection: String): String {
        var path: String = ""
        //通过uri和selection来获取真实的图片路径
        val cursor: Cursor? = if ("" == selection) {
            contentResolver.query(uri, null, null, null, null)!!
        } else {
            contentResolver.query(uri, null, selection, null, null)!!
        }
        if (cursor!!.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        }
        cursor.close()
        return path
    }

    protected fun sendPicByUri(selectedImage: Uri) {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver
            .query(selectedImage, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor!!.getString(columnIndex)
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
            mViewModel.sendImageMessage(picturePath.toString())
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
            mViewModel.sendImageMessage(file.absolutePath.toString())
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
//            if (DocumentsContract.isDocumentUri(this, uri)) {
//                //如果是document类型的uri，则通过document id处理
//                val docId = DocumentsContract.getDocumentId(uri)
//                if ("com.android.providers.media.documents" == uri?.authority) {
//                    val id =
//                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
//                    val selection = MediaStore.Images.Media._ID + "=" + id
//                    imagePath =
//                        getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
//                } else if ("com.android.providers.downloads.documents" == uri?.authority) {
//                    val contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content:" + "//downloads/public_downloads"),
//                        java.lang.Long.valueOf(docId)
//                    )
//                    imagePath = getImagePath(contentUri, "")
//                }
//            } else if ("content".equals(uri?.scheme!!, ignoreCase = true)) {
//                //如果是content类型的uri，则使用普通方式处理
//                imagePath = getImagePath(uri, "")
//            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
//                //如果是File类型的uri，直接获取图片路径即可
//                imagePath = uri.path
//            }


        }
        super.onActivityResult(requestCode, resultCode, data)

    }

}
