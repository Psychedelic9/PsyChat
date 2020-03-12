package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.databinding.*
import com.bai.psychedelic.psychat.ui.activity.ImageFullScreenActivity
import com.bai.psychedelic.psychat.ui.custom.ChatVoicePlayer
import com.bai.psychedelic.psychat.utils.*
import com.bumptech.glide.Glide
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMFileMessageBody
import com.hyphenate.chat.EMVoiceMessageBody
import kotlin.concurrent.thread


class ChatListRvAdapter constructor(
    context: Context,
    list: ArrayList<ChatItemEntity>,
    variableId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG = "ChatListRvAdapter"
    }

    private val mContext = context
    private var mList: ArrayList<ChatItemEntity> = list
    private val mVariableId = variableId
    private val chatVoicePlayer = ChatVoicePlayer.get(mContext)

    fun refreshList(list: ArrayList<ChatItemEntity>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            CHAT_TYPE_SEND_TXT -> {
                val chatRvListSendBinding = DataBindingUtil.inflate<ChatRvListItemTextSendBinding>(
                    LayoutInflater.from(mContext),
                    R.layout.chat_rv_list_item_text_send, parent, false
                )
                val viewHolder = ViewHolder(chatRvListSendBinding.root)
                viewHolder.setBinding(chatRvListSendBinding)
                return viewHolder
            }
            CHAT_TYPE_GET_TXT -> {
                val chatRvListReceiveBinding =
                    DataBindingUtil.inflate<ChatRvListItemTextReceiveBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.chat_rv_list_item_text_receive, parent, false
                    )
                val viewHolder = ViewHolder(chatRvListReceiveBinding.root)
                viewHolder.setBinding(chatRvListReceiveBinding)
                return viewHolder
            }
            CHAT_TYPE_GET_IMAGE -> {
                val chatRvListReceiveBinding =
                    DataBindingUtil.inflate<ChatRvListItemImageReceiveBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.chat_rv_list_item_image_receive, parent, false
                    )
                val viewHolder = ViewHolder(chatRvListReceiveBinding.root)
                viewHolder.setBinding(chatRvListReceiveBinding)
                return viewHolder
            }
            CHAT_TYPE_SEND_IMAGE -> {
                val chatRvListReceiveBinding =
                    DataBindingUtil.inflate<ChatRvListItemImageSendBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.chat_rv_list_item_image_send, parent, false
                    )
                val viewHolder = ViewHolder(chatRvListReceiveBinding.root)
                viewHolder.setBinding(chatRvListReceiveBinding)
                return viewHolder
            }
            CHAT_TYPE_GET_VOICE -> {
                val chatRvListReceiveBinding =
                    DataBindingUtil.inflate<ChatRvListItemVoiceReceiveBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.chat_rv_list_item_voice_receive, parent, false
                    )
                val viewHolder = ViewHolder(chatRvListReceiveBinding.root)
                viewHolder.setBinding(chatRvListReceiveBinding)
                return viewHolder
            }
            CHAT_TYPE_SEND_VOICE -> {
                val chatRvListSendBinding =
                    DataBindingUtil.inflate<ChatRvListItemVoiceSendBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.chat_rv_list_item_voice_send, parent, false
                    )
                val viewHolder = ViewHolder(chatRvListSendBinding.root)
                viewHolder.setBinding(chatRvListSendBinding)
                return viewHolder
            }
            else -> {
                val chatRvListSendBinding = DataBindingUtil.inflate<ChatRvListItemTextSendBinding>(
                    LayoutInflater.from(mContext),
                    R.layout.chat_rv_list_item_text_send, parent, false
                )
                val viewHolder = ViewHolder(chatRvListSendBinding.root)
                viewHolder.setBinding(chatRvListSendBinding)
                return viewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.getBinding().setVariable(mVariableId, mList[position])
            holder.getBinding().executePendingBindings()
            when (mList[position].type) {
                CHAT_TYPE_GET_IMAGE -> {
                    MyLog.d(TAG, "onBindViewHolder get Image url = ${mList[position].content}")
                    val layoutParams =
                        (holder.getBinding() as ChatRvListItemImageReceiveBinding)
                            .chatActivityIvReceiveImageChatContent.layoutParams
                    layoutParams.width = (mList[position].width)
                    layoutParams.height = (mList[position].height)
                    (holder.getBinding() as ChatRvListItemImageReceiveBinding)
                        .chatActivityIvReceiveImageChatContent.layoutParams = layoutParams
                    Glide.with(mContext).load(mList[position].content)
                        .override((mList[position].width), (mList[position].height))
                        .into(
                            (holder.getBinding() as ChatRvListItemImageReceiveBinding)
                                .chatActivityIvReceiveImageChatContent
                        )
                    (holder.getBinding() as ChatRvListItemImageReceiveBinding)
                        .chatActivityIvReceiveImageChatContent.setOnClickListener {
                        val intent = Intent(mContext, ImageFullScreenActivity::class.java)
                        intent.putExtra(PICURLFROMTHUMBNAIL, mList[position].content)
                        mContext.startActivity(intent)
                    }
                    return
                }
                CHAT_TYPE_SEND_IMAGE -> {
                    MyLog.d(TAG, "onBindViewHolder send Image url = ${mList[position].content}")
                    val layoutParams =
                        (holder.getBinding() as ChatRvListItemImageSendBinding)
                            .chatActivityIvSendImageChatContent.layoutParams
                    layoutParams.width = (mList[position].width)
                    layoutParams.height = (mList[position].height)
                    (holder.getBinding() as ChatRvListItemImageSendBinding)
                        .chatActivityIvSendImageChatContent.layoutParams = layoutParams
                    Glide.with(mContext).load(mList[position].content)
                        .override((mList[position].width), (mList[position].height))
                        .into(
                            (holder.getBinding() as ChatRvListItemImageSendBinding)
                                .chatActivityIvSendImageChatContent
                        )
                    (holder.getBinding() as ChatRvListItemImageSendBinding)
                        .chatActivityIvSendImageChatContent.setOnClickListener {
                        val intent = Intent(mContext, ImageFullScreenActivity::class.java)
                        intent.putExtra(PICURLFROMTHUMBNAIL, mList[position].content)
                        mContext.startActivity(intent)
                    }
                    return
                }
                CHAT_TYPE_GET_VOICE -> {
                    MyLog.d(TAG, "onBindViewHolder get voice url = ${mList[position].content}")
                    val layoutParams =
                        (holder.getBinding() as ChatRvListItemVoiceReceiveBinding)
                            .chatActivityTvReceiveChatContent.layoutParams
                    layoutParams.width = getVoiceWidthByTime(mList[position].voiceLength)
                    (holder.getBinding() as ChatRvListItemVoiceReceiveBinding)
                        .chatActivityTvReceiveChatContent.layoutParams = layoutParams
                    (holder.getBinding() as ChatRvListItemVoiceReceiveBinding)
                        .chatActivityTvReceiveChatVoiceLength.text =
                        mList[position].voiceLength.toString() + "\""
                    (holder.getBinding() as ChatRvListItemVoiceReceiveBinding)
                        .chatActivityTvReceiveChatContent.setOnClickListener {
                        (holder.getBinding() as ChatRvListItemVoiceReceiveBinding).chatActivityTvReceiveChatVoiceImage
                            .setBackgroundResource(R.drawable.voice_from_icon)
                        val animReceive =
                            (holder.getBinding() as ChatRvListItemVoiceReceiveBinding).chatActivityTvReceiveChatVoiceImage
                                .background as AnimationDrawable
                        if (!animReceive.isRunning) {
                            animReceive.start()
                        }
                        val message = mList[position].voiceMessage
                        MyLog.d(TAG,"receive voice message status = ${message?.status()} " +
                                " ${EMClient.getInstance().getOptions().getAutodownloadThumbnail()}" +
                                " voiceBody.downloadStatus() = ${(message?.body as EMVoiceMessageBody).downloadStatus()}" +
                                " LocalUrl = ${(message?.body as EMVoiceMessageBody).localUrl}" +
                                " remoteUrl = ${(message?.body as EMVoiceMessageBody).remoteUrl}")
                        if ((message?.body as EMVoiceMessageBody).downloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                            MyLog.d(TAG,"语音下载失败")
                            thread(true){
                                //TODO:线程池 判断download状态
                                EMClient.getInstance().chatManager().downloadAttachment(message)
                            }

                        }
                        chatVoicePlayer.play(mList[position].voiceMessage!!,
                            MediaPlayer.OnCompletionListener {
                                animReceive.stop()
                                (holder.getBinding() as ChatRvListItemVoiceReceiveBinding).chatActivityTvReceiveChatVoiceImage
                                    .setBackgroundResource(R.drawable.chatfrom_voice_playing)
                            })
                    }
                    return
                }
                CHAT_TYPE_SEND_VOICE -> {
                    MyLog.d(TAG, "onBindViewHolder send voice url = ${mList[position].content}")
                    val layoutParams =
                        (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                            .chatActivityTvSendChatContent.layoutParams
                    layoutParams.width = getVoiceWidthByTime(mList[position].voiceLength)
                    (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                        .chatActivityTvSendChatContent.layoutParams = layoutParams
                    (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                        .chatActivityTvSendChatVoiceLength.text =
                        mList[position].voiceLength.toString() + "\""
                    (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                        .chatActivityTvSendChatContent.setOnClickListener {

                        (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                            .chatActivityTvSendChatVoiceImage.setBackgroundResource(
                            R.drawable.voice_to_icon
                        )
                        val animSend = (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                            .chatActivityTvSendChatVoiceImage.background as AnimationDrawable

                        if (!animSend.isRunning) {
                            animSend.start()
                        }
                        chatVoicePlayer.play(mList[position].voiceMessage!!,
                            MediaPlayer.OnCompletionListener {
                                animSend.stop()
                                (holder.getBinding() as ChatRvListItemVoiceSendBinding)
                                    .chatActivityTvSendChatVoiceImage.setBackgroundResource(R.drawable.chatto_voice_playing)
                            })
                    }
                    return
                }
            }

        }


    }

    private fun getVoiceWidthByTime(timeLength: Int): Int {
        val point = Point()
        (mContext as AppCompatActivity).window.windowManager.defaultDisplay.getSize(point)
        val screenWidth = point.x
        var width = (((timeLength.toFloat() / 60) * screenWidth) * 1.0)
        MyLog.d(
            TAG,
            "getVoiceWidthByTime screenwidth = $screenWidth time = $timeLength width = $width"
        )
        val minWidthDp = DisplayUtil.dip2px(mContext,70f)
        val maxWidthMarginDp = DisplayUtil.dip2px(mContext,100f)
        if (width > screenWidth - maxWidthMarginDp){
            width = (screenWidth - maxWidthMarginDp).toDouble()
        }
        if (width < minWidthDp ){
            width += minWidthDp
        }
        return width.toInt()
    }

    override fun getItemViewType(position: Int): Int {
        val entity = mList[position]
        return entity.type
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var binding: ViewDataBinding? = null
        fun getBinding(): ViewDataBinding {
            return binding!!
        }

        fun setBinding(binding: ViewDataBinding) {
            this.binding = binding
        }
    }
}
