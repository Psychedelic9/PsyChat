package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_GET_TXT
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT
import org.koin.core.KoinComponent
import org.koin.core.inject
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.bai.psychedelic.psychat.utils.UserUtils
import com.hyphenate.chat.*
import java.lang.reflect.Type


class ChatViewModel:ViewModel(),KoinComponent {

    private var mList = ArrayList<ChatItemEntity>()
    private var mConversationUserId:String = ""
    var mNickName:String = ""
    private val mEMClient:EMClient by inject()
    private lateinit var mConversation: EMConversation
    fun getChatList():ArrayList<ChatItemEntity>{
        val messages = mConversation.allMessages
        mList.clear()
        messages.forEach {
            val chatItemEntity = ChatItemEntity()

                when(it?.type){
                    EMMessage.Type.TXT->{
                        chatItemEntity.apply {
                            type = if (it.from ==  mEMClient.currentUser){
                                CHAT_TYPE_SEND_TXT
                            }else{
                                CHAT_TYPE_GET_TXT
                            }
                            name = it.from
                            content = (it.body as EMTextMessageBody).message
                            sendTime = UserUtils.changeLongTimeToDateTime(it.msgTime)
                        }
                        mList.add(chatItemEntity)
                        return@forEach
                    }
                    EMMessage.Type.IMAGE->{}
                    EMMessage.Type.VIDEO->{}
                    EMMessage.Type.LOCATION->{}
                    EMMessage.Type.VOICE->{}
                    EMMessage.Type.FILE->{}
                    EMMessage.Type.CMD->{}

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