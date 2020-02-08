package com.bai.psychedelic.psychat.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.FragmentMeViewModel
import com.bai.psychedelic.psychat.databinding.MeFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class MeFragment : Fragment() {
    private lateinit var mContext: AppCompatActivity
    private lateinit var mRootView: View
    private lateinit var mBinding:MeFragmentBinding
    private val mViewModel: FragmentMeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as AppCompatActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.me_fragment, container, false)
        mRootView = mBinding.root

        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
