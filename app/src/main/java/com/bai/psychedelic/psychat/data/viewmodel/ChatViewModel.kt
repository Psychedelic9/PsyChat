package com.bai.psychedelic.psychat.data.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.bai.psychedelic.psychat.utils.*
import com.hyphenate.EMCallBack
import com.hyphenate.chat.*
import com.bai.psychedelic.psychat.ui.activity.ChatActivity
import java.io.File


class ChatViewModel : ViewModel(), KoinComponent {
    private val TAG = "ChatViewModel"
    private var mList = ArrayList<ChatItemEntity>()
    private var mConversationUserId: String = ""
    var mNickName: String = ""
    private val mEMClient: EMClient by inject()
    private var pagesize = 20
    private lateinit var mConversation: EMConversation
    private var mKeyBoardHeight: Int = 900

    fun setKeyBoardHeight(height: Int) {
        mKeyBoardHeight = height
    }

    fun getKeyBoardHeight(): String {
        return mKeyBoardHeight.toString() + "dp"
    }

    fun getMoreLocalList(): ArrayList<ChatItemEntity> {

        var messages: List<EMMessage>? = null
        messages = mConversation.loadMoreMsgFromDB(
            if (mConversation.allMessages.size == 0) "" else mConversation.allMessages[0].msgId,
            pagesize
        )
        return changeEMMessages2ChatItemEntities(messages)
    }

    fun getLatestList(): ArrayList<ChatItemEntity> {
        var messages = mConversation.allMessages
        mConversation.markAllMessagesAsRead()
        return changeEMMessages2ChatItemEntities(messages)
    }


    private fun changeEMMessages2ChatItemEntities(elist: List<EMMessage>): ArrayList<ChatItemEntity> {
        val chatList = ArrayList<ChatItemEntity>()
        var lastMsgTime = 0L
        elist.forEach {
            if (lastMsgTime == 0L) {
                lastMsgTime = it.msgTime
            } else {
                if (it.msgTime - lastMsgTime > 5 * 60 * 1000) {
                    lastMsgTime = it.msgTime
                    chatList.add(ChatItemEntity().apply {
                        type = CHAT_TYPE_MSG_TIME
                        sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                    })
                }
            }
            val chatItemEntity = ChatItemEntity()
            when (it?.type) {
                EMMessage.Type.TXT -> {
                    chatItemEntity.apply {
                        type = if (it.from == mEMClient.currentUser) {
                            CHAT_TYPE_SEND_TXT
                        } else {
                            CHAT_TYPE_GET_TXT
                        }
                        name = it.from
                        content = (it.body as EMTextMessageBody).message
                        sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                    }
                    chatList.add(chatItemEntity)
                    return@forEach
                }
                EMMessage.Type.IMAGE -> {
                    chatItemEntity.apply {
                        if (it.from == mEMClient.currentUser) {
                            type = CHAT_TYPE_SEND_IMAGE
                            content = (it.body as EMImageMessageBody).localUrl
                        } else {
                            type = CHAT_TYPE_GET_IMAGE
                            content = (it.body as EMImageMessageBody).thumbnailUrl
                        }
                        width = (it.body as EMImageMessageBody).width
                        height = (it.body as EMImageMessageBody).height
                        name = it.from
                        sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                    }
                    chatList.add(chatItemEntity)
                    MyLog.d(
                        TAG,
                        "add ImageMessage url = ${chatItemEntity.content} width = ${chatItemEntity.width} height = ${chatItemEntity.height}"
                    )
                    return@forEach
                }
                EMMessage.Type.VIDEO -> {
                    chatItemEntity.apply {
                        if (it.from == mEMClient.currentUser){
                            type = CHAT_TYPE_SEND_VIDEO
                            content = (it.body as EMVideoMessageBody).localUrl
                        }else{
                            type = CHAT_TYPE_GET_VIDEO
                            content = (it.body as EMVideoMessageBody).remoteUrl
                        }
                        width = (it.body as EMVideoMessageBody).thumbnailWidth
                        height = (it.body as EMVideoMessageBody).thumbnailHeight
                        name = it.from
                        sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                        length = (it.body as EMVideoMessageBody).duration
                        message = it
                    }
                    MyLog.d(TAG,"width = ${chatItemEntity.width} height = ${chatItemEntity.height}")
                    chatList.add(chatItemEntity)
                    return@forEach
                }
                EMMessage.Type.LOCATION -> {
                }
                EMMessage.Type.VOICE -> {
                    chatItemEntity.apply {
                        if (it.from == mEMClient.currentUser) {
                            type = CHAT_TYPE_SEND_VOICE
                            content = (it.body as EMVoiceMessageBody).localUrl
                        } else {
                            type = CHAT_TYPE_GET_VOICE
                            content = (it.body as EMVoiceMessageBody).remoteUrl
                        }
                        name = it.from
                        length = (it.body as EMVoiceMessageBody).length
                        sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                        message = it
                    }
                    chatList.add(chatItemEntity)
                    MyLog.d(
                        TAG,
                        "add VoiceMessage url = ${chatItemEntity.content} length = ${chatItemEntity.length}"
                    )
                    return@forEach
                }
                EMMessage.Type.FILE -> {
                }
                EMMessage.Type.CMD -> {
                }

            }
        }
        return chatList
    }

    fun getChatList(): ArrayList<ChatItemEntity> {
        var messages = mConversation.allMessages
        val msgCount = messages?.size ?: 0
        if (msgCount < mConversation.allMsgCount && msgCount < pagesize) {
            var msgId: String? = null
            if (messages != null && messages.size > 0) {
                msgId = messages[0].msgId
            }
            val messageDB = mConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount)
            messages = messageDB + messages
        }
        mList.clear()
        mList = changeEMMessages2ChatItemEntities(messages)
        return mList
    }

    fun setConversationNickName(name: String) {
        mNickName = name
    }

    fun sendVideoMessage(
        videoPath: String,
        imagePath: String,
        length: Int,
        callback: ChatActivity.SendMediaCallback
    ) {
        val message = EMMessage.createVideoSendMessage(videoPath,imagePath,length,mConversationUserId)
        message.setMessageStatusCallback(object:EMCallBack{
            override fun onSuccess() {
                callback.onSuccess()
            }

            override fun onProgress(progress: Int, status: String?) {
                MyLog.d(TAG,"视频消息发送中...")
            }

            override fun onError(code: Int, error: String?) {
                callback.onFailed(code,error.toString())
            }
        })
        if (mConversation.type == EMConversation.EMConversationType.GroupChat) {
            message.chatType = EMMessage.ChatType.GroupChat
        }
        mEMClient.chatManager().sendMessage(message)
    }


    fun sendImageMessage(path: String, callback: ChatActivity.SendMediaCallback) {

        val message = EMMessage.createImageSendMessage(path, false, mConversationUserId)

        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                callback.onSuccess()
                MyLog.d(TAG, "sendImageMessage onSuccess")
            }

            override fun onProgress(progress: Int, status: String?) {
                MyLog.d(TAG, "sendImageMessage onProgress")

            }

            override fun onError(code: Int, error: String?) {
                MyLog.d(TAG, "sendImageMessage onError code = $code error = $error")
                callback.onFailed(code,error.toString())
            }
        })

        val localUrl = (message.body as EMImageMessageBody).localUrl
        if (mConversation.type == EMConversation.EMConversationType.GroupChat) {
            message.chatType = EMMessage.ChatType.GroupChat
        }

        mEMClient.chatManager().sendMessage(message)
    }

    fun sendTextMessage(content: String) {
        val message = EMMessage.createTxtSendMessage(content, mConversationUserId)
        if (mConversation.type == EMConversation.EMConversationType.GroupChat) {
            message.chatType = EMMessage.ChatType.GroupChat
        }
        mEMClient.chatManager().sendMessage(message)
    }

    fun sendVoiceMessage(voiceFilePath: String, voiceTimeLength: Int) {
        val message =
            EMMessage.createVoiceSendMessage(voiceFilePath, voiceTimeLength, mConversationUserId)
        if (mConversation.type == EMConversation.EMConversationType.GroupChat) {
            message.chatType = EMMessage.ChatType.GroupChat
        }
        mEMClient.chatManager().sendMessage(message)
    }

    fun setConversationUserId(id: String) {
        this.mConversationUserId = id
        mConversation = mEMClient.chatManager().getConversation(mConversationUserId)
    }

    fun getConversationUserId():String{
        return mConversationUserId
    }

    fun getConversation(): EMConversation {
        return mConversation
    }


}