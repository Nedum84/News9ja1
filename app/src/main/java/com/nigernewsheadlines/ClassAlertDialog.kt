package com.nigernewsheadlines

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import android.view.LayoutInflater
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject


class ClassAlertDialog(var context:Context) {

    init {
        val alertDialog:AlertDialog?=null
    }

    fun redirectToPlayStore(){
        val appPackageName = context.packageName // getPackageName() from Context or Activity object
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, Bundle())
        } catch (anfe: android.content.ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            val chooser = Intent.createChooser(intent,"Update using:")
            ContextCompat.startActivity(context, chooser, Bundle())
        }
    }
//    fun rateApp(){
//        AlertDialog.Builder(context)
//                .setView(LayoutInflater.from(context).inflate(R.layout.alert_dialog_inflate_rate_app, null))
//                .setPositiveButton("Rate Now"
//                ) { _, _ ->
//                    //actions
//                    redirectToPlayStore()
//                }
//                .setNegativeButton("Later"
//                ) { dialog, id ->
//
//                }.setCancelable(true)
//                .show()
//    }
//    fun feedBackMsg(){
//        val inflater = LayoutInflater.from(context).inflate(R.layout.alert_dialog_inflate_feedback_msg, null)
//        AlertDialog.Builder(context)
//                .setView(inflater)
//                .setPositiveButton("Send Message"
//                ) { _, _ ->
//                    //actions
//                    val feedbackMsgContent = inflater.feedback_msg_content
//                    sendFeedBackMsg(feedbackMsgContent.text.toString())
//                }
//                .setNegativeButton("Cancel"
//                ) { dialog, id ->
//
//                }.setCancelable(false)
//                .show()
//    }
    fun alertMessage(displayMsg:String){
        AlertDialog.Builder(context)
                .setMessage(displayMsg)
                .setPositiveButton("Ok"
                ) { dialog, id ->
                }.setCancelable(false)
                .show()
    }
//    private fun sendFeedBackMsg(msgContent:String){
//
//        //volley interactions start
//
//        //creating volley string request
//        val dialog = ClassProgressDialog(context)
//        dialog.createDialog()
//        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_SUBMIT_FEEDBACK_MSG,
//                Response.Listener<String> { response ->
//                    dialog.dismissDialog()
//
//                    try {
//                        val obj = JSONObject(response)
//                        val feedBackMsgStatus = obj.getString("feedBackMsgStatus")
//
//                        if (feedBackMsgStatus=="ok") {
//                            alertMessage("Feedback message sent successfully")
//                        }else{
//                            alertMessage(feedBackMsgStatus)
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        Toast.makeText(context, e.printStackTrace().toString() + " Error", Toast.LENGTH_LONG).show()
//                    }
//                },
//                Response.ErrorListener { volleyError ->
//                    dialog.dismissDialog()
//                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
//                }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String?> {
//                val params = HashMap<String, String?>()
//                params["request_type"] = "submit_feedback_msg"
//                params["msg_content"] = msgContent
//                return params
//            }
//        }
//        //adding request to queue
//        VolleySingleton.instance?.addToRequestQueue(stringRequest)
//        //volley interactions end
//    }

}