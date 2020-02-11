package com.bai.psychedelic.psychat.data.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.WechatRvListItemEntity
import com.bai.psychedelic.psychat.utils.UserUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentWeChatViewModel : ViewModel(),KoinComponent {
    private lateinit var mAllConversations:Map<String, EMConversation>
    private val mEMClient:EMClient by inject()
    @SuppressLint("SimpleDateFormat")
    fun getChatListFromDB(): ArrayList<WechatRvListItemEntity> {
        val list = ArrayList<WechatRvListItemEntity>()
        mAllConversations = mEMClient.chatManager().allConversations
        val conversationIds = mAllConversations.keys
        conversationIds.forEach {
            val conversation = mAllConversations[it]
            var conversationType = when(conversation?.lastMessage?.chatType){
                EMMessage.ChatType.Chat->"chat"
                EMMessage.ChatType.ChatRoom->"chat_room"
                EMMessage.ChatType.GroupChat->"group_chat"
                else ->""
            }
            val conversationLastMessage = when(conversation?.lastMessage?.type){
                EMMessage.Type.TXT->(conversation.lastMessage?.body as EMTextMessageBody).message
                EMMessage.Type.IMAGE->"[图片消息]"
                EMMessage.Type.VIDEO->"[视频消息]"
                EMMessage.Type.LOCATION->"[位置消息]"
                EMMessage.Type.VOICE->"[语音消息]"
                EMMessage.Type.FILE->"[文件]"
                else->""

            }

            var entity = WechatRvListItemEntity(
                id = Random().nextLong(),
                name = conversation?.conversationId().toString(),
                content = conversationLastMessage,
                msgCount = conversation?.unreadMsgCount.toString(),
                lastTime = UserUtils.changeLongTimeToDateTime(conversation?.lastMessage?.msgTime),
                icon = "123",
                nickName = conversation?.lastMessage?.userName.toString(),
                messageType = conversationType
            )
            list.add(entity)
        }

        return list

    }
}
