package com.bai.psychedelic.psychat.utils

import android.content.Context
import com.bai.psychedelic.psychat.application.MyApplication
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


object VideoCache {

    private var sDownloadCache: SimpleCache? = null

    fun getInstance(context:Context): SimpleCache{
        if (sDownloadCache == null)
            sDownloadCache =
                SimpleCache(createFile(context.externalCacheDir!!.absolutePath),
                    NoOpCacheEvictor())
        return sDownloadCache as SimpleCache
    }

    private fun createFile(path: String): File {
        val file = File(path)
        //判断文件目录是否存在
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }
}