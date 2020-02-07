package com.bai.psychedelic.psychat.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.FragmentFindViewModel

class FindFragment : Fragment() {

    private lateinit var viewModel: FragmentFindViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FragmentFindViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
