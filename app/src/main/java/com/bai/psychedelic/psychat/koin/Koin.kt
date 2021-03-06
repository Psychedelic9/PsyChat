package com.bai.psychedelic.psychat.koin



import com.arcb.laborpos.net.UrlDefinition
import com.bai.psychedelic.psychat.BuildConfig
import com.bai.psychedelic.psychat.data.viewmodel.*
import com.bai.psychedelic.psychat.net.InterceptorLogger
import com.bai.psychedelic.psychat.net.LogInterceptor
import com.bai.psychedelic.psychat.net.WebService
import com.bai.psychedelic.psychat.repository.DataRepository
import com.bai.psychedelic.psychat.utils.*
import com.hyphenate.chat.EMClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.cache.CacheInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *@Author: yiqing
 *@CreateDate: 2019/5/16 14:26
 *@UpdateDate: 2019/5/16 14:26
 *@Description:
 *@ClassName: koin

 */
val netModule: Module = module {

    single<OkHttpClient> {
        //缓存路径
        val file = File(AppManager.getApplication().cacheDir.absolutePath, "HttpCache")
        val cache = Cache(file, NET_CACHE_SIZE)
        OkHttpClient.Builder()
            .connectTimeout(NET_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(NET_WRITE_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NET_READ_TIME_OUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
//            .addInterceptor(EncryptInterceptor())
            .addInterceptor(
                LogInterceptor(
                    if (BuildConfig.DEBUG) LogInterceptor.Level.BODY else LogInterceptor.Level.NONE,
                    object : InterceptorLogger {
                        override fun log(msg: String) {
                            MyLog.d("Koin", msg)
                        }
                    }
                )
            )
            .addInterceptor(CacheInterceptor())
            .cache(cache)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(UrlDefinition.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(get<OkHttpClient>())
            .build()
    }

    single<WebService> {
        get<Retrofit>().create(WebService::class.java)
    }




}
val appModule = module {
    single<EMClient> {
        EMClient.getInstance()
    }

    single<ExecutorService>{
        Executors.newSingleThreadExecutor()
    }
}
/**
 * 适配器 Module
 */
val adapterModule:Module = module {
//    factory { WechatRvAdapter() }
}

/**
 * 数据仓库 Module
 */
val repositoryModule: Module = module {
    factory { DataRepository() }
}

val viewModelModule: Module = module {
    viewModel { MainViewModel() }
    viewModel { LoginViewModel() }
//    viewModel { RegisterViewModel(get())}
    viewModel { FragmentChatListViewModel() }
    viewModel { FragmentContactListViewModel() }
//    viewModel { FindModel() }
    viewModel { FragmentMeViewModel() }
    viewModel { ChatViewModel() }
    viewModel { AddFriendsViewModel() }
    viewModel { SplashViewModel() }
    viewModel { UserDetailViewModel() }

}
