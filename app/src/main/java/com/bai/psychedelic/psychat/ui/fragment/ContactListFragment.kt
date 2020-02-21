package com.bai.psychedelic.psychat.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.FragmentContactListViewModel
import com.bai.psychedelic.psychat.databinding.ContactListFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class ContactListFragment : Fragment() {

    private val mViewModel: FragmentContactListViewModel by viewModel()
    private lateinit var mBinding: ContactListFragmentBinding
    private lateinit var mRootView: View
    private lateinit var mContext:Context

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
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
