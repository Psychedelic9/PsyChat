package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.WechatRvListItemEntity
import com.bai.psychedelic.psychat.databinding.WechatRvListItemBinding

class ChatListRvAdapter(context: Context, list: ArrayList<WechatRvListItemEntity>, variableId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext = context
    private val mList: ArrayList<WechatRvListItemEntity> = list
    private val mVariableId = variableId

    public fun addData(entity: WechatRvListItemEntity) {
        mList.add(entity)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<WechatRvListItemBinding>(
            LayoutInflater.from(mContext),
            R.layout.wechat_rv_list_item, parent, false
        )
        val viewHolder = ChatListViewHolder(binding.root)
        viewHolder.setBinding(binding)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatListViewHolder).getBinding().setVariable(mVariableId,mList[position])
        (holder.getBinding() as WechatRvListItemBinding).fragmentChatItemLl.setOnClickListener {
            //TODO:jump to chat activity
        }
        holder.getBinding().executePendingBindings()
    }

    inner class ChatListViewHolder(itemView: View) :
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