package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject


class LoginViewModel : ViewModel(), KoinComponent {
    private val mEMClient: EMClient by inject()


}