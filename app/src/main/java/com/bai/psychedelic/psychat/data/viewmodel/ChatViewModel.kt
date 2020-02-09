package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_GET_TXT
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT
import org.koin.core.KoinComponent

class ChatViewModel:ViewModel(),KoinComponent {

    private var mList = ArrayList<ChatItemEntity>()
    var mConversationUserId:String = ""
    var mNickName:String = ""
    fun getChatList():ArrayList<ChatItemEntity>{
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
    }
}