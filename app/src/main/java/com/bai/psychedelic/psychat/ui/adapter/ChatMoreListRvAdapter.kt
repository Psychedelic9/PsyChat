package com.bai.psychedelic.psychat.ui.adapter

import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatMoreEntity
import com.bai.psychedelic.psychat.databinding.ChatMoreRvItemBinding
import com.bai.psychedelic.psychat.ui.activity.ChatActivity
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_IMAGE

class ChatMoreListRvAdapter constructor(
    context: ChatActivity,
    list: ArrayList<ChatMoreEntity>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList:ArrayList<ChatMoreEntity> = list
    private var mContext:ChatActivity = context

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
            when(mList[position].sourceId){
                R.drawable.icon_photo->{
                    (holder.getBinding() as ChatMoreRvItemBinding).chatMoreCl.setOnClickListener {
                        mContext.startActivityForResult(selectPicture(),START_ACTIVITY_IMAGE)
                    }
                }
            }
        }
    }

    //调用系统图库选择图片
    fun selectPicture(): Intent {
        return Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
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