package com.bai.psychedelic.psychat.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bai.psychedelic.psychat.utils.MyLog

import java.util.ArrayList
import kotlin.concurrent.thread


/**
 * 可根据点击/多指触控 放大,放小的ImageVIew
 *
 * @author Gloomy
 * @date 2016年07月02日15:12:20
 */
class ZoomImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ImageView(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener,
    View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private var isInit: Boolean = false


    /**
     * 缩放工具
     */
    private var mMatrix: Matrix? = null

    /**
     * 缩放的最小值
     */
    private var mMinScale: Float = 0.toFloat()
    /**
     * 缩放的中间值
     */
    private var mMidScale: Float = 0.toFloat()
    /**
     * 缩放的最大值
     */
    private var mMaxScale: Float = 0.toFloat()

    /**
     * 多点手势触 控缩放比率分析器
     */
    private var mScaleGestureDetector: ScaleGestureDetector? = null

    //--自由移动

    /**
     * 记录上一次多点触控的数量
     */
    private var mLastPointereCount: Int = 0

    private var mLastX: Float = 0.toFloat()
    private var mLastY: Float = 0.toFloat()
    private var mTouchSlop: Int = 0
    private var isCanDrag: Boolean = false
    private var isCheckLeftAndRight: Boolean = false
    private var isCheckTopAndBottom: Boolean = false

    //----双击放大与缩小
    private var mGestureDetector: GestureDetector? = null
    private var isScaleing: Boolean = false
    private var events: List<MotionEvent>? = null
    private var onClickListener: View.OnClickListener? = null
    private val arae_img_id = -1

    /**
     * 获取图片放大缩小后的宽高/top/left/right/bottom
     *
     * @return
     */
    private val matrixRectF: RectF
        get() {
            val rectF = RectF()
            val drawable = drawable

            if (drawable != null) {
                rectF.set(
                    0f,
                    0f,
                    drawable.intrinsicWidth.toFloat(),
                    drawable.intrinsicHeight.toFloat()
                )
                mMatrix!!.mapRect(rectF)
            }

            return rectF
        }

    /**
     * 获取当前的缩放比率
     *
     * @return
     */
    private val scale: Float
        get() {
            val values = FloatArray(9)
            mMatrix!!.getValues(values)
            return values[Matrix.MSCALE_X]
        }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0) {
        initView(context)
    }

    fun initView(context: Context) {
        MyLog.d(TAG,"initView()")
        scaleType = ImageView.ScaleType.MATRIX
        mMatrix = Matrix()
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        mGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (isScaleing || scale >= mMaxScale)
                        return true
                    isScaleing = true
                    val x = e.x
                    val y = e.y

                    if (scale < mMidScale) {
                        postDelayed(AutoScaleRunnable(mMidScale, x, y), 16)
                    } else {
                        postDelayed(AutoScaleRunnable(mMinScale, x, y), 16)
                    }

                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(this@ZoomImageView)
                        return true
                    }
                    return false
                }
            })
        setOnTouchListener(this)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        events = ArrayList()
    }

    private inner class AutoScaleRunnable(
        /**
         * 要缩放的目标值
         */
        private val mTargetScale: Float, private val x: Float //缩放的中心点x
        , private val y: Float //缩放的中心点y
    ) : Runnable {
        private var tmpScale: Float = 0.toFloat()

        private val BIGGER = 1.07f
        private val SMALL = 0.93f

        init {

            if (scale < mTargetScale) {
                tmpScale = BIGGER
            } else {
                tmpScale = SMALL
            }
        }

        override fun run() {
            mMatrix!!.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix


            val currentScale = scale
            if (tmpScale > 1.0f && currentScale < mTargetScale || tmpScale < 1.0f && currentScale > mTargetScale) {
                postDelayed(this, 16)
            } else {
                val scale = mTargetScale / currentScale
                mMatrix!!.postScale(scale, scale, x, y)
                checkBorderAndCenterWhenScale()
                imageMatrix = mMatrix
                isScaleing = false
            }
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        log("注册了OnGlobalLayoutListener")
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    @SuppressLint("NewApi")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        log("反注册了OnGlobalLayoutListener")
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        log("执行了onGlobalLayout| NULL:" + (drawable == null))
        if (drawable == null || width == 0 || height == 0) return
        if (!isInit) {
            isInit = true
            thread {
                //共享元素动画和缩放有冲突导致初始化大小不对，暂时加个延时
               Thread.sleep(500)
               (context as AppCompatActivity).runOnUiThread {
                   log("初始化完毕")
                   val width = width
                   val height = height
                   val screenWeight = height * 1.0f / width
                   val imageH = drawable.intrinsicHeight // 图片高度
                   val imageW = drawable.intrinsicWidth // 图片宽度
                   val imageWeight = imageH * 1.0f / imageW
                   //如果当前屏幕高宽比 大于等于 图片高宽比,就缩放图片
                   if (screenWeight >= imageWeight) {
                       MyLog.d(TAG,"screenWeight >= imageWeight imageW = $imageW width = $width imageH = $imageH height = $height" )
                       var scale = 1.0f
                       //图片比当前View宽,但是比当前View矮
                       if (imageW > width && imageH <= height) {
                           scale = width * 1.0f / imageW //根据宽度缩放
                           MyLog.d(TAG,"图片比当前View宽,但是比当前View矮")
                       }

                       //图片比当前View窄,但是比当前View高
                       if (imageH > height && imageW <= width) {
                           scale = height * 1.0f / imageH //根据高度缩放
                           MyLog.d(TAG,"图片比当前View窄,但是比当前View高")

                       }

                       //图片高宽都大于当前View,那么就根据最小的缩放值来缩放
                       if (imageH > height && imageW > width) {
                           scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH)
                           log("max scale:$scale")
                       }

                       if (imageH < height && imageW < width) {
                           scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH)
                           log("min scale:$scale")
                       }

                       /**
                        * 设置缩放比率
                        */
                       mMinScale = scale
                       mMidScale = mMinScale * 2
                       mMaxScale = mMinScale * 4
                       /**
                        * 把图片移动到中心点去
                        */
                       val dx = getWidth() / 2 - imageW / 2
                       val dy = getHeight() / 2 - imageH / 2

                       /**
                        * 设置缩放(全图浏览模式,用最小的缩放比率去查看图片就好了)/移动位置
                        */
                       mMatrix!!.postTranslate(dx.toFloat(), dy.toFloat())
                       mMatrix!!.postScale(
                           mMinScale,
                           mMinScale,
                           (width / 2).toFloat(),
                           (height / 2).toFloat()
                       )
                   } else {
                       MyLog.d(TAG,"!screenWeight >= imageWeight")

                       //将宽度缩放至屏幕比例缩放(长图,全图预览)
                       val scale = width * 1.0f / imageW
                       /**
                        * 设置缩放比率
                        */
                       mMaxScale = scale
                       mMidScale = mMaxScale / 2
                       mMinScale = mMaxScale / 4

                       //因为是长图浏览,所以用最大的缩放比率去加载长图
                       //mMatrix.postTranslate(0, 0);
                       mMatrix!!.postScale(mMaxScale, mMaxScale, 0f, 0f)
                   }

                   imageMatrix = mMatrix
                   invalidate()
               }

           }
        }
    }


    /**
     * 设置初始化状态为false
     */
    fun reSetState() {
        isInit = false
        tag = null
        mMatrix!!.reset()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (mGestureDetector!!.onTouchEvent(motionEvent))
            return true

        //将触摸事件传递给ScaleGestureDetector
        if (motionEvent.pointerCount > 1)
            mScaleGestureDetector!!.onTouchEvent(motionEvent)


        var x = 0f
        var y = 0f

        val pointerCount = motionEvent.pointerCount

        for (i in 0 until pointerCount) {
            x += motionEvent.getX(i)
            y += motionEvent.getY(i)
        }

        x /= pointerCount.toFloat()
        y /= pointerCount.toFloat()

        if (mLastPointereCount != pointerCount) {
            isCanDrag = false
            mLastX = x
            mLastY = y
        }

        mLastPointereCount = pointerCount

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val rectF = matrixRectF
                if (rectF.width() > width + 0.01f || rectF.height() > height + 0.01f) {
                    if (rectF.right != width.toFloat() && rectF.left != 0f) {
                        try {
                            parent.requestDisallowInterceptTouchEvent(true)
                        } catch (e: Exception) {
                            log(e.toString())
                        }

                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {

                var dx = x - mLastX
                var dy = y - mLastY

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy)
                }

                if (isCanDrag) {
                    val rectF = matrixRectF

                    if (drawable != null) {
                        isCheckTopAndBottom = true
                        isCheckLeftAndRight = isCheckTopAndBottom

                        if (rectF.width() <= width) {
                            isCheckLeftAndRight = false
                            dx = 0f
                        }

                        if (rectF.height() <= height) {
                            isCheckTopAndBottom = false
                            dy = 0f
                        }

                        mMatrix!!.postTranslate(dx, dy)
                        checkBorderWhenTranslate()
                        imageMatrix = mMatrix
                    }
                }
                mLastX = x
                mLastY = y

                val rect = matrixRectF
                if (rect.width() > width + 0.01f || rect.height() > height + 0.01f) {
                    if (rect.right != width.toFloat() && rect.left != 0f) {
                        try {
                            parent.requestDisallowInterceptTouchEvent(true)
                        } catch (e: Exception) {
                            log(e.toString())
                        }

                    }
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mLastPointereCount = 0
            }
        }
        return true
    }

    /**
     * 在移动图片的时候进行边界检查
     */
    private fun checkBorderWhenTranslate() {
        val rectF = matrixRectF

        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val height = height

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom
        }


        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right
        }

        mMatrix!!.postTranslate(deltaX, deltaY)
        imageMatrix = mMatrix
    }

    /**
     * 判断是否足以触发移动事件
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        return Math.sqrt((dx * dx + dy * dy).toDouble()) > mTouchSlop
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        MyLog.d(TAG,"onScale()")
        var scaleFactor = detector.scaleFactor//获取用户手势判断出来的缩放值
        val scale = scale

        /**
         * 没有图片
         */
        if (drawable == null) return true

        //缩放范围控制
        if (scale < mMaxScale && scaleFactor > 1.0f || scale > mMinScale && scaleFactor < 1.0f) {
            if (scaleFactor * scale < mMinScale) {
                scaleFactor = mMinScale / scale
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale
            }

            mMatrix!!.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix
        }
        return true
    }

    /**
     * 在缩放的时候进行边界,位置 检查
     */
    private fun checkBorderAndCenterWhenScale() {
        val rectF = matrixRectF

        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val height = height

        if (rectF.width() >= width) {
            if (rectF.left > 0)
                deltaX = -rectF.left
            if (rectF.right < width)
                deltaX = width - rectF.right
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0)
                deltaY = 0f
            if (rectF.bottom < height)
                deltaY = height - rectF.bottom
        }

        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2
        }

        mMatrix!!.postTranslate(deltaX, deltaY)
        imageMatrix = mMatrix
    }

    override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
        return true //缩放开始,返回true 用于接收后续时间
    }

    override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {

    }

    override fun setImageBitmap(bm: Bitmap) {
        reSetState()
        super.setImageBitmap(bm)
    }

    override fun setImageResource(resId: Int) {
        reSetState()
        super.setImageResource(resId)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        reSetState()
        super.setImageDrawable(drawable)
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        this.onClickListener = l
    }

    companion object {

        private val TAG = "ZoomImageView"


        /**
         * 是否是Debug模式
         */
        private val IS_DEBUG = true

        /**
         * 打印日志
         *
         * @param value 要打印的日志
         */
        fun log(value: String) {
            if (IS_DEBUG)
                Log.w(TAG, value)
        }
    }
}