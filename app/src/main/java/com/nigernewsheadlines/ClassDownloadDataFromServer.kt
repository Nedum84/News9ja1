package com.nigernewsheadlines

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.net.HttpURLConnection
import java.net.MalformedURLException


class ClassDownloadDataFromServer(var context: Context) {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    var bitmap: Bitmap? = null
    var inputStream: InputStream? = null
    var responseCode = -1

    fun downLoadNews() {
        newsListDBHelper = SQLiteNewsDBHelper(context)

        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS,
                Response.Listener<String> { response ->

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val noOfDev = obj.getJSONArray("all_news_arrayz")



                            for (i in 0 until noOfDev.length()) {
                                val eachDevotion = noOfDev.getJSONObject(i)

//                                val contains=newsListDBHelper.readDeletedNewsIds().contains(eachDevotion.getInt("news_id").toString())
                                if(eachDevotion.getString("news_id") !in newsListDBHelper.readDeletedNewsIds()){
                                    if (!newsListDBHelper.checkIfNewsExist(eachDevotion.getInt("news_id"))){
                                        //download img
                                        val addEachNewsRow = NewsListClassBinder(
                                                1,
                                                eachDevotion.getInt("news_id"),
                                                eachDevotion.getInt("news_list_id"),
                                                eachDevotion.getInt("news_category"),
                                                eachDevotion.getString("news_url"),
                                                eachDevotion.getString("news_title"),
                                                eachDevotion.getString("news_img_urls"),
//                                            getBitmapFromURL("gh"),
                                                eachDevotion.getString("news_content_body"),
                                                eachDevotion.getInt("news_date_int"),
                                                eachDevotion.getInt("news_no_of_views"),
                                                eachDevotion.getInt("news_no_of_comments"),
                                                0,
                                                0
                                        )
                                        newsListDBHelper.insertNews(addEachNewsRow)
                                    }else{
                                        //update already added news
                                        SQLiteNewsDBHelper(context).updateNewsRow(
                                                eachDevotion.getInt("news_id"),
                                                eachDevotion.getInt("news_list_id"),
                                                eachDevotion.getInt("news_category"),
                                                eachDevotion.getString("news_url"),
                                                eachDevotion.getString("news_title"),
                                                eachDevotion.getString("news_img_urls"),
                                                "",
                                                eachDevotion.getString("news_content_body"),
                                                eachDevotion.getInt("news_date_int"),
                                                eachDevotion.getInt("news_no_of_views"),
                                                eachDevotion.getInt("news_no_of_comments")
                                        )
                                    }

                                }

                                //setting the last news id preference
                                if(i == 0){
                                    ClassSharedPreferences(context).setCurrentNewsListId(eachDevotion.getInt("news_id"))
                                }

                            }
                        } else {
                            Toast.makeText(context, "An error occurred while loading data... ", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    //if empty, there's n/w failure
                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "download_news"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }
    fun downLoadNewsListAndNewsCategory(){
        newsListDBHelper = SQLiteNewsDBHelper(context)

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

                                if (SQLiteNewsDBHelper(context).readNewsListDetails(eachNewsList.getInt("news_list_id")).size == 0){

                                    newsListDBHelper.insertNewsIntoTheList(
                                            eachNewsList.getInt("news_list_id"),
                                            eachNewsList.getString("news_website"),
                                            eachNewsList.getString("news_name")
                                        )
                                }else{
                                    newsListDBHelper.updateNewsInTheNewsList(
                                            eachNewsList.getInt("news_list_id"),
                                            eachNewsList.getString("news_website"),
                                            eachNewsList.getString("news_name")
                                        )

                                }
                            }
                            //for news category
                            for (k in 0 until noOfNewsCategory.length()) {
                                val eachNewsCategory = noOfNewsCategory.getJSONObject(k)

                                if (SQLiteNewsDBHelper(context).readCategoryDetails(eachNewsCategory.getInt("cat_id")).size == 0){

                                    newsListDBHelper.insertCategory(
                                            eachNewsCategory.getInt("cat_id"),
                                            eachNewsCategory.getString("cat_name")
                                    )
                                }else{
                                    newsListDBHelper.updateCategory(
                                            eachNewsCategory.getInt("cat_id"),
                                            eachNewsCategory.getString("cat_name")
                                    )

                                }
                            }
                        } else {
                            Toast.makeText(context, "An error occurred while loading data... ", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    //if empty, there's n/w failure
                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params.put("request_type", "download_news_list_and_news_category")
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }

    fun getBitmapFromURL(srcUrl: String){
        val url: URL?
        try {
            url = URL(srcUrl)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.doOutput = true
            httpURLConnection.connect()
            responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream!!.close()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        newsListDBHelper.insertNewsImg(bitmap)
    }
    fun downImg(){
        val file: File
        val inputStream: InputStream
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        file = File(path, "DemoPicture.jpg")
        try {
            // Make sure the Pictures directory exists.
            path.mkdirs()

//            val url = URL("http://www.pacuss.com/images/savvy.jpg")
            val url = URL("http://192.168.43.168/news/banner.jpg")
            /* Open a connection to that URL. */
            val ucon = url.openConnection()

            /*
             * Define InputStreams to read from the URLConnection.
             */
            inputStream = ucon.getInputStream()

            val os = FileOutputStream(file)
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            os.write(data)
            inputStream.close()
            os.close()

        } catch (e: IOException) {
            Log.d("ImageManager", "Error: $e")
        }


    }

}