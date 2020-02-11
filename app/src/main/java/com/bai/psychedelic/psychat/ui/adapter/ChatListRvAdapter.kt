package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.ChatViewModel
import com.bai.psychedelic.psychat.databinding.ChatRvListItemReceiveBinding
import com.bai.psychedelic.psychat.databinding.ChatRvListItemSendBinding
import com.bai.psychedelic.psychat.utils.CHAT_TYPE_SEND_TXT
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class ChatListRvAdapter constructor(context: Context, list: ArrayList<ChatItemEntity>, variableId: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext = context
    private var mList:ArrayList<ChatItemEntity>  = list
    private val mVariabledId = variableId

    fun refreshList(list:ArrayList<ChatItemEntity>){
        mList.clear()
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CHAT_TYPE_SEND_TXT){
            val chatRvListSendBinding = DataBindingUtil.inflate<ChatRvListItemSendBinding>(
                LayoutInflater.from(mContext),
                R.layout.chat_rv_list_item_send,parent,false)
            val viewHolder = ViewHolderSendOutText(chatRvListSendBinding.root)
            viewHolder.setBinding(chatRvListSendBinding)
            return viewHolder
        }else
//            if (viewType == CHAT_TYPE_GET_TXT)
        {
            val chatRvListReceiveBinding = DataBindingUtil.inflate<ChatRvListItemReceiveBinding>(
                LayoutInflater.from(mContext),
                R.layout.chat_rv_list_item_receive,parent,false)
            val viewHolder = ViewHolderReceiveInText(chatRvListReceiveBinding.root)
            viewHolder.setBinding(chatRvListReceiveBinding)
            return viewHolder
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderSendOutText){
            holder.getBinding().setVariable(mVariabledId, mList[position])
            holder.getBinding().executePendingBindings()
        }else if (holder is ViewHolderReceiveInText){
            holder .getBinding().setVariable(mVariabledId, mList[position])
            holder.getBinding().executePendingBindings()
        }



    }

    override fun getItemViewType(position: Int): Int {
        val entity = mList.get(position)
        return entity.type
    }


    inner class ViewHolderSendOutText(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var binding: ViewDataBinding? = null
        fun getBinding(): ViewDataBinding {
            return binding!!
        }

        fun setBinding(binding: ViewDataBinding) {
            this.binding = binding
        }
    }
    inner class ViewHolderReceiveInText(itemView: View) :
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
