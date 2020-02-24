package com.bai.psychedelic.psychat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ContactListItemEntity
import com.bai.psychedelic.psychat.databinding.ContactListRvItemBinding

class ContactListRvAdapter constructor(context:Context,list:ArrayList<ContactListItemEntity>,variableId: Int)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList:ArrayList<ContactListItemEntity> = list
    private val mContext:Context = context
    private val mVariableId:Int = variableId
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val contactListBinding = DataBindingUtil.inflate<ContactListRvItemBinding>(LayoutInflater.from(mContext),
            R.layout.contact_list_rv_item,parent,false)
        val viewHolder = ViewHolder(contactListBinding.root)
        viewHolder.setBinding(contactListBinding)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            holder.getBinding().setVariable(mVariableId, mList[position])
            holder.getBinding().executePendingBindings()
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