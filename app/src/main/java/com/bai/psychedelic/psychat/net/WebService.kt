package com.bai.psychedelic.psychat.net

import com.arcb.laborpos.net.UrlDefinition
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


/**
 * 网络请求接口
 */
interface WebService {
    @Multipart
    @POST(UrlDefinition.TRANS_RESULT)
    fun upLoadImage(@Query("userId") userId:String,@Part file:MultipartBody.Part): Observable<Any>

}