package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatMoreEntity
import com.bai.psychedelic.psychat.databinding.ChatMoreRvItemBinding

class ChatMoreListRvAdapter constructor(
    context: Context,
    list: ArrayList<ChatMoreEntity>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList:ArrayList<ChatMoreEntity> = list
    private var mContext:Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ChatMoreRvItemBinding>(
            LayoutInflater.from(mContext), R.layout.chat_more_rv_item,parent,false
        )
        val viewHolder = ViewHolder(binding.root)
        viewHolder.setBinding(binding)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            (holder.getBinding() as ChatMoreRvItemBinding).chatMoreTv.text = mList[position].text
            (holder.getBinding() as ChatMoreRvItemBinding).chatMoreIv.setImageResource(mList[position].sourceId)
        }
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