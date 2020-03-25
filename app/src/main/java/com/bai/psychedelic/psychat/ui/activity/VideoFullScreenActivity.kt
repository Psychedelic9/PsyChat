package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.BuildConfig
import com.bai.psychedelic.psychat.databinding.ActivityVideoFullScreenBinding
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.bai.psychedelic.psychat.utils.VIDEO_URL_FROM_DB
import com.bai.psychedelic.psychat.utils.VideoCache
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory
import androidx.core.net.toFile
import com.bai.psychedelic.psychat.R
import java.io.File
import java.io.FileInputStream


class VideoFullScreenActivity : AppCompatActivity() {
    private val TAG = "VideoFullScreenActivity"
    private lateinit var mBinding: ActivityVideoFullScreenBinding
    private lateinit var mContext: Context
    private lateinit var mUri: String
    private lateinit var mPlayerView: PlayerView
    private lateinit var mPlayer: ExoPlayer
    private val playWhenReady: Boolean = true
    private val currentWindow: Int = 0
    private val playbackPosition: Long = 0

    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, VideoFullScreenActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mUri = intent.getStringExtra(VIDEO_URL_FROM_DB)!!
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_full_screen)
        mContext = this
        setStatusBar()
        initExoPlayer()

    }

    private fun initExoPlayer() {
        mPlayerView = mBinding.activityFullScreenVideoPlayerView
        mPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(), DefaultLoadControl()
        )
        mPlayerView.player = mPlayer
        mPlayer.playWhenReady = playWhenReady
        mPlayer.seekTo(currentWindow, playbackPosition)
        if (mUri.isNotEmpty()) {
            MyLog.d(TAG, "uri = $mUri")
            val uri = Uri.parse(mUri)
            MyLog.d(TAG, "uri = $uri")


            val mediaSource = buildMediaSource(uri)
            mPlayer.prepare(mediaSource, true, false)
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val bandwidthMeter = DefaultBandwidthMeter()

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, BuildConfig.APPLICATION_ID), bandwidthMeter
        )
        //缓存
        var videoDataSourceFactory: CacheDataSourceFactory? = null
        val cache = VideoCache.getInstance(mContext)

//        val file = File(mContext.externalCacheDir!!.absolutePath)

//        val dataSize = FileInputStream(file).available().toLong()

//        if (dataSize < 512 * 1024 * 1024) {
            // CacheDataSinkFactory 第二个参数为单个缓存文件大小，如果需要缓存的文件大小超过此限制，则会分片缓存，不影响播放
            val cacheWriteDataSinkFactory = CacheDataSinkFactory(cache, java.lang.Long.MAX_VALUE)
            videoDataSourceFactory = CacheDataSourceFactory(
                cache,
                dataSourceFactory,
                FileDataSourceFactory(),
                cacheWriteDataSinkFactory,
                CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                null
            )
//        }
        //        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        return if (videoDataSourceFactory == null) {
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        } else {
            ExtractorMediaSource.Factory(videoDataSourceFactory).createMediaSource(uri)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPlayer.stop() 
    }


    private fun setStatusBar() {
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }
}
