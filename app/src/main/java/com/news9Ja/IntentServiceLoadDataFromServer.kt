package com.news9Ja

import android.app.IntentService
import android.content.Intent


@Suppress("DEPRECATION")

class IntentServiceLoadDataFromServer : IntentService("IntentServiceLoadDataFromServer"), ClassDownloadDataFromServer.DataDownloadCallbackInterface {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    lateinit var settingsPreferences : ClassSharedPreferencesSettings

    override fun onCreate() {
        newsListDBHelper = SQLiteNewsDBHelper(this)
        settingsPreferences = ClassSharedPreferencesSettings(this)

//        Thread.setDefaultUncaughtExceptionHandler(MyThreadUncaughtExceptionHandler())
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        androidx.legacy.content.WakefulBroadcastReceiver.completeWakefulIntent(intent)

//        Log.d("Background_Service",(10..99).shuffled().last().toString())
        if((System.currentTimeMillis()/1000)-ClassSharedPreferences(this).getTimeForCatAndNewsCatDownload() >= 43200)// is it more than 12HRS?
            ClassDownloadDataFromServer(this).downLoadNewsListAndNewsCategory()


        if (ClassNetworkStatus(this).isNetworkAvailable())
            ClassDownloadDataFromServer(this).downLoadNews()//news download and updates...

        if (settingsPreferences.getAutoDownloadNewsImgStatus()){//Image Download
            ClassDownloadDataFromServer(this).downLoadBitmap()
        }


        //for deleting old news
        deleteOldNews()
    }
    private fun deleteOldNews(){
        val systemSeconds = System.currentTimeMillis()/1000
        val nextDelDate = settingsPreferences.getNextNewsDeleteInterval()
        if (systemSeconds >= nextDelDate){
            //delete all news
            val allNews = newsListDBHelper.readAllNewsList(0,"all_the_news")
            for(i in allNews){
                if (i.news_is_bookmarked == 1)continue
                newsListDBHelper.deleteNews(i.news_id.toString())
            }

            settingsPreferences.setNextNewsDeleteInterval((System.currentTimeMillis()/1000)+(settingsPreferences.getNewsDeleteInterval()*3600))
    }



//        val set = mutableListOf("Obi","Ada","Adaka","Adaora","Adamma","Monica...")
//        set.addAll(ClassSharedPreferences(this).getNewsTitleForNotification())
//        ClassSharedPreferences(this).setNewsTitleForNotification(HashSet(set))
//        ClassNotification(this).sendNewsUpdates()
//        ClassNotification(this).createNotification("President Buhari is coming here","Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod\n" +
//                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\n" +
//                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo\n" +
//                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse\n" +
//                "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non\n" +
//                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.","http://192.168.44.236/news/cross2.jpg",this,12,"57")


//        val newsDetails = ClassSharedPreferences(this).getSavedServer()
//        val data_array = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()
//        var counter = 0
//        for (i in 40 until data_array.size) {
////            val eachNews = data_array[i]
//            val eachNews = data_array[(1..70).shuffled().last()]
//            ClassNotification(this).sendNewsUpdatesSingle("${eachNews.news_id}. ${eachNews.news_title}","${eachNews.news_img_urls}","${eachNews.news_id}")
//
//            counter++
//            if (counter >=1)break//fetch only 1
//        }
    }


    override fun onReload() {
//        finish()
//        startActivity(intent)
    }

    override fun onFirstTimeErrorShow() {
//        tapToSetup.visibility  = View.VISIBLE
    }
}

