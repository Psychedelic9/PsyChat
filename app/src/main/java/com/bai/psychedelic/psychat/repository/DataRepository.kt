package com.bai.psychedelic.psychat.repository

import com.bai.psychedelic.psychat.data.entity.ResultEntity
import com.bai.psychedelic.psychat.net.WebService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File


class DataRepository:KoinComponent{
    val webService:WebService by inject()
    fun login(phone:String?, password:String?, success:(ResultEntity)->Unit,
              failed:((Throwable) -> Unit)? = null){
//        addDisposable(webService.login(phone,password)
//            .asyncSchedulers()
//            .subscribeOn(Schedulers.io())
//            .netSubscribe(success,failed)
//        )
    }
    fun uploadIcon(userId:String,file:File){
        val requestBody = RequestBody.create(MediaType.parse("multipart/from-data"),file)
        val body = MultipartBody.Part.createFormData("imageFile",file.name,requestBody)
        val subscribe = webService.upLoadImage(userId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.toString()
            }, {

            })

    }




}