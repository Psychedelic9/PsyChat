package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.databinding.ChatRvListItemImageReceiveBinding
import com.bai.psychedelic.psychat.databinding.ChatRvListItemImageSendBinding
import com.bai.psychedelic.psychat.databinding.ChatRvListItemTextReceiveBinding
import com.bai.psychedelic.psychat.databinding.ChatRvListItemTextSendBinding
import com.bai.psychedelic.psychat.ui.activity.ImageFullScreenActivity
import com.bai.psychedelic.psychat.utils.*
import com.bumptech.glide.Glide

class ChatListRvAdapter constructor(
    context: Context,
    list: ArrayList<ChatItemEntity>,
    variableId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext = context
    private var mList: ArrayList<ChatItemEntity> = list
    private val mVariableId = variableId
    private val TAG = "ChatListRvAdapter"
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
                        val intent = Intent(mContext,ImageFullScreenActivity::class.java)
                        intent.putExtra(PICURLFROMTHUMBNAIL,mList[position].content)
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
                        val intent = Intent(mContext,ImageFullScreenActivity::class.java)
                        intent.putExtra(PICURLFROMTHUMBNAIL,mList[position].content)
                        mContext.startActivity(intent)
                    }
                    return
                }
            }

        }


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
//    inner class ViewHolderReceiveInText(itemView: View) :
//        RecyclerView.ViewHolder(itemView) {
//        private var binding: ViewDataBinding? = null
//        fun getBinding(): ViewDataBinding {
//            return binding!!
//        }
//        fun setBinding(binding: ViewDataBinding) {
//            this.binding = binding
//        }
//    }
}
