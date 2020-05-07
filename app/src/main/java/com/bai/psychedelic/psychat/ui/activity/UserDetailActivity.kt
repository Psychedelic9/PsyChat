package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.UserDetailViewModel
import com.bai.psychedelic.psychat.databinding.ActivityUserDetailBinding
import com.bai.psychedelic.psychat.databinding.TakePicMenuBinding
import com.bai.psychedelic.psychat.utils.MyLog
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_CAMERA
import com.bai.psychedelic.psychat.utils.START_ACTIVITY_IMAGE
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import org.koin.android.viewmodel.ext.android.viewModel
import top.zibin.luban.CompressionPredicate
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.IOException
import java.util.*


class UserDetailActivity : AppCompatActivity() {
    private lateinit var mContext: Context
    private lateinit var mBinding: ActivityUserDetailBinding
    private lateinit var mPopupWindow: PopupWindow
    private lateinit var cameraPicFile: File
    private val TAG = "UserDetailActivity"
    private val mViewModel:UserDetailViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        StatusBarUtil.setStatusTextColor(true, this)
        mContext = this
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_user_detail)
        mBinding.model = mViewModel
        initPopupMenu()
    }

    fun backPress(view: View) {
        onBackPressed()
    }

    private fun initPopupMenu(){
        val menuBinding = DataBindingUtil.inflate<TakePicMenuBinding>(
            LayoutInflater.from(mContext),
            R.layout.take_pic_menu,
            null,
            false
        )
         mPopupWindow = PopupWindow(
            menuBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        mPopupWindow.setOnDismissListener {
            bgAlpha(1.0f)
        }


        menuBinding.takePicMenuTakePic.setOnClickListener {
            startActivityForResult(takePicByCamera(),START_ACTIVITY_CAMERA)
            mPopupWindow.dismiss()
        }
        menuBinding.takePicMenuSelectFromAlbum.setOnClickListener {
            startActivityForResult(selectPicture(),START_ACTIVITY_IMAGE)
            mPopupWindow.dismiss()
        }
    }

    private fun getImagePathFromUri(uri:Uri):String{
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver
            .query(uri, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            cursor = null
            MyLog.d(TAG, "getImagePathFromUri $picturePath")
            return picturePath
        }else{
            MyLog.d(TAG, "getImagePathFromUri 获取图片地址失败！")
            Toast.makeText(mContext,"获取图片地址失败!",Toast.LENGTH_LONG).show()
            return "err"
        }
    }

    private fun zipPicture(path:String){
        Luban.with(mContext)
        .load(path)
        .ignoreBy(300)
        .setTargetDir(externalCacheDir!!.absolutePath)
        .filter(object: CompressionPredicate{
            override fun apply(path: String?): Boolean {
                return !(TextUtils.isEmpty(path) || path!!.toLowerCase().endsWith(".gif"))
            }
        })
        .setCompressListener(object: OnCompressListener{
            override fun onSuccess(file: File?) {
                MyLog.d(TAG,"zipPicture 图片压缩成功")

            }

            override fun onError(e: Throwable?) {
                MyLog.d(TAG,"zipPicture 图片压缩失败")
            }

            override fun onStart() {
                MyLog.d(TAG,"zipPicture 图片压缩开始")
            }
        }).launch()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                START_ACTIVITY_IMAGE -> {
                    if (null != data) {
                        val uri = data.data
                        MyLog.d(TAG, "uri = ${data.data}")
                        if (uri != null) {
                            //TODO:压缩图片上传服务器
                            val imagePath = getImagePathFromUri(uri)

                        }
                    }
                }
                START_ACTIVITY_CAMERA -> {
                    MyLog.d(TAG, "uri = ${Uri.fromFile(cameraPicFile)}")
                    val uri = Uri.fromFile(cameraPicFile)
                    if (uri!=null){
                        //TODO:压缩图片上传服务器
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun takePicByCamera(): Intent {
        val outputImage = getCameraPicFile()
        val photoUri: Uri
        try {
            //判断文件是否存在，存在删除，不存在创建
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //判断当前Android版本
        photoUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                mContext,
                "com.bai.psychedelic.psychat.fileProvider",
                outputImage
            )
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
        return intent
    }
    //调用系统图库选择图片
    private fun selectPicture(): Intent {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        return intent
    }
    private fun getCameraPicFile(): File {
        cameraPicFile = File(mContext.externalCacheDir, "${UUID.randomUUID()}.jpg")
        return cameraPicFile
    }

    fun selectPic(view: View) {
        bgAlpha(0.618f)
        val rootView = LayoutInflater.from(mContext).inflate(R.layout.activity_user_detail, null)
        mPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)

    }

    private fun bgAlpha(f: Float) {
        val layoutParams = window.attributes
        layoutParams.alpha = f
        window.attributes = layoutParams
    }

}
