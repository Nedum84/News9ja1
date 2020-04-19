package com.news9Ja

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import android.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import java.util.*


class ClassDownloadDataFromServer(var context: Context) {
    var newsListDBHelper : SQLiteNewsDBHelper = SQLiteNewsDBHelper(context)
    private var newsList: MutableList<NewsListClassBinder> = mutableListOf()
//    val newsTitles : MutableList<String> = mutableListOf()
    private var progressDialog:ClassProgressDialog = ClassProgressDialog(context,"Setting App for the first time...")

    private var callbackInterface: DataDownloadCallbackInterface? = null
    init {
        try {
            callbackInterface = context as DataDownloadCallbackInterface
        } catch (e: ClassCastException) {
//            throw RuntimeException(context.toString() + "Activity must implement ListAdapterCallbackInterface.", e)
        }
    }

    fun downLoadNews(first_time:Boolean = false) {
        if (first_time)
            progressDialog.createDialog()

        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS,
                Response.Listener<String> { response ->

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val newsDev = obj.getJSONArray("all_news_arrayz")
                            val newsNo = newsDev.length()


                            if ((newsNo!=0)){//something was returned
                                for (i in 0 until newsNo) {
                                    val eachDevotion = newsDev.getJSONObject(i)

//                                    skip if the new sources or news cat is not saved yet in sqlite
                                    if (newsListDBHelper.readNewsListDetails(eachDevotion.getInt("news_list_id")).size == 0
                                            || newsListDBHelper.readCategoryDetails(eachDevotion.getInt("news_category")).size == 0)continue
                                    //Checking if news sources and categories has been removed
                                    if (newsListDBHelper.readNewsListDetails(eachDevotion.getInt("news_list_id"))[0][3].toInt() == 0
                                            || newsListDBHelper.readCategoryDetails(eachDevotion.getInt("news_category"))[0][2].toInt() == 0)continue
//                                  Skip if already deleted...
                                    if(eachDevotion.getString("news_id") !in newsListDBHelper.readDeletedNewsIds()){

                                        val addEachNewsRow = NewsListClassBinder(
                                                1,
                                                eachDevotion.getInt("news_id"),
                                                eachDevotion.getInt("news_list_id"),
                                                eachDevotion.getInt("news_category"),
                                                eachDevotion.getString("news_url"),
                                                eachDevotion.getString("news_title"),
                                                eachDevotion.getString("news_img_urls"),
                                                eachDevotion.getString("news_content_body"),
                                                eachDevotion.getInt("news_date_int"),
                                                eachDevotion.getInt("news_no_of_views"),
                                                eachDevotion.getInt("news_no_of_comments"),
                                                0
                                        )
                                        if (ClassSharedPreferencesSettings(context).getIsNewsLoadedOffline()){//offline mode
                                            if (!newsListDBHelper.checkIfNewsExist(eachDevotion.getInt("news_id"))){
                                                //insert news
                                                newsListDBHelper.insertNews(addEachNewsRow)
                                            }else{
                                                //update already added news
//                                                newsListDBHelper.updateNewsRow(addEachNewsRow)
                                            }
                                        }


                                        newsList.add(addEachNewsRow)//for the lastest news
                                    }

                                }

                                newsList = ClassUtilities().descOrder(newsList)//re arrange in desc order
//                          for server  json response
                                saveServerJsonResponse()

                                //FOR NOTIFICATION
                                if((System.currentTimeMillis()/1000)-ClassSharedPreferences(context).getLastRefreshTimeForNotification() > 1200)// is it more than 20 MINS?
                                    if(ClassDateAndTime().dayTimeCheck())
                                        sendNotification()




                                ClassSharedPreferences(context).setLastNewsId(newsList[0].news_id)//For last News Id
                            }
                        } else {
                            Toast.makeText(context, "An error occurred while loading data... ", Toast.LENGTH_SHORT).show()
                        }

                        if(first_time){
                            progressDialog.dismissDialog()
                            ClassAlertDialog(context).toast("Set Up finished...")
                            ClassSharedPreferencesSettings(context).setOpeningForTheFirstTime(false)
                            callbackInterface?.onReload()
                        }
                    } catch (e: JSONException) {
                        progressDialog.dismissDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { _ ->
                    if(first_time)progressDialog.dismissDialog()

                    if(first_time){
                        AlertDialog.Builder(context)
                                .setTitle("Network Error!")
                                .setMessage("Enable Internet Connection and tap on RETRY to set up the app for the first time")
                                .setPositiveButton("RETRY"
                                ) { dialog, id ->
                                    downLoadNews(true)

                                }.setNegativeButton("CANCEL"
                                ) { _, _ ->
                                    ClassAlertDialog(context).toast("Set Up is required")
                                    callbackInterface?.onFirstTimeErrorShow()
                                }.setCancelable(false)
                                .show()
                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "download_news"
                params["get_latest"] = "1"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }

    private fun sendNotification() {
        if(newsList.size == 0)return
        if(newsList[0].news_id<=ClassSharedPreferences(context).getLastNewsId())return



        if(ClassSharedPreferencesSettings(context).getNotificationStatus()){
            if(ClassSharedPreferencesSettings(context).getNotificationMode() == 1) {//0=single or 1=batch

                //call batch notification
                ClassNotification(context).sendNewsUpdates()
//                ImgDownloadForNotification(context,"${newsList[0].news_title}","${newsList[0].news_id}", newsList[0].news_list_id).execute("${newsList[0].news_img_urls}")
            }else{
                for (i in 0 until newsList.size) {
                    val eachNews = newsList[i]
                    ImgDownloadForNotification(context,"${eachNews.news_title}","${eachNews.news_id}",eachNews.news_list_id).execute("${eachNews.news_img_urls}")


                    if(i==0)// or 1(for two updates)
                        break
                }
            }
        }

    }

    fun saveServerJsonResponse(){
        if(newsList.size == 0)return

//        val dList = ClassUtilities().descOrder(newsList)
        val jsonText = Gson().toJson(newsList)
        ClassSharedPreferences(context).setSavedServer(jsonText)
    }



    fun downLoadNewsListAndNewsCategory(first_time:Boolean = false){
        if (first_time)
            progressDialog.createDialog()
        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS_LIST_AND_LIST_CATEGORY,
                Response.Listener<String> { response ->

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val noOfNewsList = obj.getJSONArray("news_list_arrayzs")
                            val noOfNewsCategory = obj.getJSONArray("news_category_arrayzs")

                            //for news list
                            for (i in 0 until noOfNewsList.length()) {
                                val eachNewsList = noOfNewsList.getJSONObject(i)

                                if (newsListDBHelper.readNewsListDetails(eachNewsList.getInt("news_list_id")).size == 0){

                                    newsListDBHelper.insertNewsIntoTheList(
                                            eachNewsList.getInt("news_list_id"),
                                            eachNewsList.getString("news_website"),
                                            eachNewsList.getString("news_name"),
                                            eachNewsList.getInt("arrangement_order")
                                        )
                                }else{
                                    newsListDBHelper.updateNewsInTheNewsList(
                                            eachNewsList.getInt("news_list_id"),
                                            eachNewsList.getString("news_website"),
                                            eachNewsList.getString("news_name"),
                                            eachNewsList.getInt("arrangement_order")
                                        )

                                }
                            }
                            //for news category
                            for (k in 0 until noOfNewsCategory.length()) {
                                val eachNewsCategory = noOfNewsCategory.getJSONObject(k)

                                if (SQLiteNewsDBHelper(context).readCategoryDetails(eachNewsCategory.getInt("cat_id")).size == 0){

                                    newsListDBHelper.insertCategory(
                                            eachNewsCategory.getInt("cat_id"),
                                            eachNewsCategory.getString("cat_name"),
                                            eachNewsCategory.getInt("arrangement_order")
                                    )
                                }else{
                                    newsListDBHelper.updateCategory(
                                            eachNewsCategory.getInt("cat_id"),
                                            eachNewsCategory.getString("cat_name"),
                                            eachNewsCategory.getInt("arrangement_order")
                                    )

                                }
                            }

                        } else {
                            Toast.makeText(context, "An error occurred while loading data... ", Toast.LENGTH_LONG).show()
                        }

                        if(first_time){
                            downLoadNews(true)
                        }
//                        for the next download time
                        ClassSharedPreferences(context).setTimeForCatAndNewsCatDownload()//for next download time (12hrs)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progressDialog.dismissDialog()
                    }
                },
                Response.ErrorListener { _ ->
                    if(first_time)progressDialog.dismissDialog()

                    if(first_time){
                        AlertDialog.Builder(context)
                                .setTitle("Network Error!")
                                .setMessage("Enable Internet Connection and tap on RETRY to set up the app for the first time")
                                .setPositiveButton("Retry"
                                ) { dialog, id ->
                                    downLoadNewsListAndNewsCategory(true)

                                }.setNegativeButton("Cancel"
                                ) { _, _ ->
                                    ClassAlertDialog(context).toast("Set Up is required")
                                    callbackInterface?.onFirstTimeErrorShow()
                                }.setCancelable(true)
                                .show()
                    }

                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "download_news_list_and_news_category"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }

    fun numberOfUnread(intentReceiver:Intent) {

        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS,
                Response.Listener<String> { response ->

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val noOfUnread = obj.getInt("no_of_unread")

                            intentReceiver.putExtra("resultCode", Activity.RESULT_OK)
                            intentReceiver.putExtra("resultValue", noOfUnread.toString())
                            // Fire the broadcast with intent packaged
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intentReceiver)
                        } else {
//                            Toast.makeText(context, "An error occurred while loading data... ", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { _ ->

                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "number_of_unread"
                params["last_news_id"] = ClassSharedPreferences(context).getLastNewsId().toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }

    fun downLoadBitmap(){
        val newsLists = newsListDBHelper.readAllNewsList(1,"category")
        for (newsRow: NewsListClassBinder in newsLists) {
            if (!newsListDBHelper.checkIfNewsImgExist(newsRow.news_id)&&(newsRow.news_id==61||newsRow.news_id==62)){
//                getBitmapFromURL(newsRow.news_img_urls,newsRow.news_id)
                ImageBitmapDownload(context, newsRow.news_id).execute(newsRow.news_img_urls)
            }
        }

    }

    //interface declaration
    interface DataDownloadCallbackInterface {
        fun onReload()
        fun onFirstTimeErrorShow()
    }

}