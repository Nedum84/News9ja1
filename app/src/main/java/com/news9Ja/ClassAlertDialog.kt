package com.news9Ja

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View


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
    fun rateApp(){
        AlertDialog.Builder(context)
                .setView(LayoutInflater.from(context).inflate(R.layout.alert_dialog_inflate_rate_app, null))
                .setPositiveButton("Rate Now"
                ) { _, _ ->
                    //actions
                    redirectToPlayStore()
                }
                .setNegativeButton("Later"
                ) { dialog, id ->

                }.setCancelable(true)
                .show()
    }
    fun alertMessage(displayMsg:String){
        AlertDialog.Builder(context)
                .setMessage(displayMsg)
                .setPositiveButton("Ok"
                ) { dialog, id ->
                }.setCancelable(false)
                .show()
    }
    fun snackBarMsg(view:View,msg :String= "No Internet Connection"){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    fun toast(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}