package com.bai.psychedelic.psychat.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bai.psychedelic.psychat.BR

import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.ui.adapter.ConversationListRvAdapter
import com.bai.psychedelic.psychat.data.entity.WechatRvListItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.FragmentWeChatViewModel
import com.bai.psychedelic.psychat.databinding.WeChatFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class WeChatFragment : Fragment() {
    private val mViewModel:FragmentWeChatViewModel by viewModel()
    private lateinit var mContext:AppCompatActivity
    private lateinit var mBinding: WeChatFragmentBinding
    private lateinit var mRootView: View
    private lateinit var mChatList : ArrayList<WechatRvListItemEntity>
    private lateinit var mAdapter : ConversationListRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as AppCompatActivity
        mChatList = mViewModel.getChatListFromDB()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.we_chat_fragment, container, false)
        mRootView = mBinding.root
        mAdapter = ConversationListRvAdapter(
            mContext,
            mChatList,
            BR.chatListItem
        )
        mBinding.wechatRv.layoutManager = LinearLayoutManager(mContext)
        mBinding.wechatRv.adapter = mAdapter
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
