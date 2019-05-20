package com.nigernewsheadlines

import android.app.AlertDialog
import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View

class ClassProgressDialog(var context: Context?, var text:String?="Please Wait...", var cancelable:Boolean = false) {
    private  val alertDialog:AlertDialog
    var builder:AlertDialog.Builder = AlertDialog.Builder(context)
    var dialogView:View = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null)
    var message:TextView

    init {
        //Alert Dialog declaration starts
        message = dialogView.findViewById(R.id.message)
        message.text = text
        builder.setView(dialogView)
        builder.setCancelable(cancelable)
        alertDialog = builder.create()
    }

    fun createDialog(){
//        dialogBuilder.create().show()
        alertDialog.show()
    }
    fun dismissDialog(){
        alertDialog.dismiss()
    }


}