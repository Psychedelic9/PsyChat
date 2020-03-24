package com.bai.psychedelic.psychat.utils

class FileUtil {
    companion object{
        fun isVideo(fileUri:String):Boolean{
            if (fileUri.contains(".mp4",true)
                ||fileUri.contains(".wmv",true)
                ||fileUri.contains(".avi",true)
                ||fileUri.contains(".mpg",true)
                ||fileUri.contains(".mov",true)
                ||fileUri.contains(".rm",true)
                ||fileUri.contains(".ram",true)
                ||fileUri.contains(".flv",true)
                ||fileUri.contains(".swf",true)
            ){
                return true
            }
            return false
        }

        fun isPicture(fileUri: String):Boolean{
            if (fileUri.contains(".mp3",true)
                ||fileUri.contains(".wma",true)
                ||fileUri.contains(".wav",true)
                ||fileUri.contains(".midi",true)
            ){
                return true
            }
            return false
        }

    }
}