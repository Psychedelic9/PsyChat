package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_GET_TXT
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT

class ChatViewModel:ViewModel() {
    private var mList = ArrayList<ChatItemEntity>()
    fun getChatList():ArrayList<ChatItemEntity>{
        val entity1 = ChatItemEntity().apply {
            id = 12345
            name = "鸣鸣"
            type = CHAT_TYPE_GET_TXT
            content = "今晚吃庐州太太"
        }
        val entity2 = ChatItemEntity().apply {
            id = 12346
            name = "祁门路搅屎王"
            type = CHAT_TYPE_SEND_TXT
            content = "你请客没问题不"
        }
        val entity3 = ChatItemEntity().apply {
            id = 12345
            name = "鸣鸣"
            type = CHAT_TYPE_GET_TXT
            content = "问题不大"
        }
        mList.add(entity1)
        mList.add(entity2)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity3)
        mList.add(entity1)
        return mList
    }
}