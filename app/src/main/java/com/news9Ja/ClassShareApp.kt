package com.news9Ja

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


class ClassShareApp(val context: Context) {
    private var newsListDBHelper : SQLiteNewsDBHelper
//    private lateinit var getNewsRow:NewsListClassBinder

    private val appPackageName:String? = context.packageName // getPackageName() from Context or Activity object
    val intent :Intent = Intent()
    private val shareAppMsg:String =
            "Get \"${context.getString(R.string.app_name)}\" on Google Play Store via https://play.google.com/store/apps/details?id=$appPackageName for real time updates from all the Nigerian Newspapers/Blogs"
    private val readMoreMessage:String =
            "Get \"${context.getString(R.string.app_name)}\" on Google Play Store via \n" +
                    "https://play.google.com/store/apps/details?id=$appPackageName & get Real time updates from all the Nigerian Newspapers/Blogs"
    init {
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        newsListDBHelper = SQLiteNewsDBHelper(context)
    }
    fun shareApp(){
        intent.putExtra(Intent.EXTRA_TEXT,shareAppMsg)
        startActivity(context, Intent.createChooser(intent,"Share to: "), Bundle())
    }
    private fun getNews2Share(dNews: NewsListClassBinder) {
        var devShareMsg:String?
        devShareMsg = "${context.getString(R.string.app_name)} \n\n"
        devShareMsg += ClassDateAndTime().getDateTime2(dNews.news_date_int!!.toLong())+"\n"
        devShareMsg += ClassHtmlFormater().fromHtml(dNews.news_title?.toUpperCase()).toString()+"\n"
        devShareMsg += ClassHtmlFormater().fromHtml(dNews.news_content_body).toString()+"\n\n"
        devShareMsg+= "*SOURCE: _${newsListDBHelper.readNewsListDetails(dNews.news_list_id)[0][1]}_*\n\n"

        devShareMsg+= shareAppMsg

        intent.putExtra(Intent.EXTRA_TEXT,devShareMsg)
        startActivity(context, Intent.createChooser(intent,"Share to: "), Bundle())
    }
    fun shareNewsPost(dNews: NewsListClassBinder){
        var newsBody = ClassHtmlFormater().fromHtml(dNews.news_content_body).toString()
        newsBody = if (newsBody.length>501) newsBody.substring(0,500)+"..." else newsBody

        var shareMsg:String? = ""
//        shareMsg += "*${context.getString(R.string.app_name)}* \n\n"
        shareMsg += "*Breaking News* \n\n"
        shareMsg += ClassDateAndTime().getDateTime2(dNews.news_date_int!!.toLong())+"\n"
        shareMsg += ClassHtmlFormater().fromHtml(dNews.news_title?.toUpperCase()).toString()+"\n\n"
        shareMsg += newsBody+"\n\n"
//        shareMsg+= "*SOURCE: _${newsListDBHelper.readNewsListDetails(dNews.news_list_id)[0][1]}_*\n\n"

        shareMsg += "*READ MORE:* ${context.getString(R.string.app_web_url)}/${dNews.news_id}-html\n\n"

//        shareMsg+= shareAppMsg


        intent.putExtra(Intent.EXTRA_TEXT, shareMsg)
        startActivity(context, Intent.createChooser(intent,"Share on: "), Bundle())
    }
    fun shareNewsOfDday(){
        val newsDetails = ClassSharedPreferences(context).getSavedServer()
        if(newsDetails !="") {
            val dataArray = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()

            var dBody = "Here are things you need to know today ${ClassDateAndTime().getDateTime2()}...\n\n"
            dBody +=  "*${newsQuotes()}*\n\n"

            dBody += "*INFORMATION IS POWER*\n\n"
            var newsTitles = ""
            var counter = 1
            for(i in 0 until dataArray.size){
                val n = dataArray[i]
                if(ClassDateAndTime().getDateTime2() != ClassDateAndTime().getDateTime2(n.news_date_int!!.toLong()))continue//date is today

                val nUrl = if(counter==1){"http://news9ja.online"}else{"news9ja.online"}
                newsTitles += "$counter. ${ClassHtmlFormater().fromHtml(n.news_title) }($nUrl/${n.news_id}-html)\n\n"

                counter++
                if(counter>=80)break
                else continue
            }

            dBody +=newsTitles+"\n\n"
//            dBody +=readMoreMessage

            if(newsTitles ==""){
                ClassAlertDialog(context).toast("Today's News is not available at this moment")
            }else{
                intent.putExtra(Intent.EXTRA_TEXT, dBody)
                startActivity(context, Intent.createChooser(intent,"Share on: "), Bundle())
            }
        }else{
            ClassAlertDialog(context).toast("No available News at this moment")
        }
    }

    private fun newsQuotes():String{
        val quoteArrays = mutableListOf<String>()
        quoteArrays.add("Good journalism costs a lot of money. Yet only good journalism can ensure the possibility of a good society, an accountable democracy, and a transparent government.")
        quoteArrays.add("When action grows unprofitable, gather information; when information grows unprofitable, sleep.")
        quoteArrays.add("Good journalism costs a lot of money. Yet only good journalism can ensure the possibility of a good society, an accountable democracy, and a transparent government.")

        return quoteArrays.shuffled().last()
    }
    fun openInBrowser(urlString:String = context.getString(R.string.app_web_url)){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null)
            context.startActivity(intent)
        }

    }

}