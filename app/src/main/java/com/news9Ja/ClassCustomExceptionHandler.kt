package com.news9Ja

import android.os.Environment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.collections.HashMap


class ClassCustomExceptionHandler(private val localPath: String, private val url: String): Thread.UncaughtExceptionHandler {/*
 * if any of the parameters is null, the respective functionality
 * will not be used
 */

    private val defaultUEH= Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t:Thread, e:Throwable) {
        val timestamp = Calendar.getInstance().timeInMillis.toString()+"_${(100..999).shuffled().last()}"
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        e.printStackTrace(printWriter)
        val stacktrace = result.toString()
        printWriter.close()
        val filename = "$timestamp.txt"
        if (localPath != null){
            if (Environment.getExternalStorageDirectory().canWrite()){
                val dir = File(localPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                writeToFile(stacktrace, filename)
            }
        }
        if (url != null){
            sendToServer(stacktrace, filename)
        }
        defaultUEH.uncaughtException(t, e)
    }
    private fun writeToFile(stacktrace:String, filename:String) {
        try{
            val bos = BufferedWriter(FileWriter("$localPath/$filename"))
            bos.write(stacktrace)
            bos.flush()
            bos.close()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }
    private fun sendToServer(stacktrace:String, filename:String) {

        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_APP_CRASH_REPORT_UPLOAD,
                Response.Listener<String> { response ->

                    try {
//                        val obj = JSONObject(response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { _ ->
                    //                    ClassAlertDialog(context).toast("ERROR IN NETWORK CONNECTIONnbb!")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["request_type"] = "send_crash_report"
                params["filename"] = filename
                params["stacktrace"] = stacktrace
                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        //volley interactions end
    }
}