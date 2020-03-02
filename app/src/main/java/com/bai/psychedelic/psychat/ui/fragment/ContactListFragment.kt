package com.bai.psychedelic.psychat.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bai.psychedelic.psychat.BR

import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.entity.ContactListItemEntity
import com.bai.psychedelic.psychat.data.viewmodel.FragmentContactListViewModel
import com.bai.psychedelic.psychat.databinding.ContactListFragmentBinding
import com.bai.psychedelic.psychat.ui.adapter.ContactListRvAdapter
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMValueCallBack
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService

class ContactListFragment : Fragment() {

    private val mViewModel: FragmentContactListViewModel by viewModel()
    private lateinit var mBinding: ContactListFragmentBinding
    private lateinit var mRootView: View
    private lateinit var mContext: Context
    private lateinit var mAdapter: ContactListRvAdapter
    private var mContactList = ArrayList<ContactListItemEntity>()
    private val mSingleExecutor: ExecutorService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.contact_list_fragment, container, false)
        mRootView = mBinding.root
        mBinding.model = mViewModel
        mAdapter = ContactListRvAdapter(mContext, mContactList, BR.contactListItem)
        mBinding.fragmentContactListRv.layoutManager = LinearLayoutManager(mContext)
        mBinding.fragmentContactListRv.adapter = mAdapter

        mSingleExecutor.execute {
            val contactList = mViewModel.getContactList()
            MyLog.d("getContactList","contactList.size = ${contactList.size}")
            refreshContackList(contactList)
        }

//        mViewModel.getContactList(object : EMValueCallBack<List<String>> {
//            override fun onSuccess(userNameList: List<String>?) {
//                if (userNameList!=null && userNameList.isNotEmpty())
//                for (username in userNameList) {
//                    mContactList.add(ContactListItemEntity().apply {
//                        id = username
//                        //TODO:userIcon
//                    })
//                }
//                mAdapter.notifyDataSetChanged()
//            }
//
//            override fun onError(error: Int, errorMsg: String?) {
//                MyLog.d("getContactList","onError code = $error msg = $errorMsg")
//                activity?.runOnUiThread {
//                    Toast.makeText(mContext,"获取联系人列表失败！",Toast.LENGTH_LONG).show()
//                }
//            }
//
//        })





        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun refreshContackList(list:ArrayList<ContactListItemEntity>){
        activity?.runOnUiThread {
            mAdapter.notifyDataSetChanged()
        }
    }
}
