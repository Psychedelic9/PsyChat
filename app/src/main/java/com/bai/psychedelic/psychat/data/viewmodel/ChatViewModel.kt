package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_GET_TXT
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.hyphenate.chat.EMMessage
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.hyphenate.chat.EMImageMessageBody


class ChatViewModel:ViewModel(),KoinComponent {

    private var mList = ArrayList<ChatItemEntity>()
    private var mConversationUserId:String = ""
    var mNickName:String = ""
    private val mEMClient:EMClient by inject()
    private lateinit var mConversation: EMConversation
    fun refreshChatList():ArrayList<ChatItemEntity>{
        val messages = mConversation.allMessages
        messages.forEach {
            val chatItemEntity = ChatItemEntity().apply {
                //TODO:根据消息类型创建对应Entity
            }
        }
        mList.add(ChatItemEntity().apply {
            content = "啥hi撒大大"
            sendTime = "1992.09.08 12:12:12"
            name = "民工"

        })
        return mList
    }
    fun setConversationNickName(name:String){
        mNickName = name
    }

    fun setConversationUserId(id:String){
        this.mConversationUserId = id
        mConversation = mEMClient.chatManager().getConversation(mConversationUserId)

    }
}