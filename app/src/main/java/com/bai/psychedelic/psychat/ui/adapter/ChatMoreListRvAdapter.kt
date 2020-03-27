package com.bai.psychedelic.psychat.ui.adapter

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ChatMoreEntity
import com.bai.psychedelic.psychat.databinding.ChatMoreRvItemBinding
import com.bai.psychedelic.psychat.ui.activity.ChatActivity
import com.bai.psychedelic.psychat.ui.activity.VoiceCallActivity
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_CAMERA
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_IMAGE
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_VOICE_CALL
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ChatMoreListRvAdapter constructor(
    context: ChatActivity,
    list: ArrayList<ChatMoreEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<ChatMoreEntity> = list
    private var mContext: ChatActivity = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ChatMoreRvItemBinding>(
            LayoutInflater.from(mContext), R.layout.chat_more_rv_item, parent, false
        )
        val viewHolder = ViewHolder(binding.root)
        viewHolder.setBinding(binding)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            (holder.getBinding() as ChatMoreRvItemBinding).chatMoreTv.text = mList[position].text
            (holder.getBinding() as ChatMoreRvItemBinding).chatMoreIv.setImageResource(mList[position].sourceId)
            when (mList[position].sourceId) {
                R.drawable.icon_photo -> {
                    (holder.getBinding() as ChatMoreRvItemBinding).chatMoreCl.setOnClickListener {
                        mContext.startActivityForResult(selectPicture(), START_ACTIVITY_IMAGE)
                    }
                }
                R.drawable.icon_camera -> {
                    (holder.getBinding() as ChatMoreRvItemBinding).chatMoreCl.setOnClickListener {
                        mContext.startActivityForResult(takePicByCamera(), START_ACTIVITY_CAMERA)
                    }
                }
                R.drawable.icon_phone -> {
                    (holder.getBinding() as ChatMoreRvItemBinding).chatMoreCl.setOnClickListener {
                        val intent = Intent(mContext,VoiceCallActivity::class.java)
                        intent.putExtra("isComingCall",false)
                        intent.putExtra("username",mContext.getViewModel().getConversationUserId())
                        mContext.startActivityForResult(intent, START_ACTIVITY_VOICE_CALL)
                    }
                }
            }
        }
    }

    private fun takePicByCamera(): Intent {
        val outputImage = mContext.getCameraPicFile()
        val photoUri: Uri
        try {
            //判断文件是否存在，存在删除，不存在创建
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //判断当前Android版本
        photoUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                mContext,
                "com.bai.psychedelic.psychat.fileProvider",
                outputImage
            )
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
        return intent
    }

    //调用系统图库选择图片
    private fun selectPicture(): Intent {
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