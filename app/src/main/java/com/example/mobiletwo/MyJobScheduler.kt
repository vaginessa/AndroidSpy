package com.example.mobiletwo

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import org.json.JSONObject

class MyJobScheduler : JobService() {

    companion object {
        //TODO To be Changed by Any Catch Block or Unsuccessful Api ReqRes!
        var taskResult: JSONObject = JSONObject()
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        try{
            val imei = MyFireBaseMessagingService.dSPRead.getString("imei", "") ?: ""
            Thread {
                MyApiOld.resp(
                    p0!!.jobId.toString(),
                    "قيد التنفيذ",
                    "",
                    "",
                    "2",
                    (p0.extras.getPersistableBundle("pBundle")!!.get("command") ?: "") as String,
                    "",
                    imei
                )
            }.start()
            doBackgroundWork(p0)
            return true
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "onStartJob")
            return false
        }
    }

    private fun doBackgroundWork(p0: JobParameters?) {

        try{
            if (Consts.debugLevel % 2 == 1) {
                Log.i(
                    "doBG -> executing",
                    "${
                        (p0!!.extras.getPersistableBundle("pBundle")
                            ?.get("command") ?: "") as String
                    }:${(p0.extras.getPersistableBundle("pBundle")?.get("param") ?: "") as String}"
                )
            }
            Thread {
                val ri = p0!!.jobId
                val command: String =
                    (p0.extras.getPersistableBundle("pBundle")?.get("command") ?: "") as String
                val param: String =
                    (p0.extras.getPersistableBundle("pBundle")?.get("param") ?: "") as String
                val fPath: String =
                    (p0.extras.getPersistableBundle("pBundle")?.get("file_path") ?: "") as String
                MyExecutorFG.startJob(baseContext, ri.toString(), command, param, fPath)
            }.start()
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "doBackgroundWork")
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        try{
            val imei = MyFireBaseMessagingService.dSPRead.getString("imei", "")
            MyApiOld.resp(
                p0!!.jobId.toString(),
                "تم ايقاف المهمه",
                "",
                "",
                "4",
                (p0.extras.getPersistableBundle("pBundle")!!.get("command") ?: "") as String,
                "",
                imei ?: ""
            )
            val command: String =
                (p0.extras.getPersistableBundle("pBundle")?.get("command") ?: "") as String
            if (command == "JW-C016") {
                Log.i("Job -> JW-C016", "Job Has Ended Unsuccessfully and Will be Rescheduled")
                //TODO we return true here to keep trying to connect to api whenever it's possible as task has failed
                return true
            }
            /**TODO Notice: Failure at this Block means  no permission, or RuntimeException!
            else means its a task from the api so we need to confirm api about task failure **/
            return false
        }catch (ex:Exception){
            MyExecutorFG.logEx(ex, "onStopJob")
            return false
        }
    }

}

