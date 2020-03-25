package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.TransitionInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.BuildConfig
import com.bai.psychedelic.psychat.databinding.ActivityVideoFullScreenBinding
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.bai.psychedelic.psychat.utils.VIDEO_URL_FROM_DB
import com.bai.psychedelic.psychat.utils.VideoCache
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.ui.custom.ProgressDialogHelper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlin.math.max


class VideoFullScreenActivity : AppCompatActivity() {
    private val TAG = "VideoFullScreenActivity"
    private lateinit var mBinding: ActivityVideoFullScreenBinding
    private lateinit var mContext: AppCompatActivity
    private lateinit var mUri: String
    private lateinit var mPlayerView: PlayerView
    private var mPlayer: ExoPlayer? = null
    private var startAutoPlay: Boolean = true
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private val KEY_WINDOW = "window"
    private val KEY_POSITION = "position"
    private val KEY_AUTO_PLAY = "auto_play"
    private var mMediaSource:MediaSource?=null

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
        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            currentWindow = savedInstanceState.getInt(KEY_WINDOW)
            playbackPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            clearStartPosition()
        }
    }

    override fun onStart() {
        super.onStart()
        initExoPlayer()
    }

    override fun onResume() {
        super.onResume()
        if (mPlayer == null) {
            initExoPlayer()
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        currentWindow = C.INDEX_UNSET
        playbackPosition = C.TIME_UNSET
    }

    private fun updateStartPosition() {
        MyLog.d(TAG,"updateStartPosition")
        startAutoPlay = mPlayer!!.playWhenReady
        currentWindow = mPlayer!!.currentWindowIndex
        playbackPosition = max(0, mPlayer!!.contentPosition)
    }

    private fun initExoPlayer() {
        if (mPlayer == null) {
            mPlayerView = mBinding.activityFullScreenVideoPlayerView
            mPlayerView.requestFocus()
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(), DefaultLoadControl()
            )
            mPlayer!!.addListener(PlayerEventListener())
            mPlayerView.player = mPlayer
            mPlayer!!.playWhenReady = startAutoPlay
            if (mUri.isNotEmpty()) {
                MyLog.d(TAG, "uri = $mUri")
                val uri = Uri.parse(mUri)
                MyLog.d(TAG, "uri = $uri")
                mMediaSource = buildMediaSource(uri)
            }
        }
        val haveStartPosition = currentWindow != C.INDEX_UNSET
        MyLog.d(TAG, "haveStartPosition $haveStartPosition")
        if (haveStartPosition) {
            mPlayer!!.seekTo(currentWindow, playbackPosition)
        }
        mPlayer!!.prepare(mMediaSource, !haveStartPosition, false)
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

    private fun releasePlayer() {
        if (mPlayer != null) {
            updateStartPosition()
            mPlayer!!.release()
            mPlayer = null
            mMediaSource = null
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        updateStartPosition()
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, currentWindow)
        outState.putLong(KEY_POSITION, playbackPosition)
    }


    private fun setStatusBar() {
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onSeekProcessed() {
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
        }

        override fun onLoadingChanged(isLoading: Boolean) {
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    ProgressDialogHelper.show(mContext, false)
                }
                Player.STATE_READY -> {
                    ProgressDialogHelper.dismiss()
                }
                else -> {
                    ProgressDialogHelper.dismiss()
                }
            }
        }


    }

}
