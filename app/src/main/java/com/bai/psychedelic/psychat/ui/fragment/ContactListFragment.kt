package com.bai.psychedelic.psychat.ui.fragment

import android.content.Context
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
import com.bai.psychedelic.psychat.data.entity.ContactListItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.FragmentContactListViewModel
import com.bai.psychedelic.psychat.databinding.ContactListFragmentBinding
import com.bai.psychedelic.psychat.ui.adapter.ContactListRvAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class ContactListFragment : Fragment() {

    private val mViewModel: FragmentContactListViewModel by viewModel()
    private lateinit var mBinding: ContactListFragmentBinding
    private lateinit var mRootView: View
    private lateinit var mContext:Context
    private lateinit var mAdapter:ContactListRvAdapter
    private lateinit var mContactList:ArrayList<ContactListItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.contact_list_fragment,container,false)
        mRootView = mBinding.root
        mBinding.model = mViewModel
        mContactList = mViewModel.getContactList()
        mAdapter = ContactListRvAdapter(mContext,mContactList, BR.contactListItem)
        mBinding.fragmentContactListRv.layoutManager = LinearLayoutManager(mContext)
        mBinding.fragmentContactListRv.adapter = mAdapter
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
