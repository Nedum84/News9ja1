package com.news9Ja

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.inc_news_recycler.*
import kotlinx.android.synthetic.main.activity_list_news_category.*
import kotlinx.android.synthetic.main.inc_network_error_page.*
import org.json.JSONException
import org.json.JSONObject



class ActivityListNewsCategory : ActivityBaseActivity(), ListAdapter.ListAdapterCallbackInterface  {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder>? = mutableListOf()
    var start_page_from:String = "0"
    var isLoadingDataFromServer = false  //for checking when data fetching is going on

    val linearLayoutManager = LinearLayoutManager(this)
    lateinit var ADAPTER : ListAdapter
    var currentCategoryId:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_news_category)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        Slidr.attach(this)//for slidr swipe lib

        newsListDBHelper = SQLiteNewsDBHelper(this)
        getCatDetailsFromNotification()//Get news details from notification tray

        //load data...
        initialize()
        loadFromSavedJson()

        news_list_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // only load more items if it's currently not loading
                if (!isLoadingDataFromServer) {
                    if(!recyclerView.canScrollVertically(1)){//1->bottom, -1 ->top
                        isLoadingDataFromServer = true
                        loadNewsData(true)//loading questions again
                    }
                }
            }
        })
//        swipe to refresh news
        swipe_to_refresh?.setOnRefreshListener {
            swipe_to_refresh?.isRefreshing = false
            refreshData()
        }

        refreshPageFromNw.setOnClickListener {
            refreshData()
        }
    }
    private fun getCatDetailsFromNotification(){
        val intent = intent
        if (intent.hasExtra("notify_cat_id")){
            val notify_cat_id = intent.extras!!.getString("notify_cat_id")
            ClassSharedPreferences(this).setCurrentCategoryId(notify_cat_id!!.toInt())

        }
    }
    private fun initialize(){
        currentCategoryId   = ClassSharedPreferences(this).getCurrentCategoryId()
        toolbar?.title =if(currentCategoryId !=0) newsListDBHelper.readCategoryDetails(currentCategoryId)[0][1]
        else "Latest News"//for latest news

        //for latest news to show category
        val fragPos = if(currentCategoryId !=0)2222;else 1111//1111 ->for latest news

        ADAPTER = ListAdapter(newsList!!,this, fragPos)
        news_list_recycler.layoutManager = linearLayoutManager
        news_list_recycler.itemAnimator = DefaultItemAnimator()
        news_list_recycler.adapter = ADAPTER
    }
    private fun refreshData(){
        nwErrorPageWrapper?.visibility = View.GONE
        initialize()
        newsList!!.clear()
        ADAPTER.addItems(newsList!!)
        ADAPTER.notifyDataSetChanged()
        loadNewsData()
    }
    private fun loadNewsData(isReloading:Boolean = false){
        start_page_from = if(newsList!!.size==0){ "0" }else{ newsList!!.map { it.news_id }.sortedDescending().last().toString() }


        if (ClassSharedPreferencesSettings(this).getIsNewsLoadedOffline()){
            val newsData =if(currentCategoryId !=0) newsListDBHelper.readAllNewsList(currentCategoryId,"category",start_page_from.toInt())
                                                    else newsListDBHelper.readAllNewsList(currentCategoryId,"all_news",start_page_from.toInt())//for latest news
//            if(data_array.size ==0 && linearLayoutManager.itemCount == 0)
            if(newsData.size ==0 && newsList!!.size == 0){
                no_data_tag.visibility = View.VISIBLE
            }else{
                no_data_tag.visibility = View.GONE
                ADAPTER.addItems(newsData)
            }
            isLoadingDataFromServer = false
        }else{
            loadNewsFromServer(isReloading)
        }

    }
    private fun loadFromSavedJson(){
        val newsDetails = ClassSharedPreferences(this).getSavedServer()
        if(newsDetails !=""){
            var dataArray = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()
            dataArray = dataArray.distinctBy { it.news_id to it.news_date_int } as MutableList//OR Pair(it.news_id, it.news_date_int) OR listOf(...)  //for removing distincts

            var newData = mutableListOf<NewsListClassBinder>()
            for (i in 0 until dataArray.size) {
                val eachNews = dataArray[i]
                if(eachNews.news_category != currentCategoryId&&currentCategoryId != UrlHolder.LATEST_CAT_ID) continue
                if(eachNews in newsList!!)continue


                newData.add(eachNews)
            }


            if(newData.isEmpty() && newsList!!.size == 0){
                no_data_tag.visibility = View.VISIBLE
            }else{
                newData = if(newData.size>20)newData.subList(0, 20)else newData//20 news from JSON data

                no_data_tag.visibility = View.GONE
                ADAPTER.addItems(newData)
            }

        }else{
            nwErrorPageWrapper.visibility = View.VISIBLE
        }
        isLoadingDataFromServer = false
    }

    private fun loadNewsFromServer(isReloading:Boolean = false){
        val pDialog = ClassProgressDialog(this,"Loading, please wait...")
        if (!isReloading)pDialog.createDialog()

        //creating volley string request
        loadingProgressbar?.visibility = View.VISIBLE
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS,
                Response.Listener<String> { sub ->
                    pDialog.dismissDialog()
                    loadingProgressbar?.visibility = View.GONE
                    nwErrorPageWrapper?.visibility = View.GONE
                    isLoadingDataFromServer = false

                    try {
                        val obj = JSONObject(sub)
                        if (!obj.getBoolean("error")) {
                            val noOfSub = obj.getJSONArray("all_news_arrayz")
                            if ((noOfSub.length()!=0)){
                                no_data_tag?.visibility = View.GONE

                                val q_data_array = mutableListOf<NewsListClassBinder>()
                                for (i in 0 until noOfSub.length()) {
                                    val newsDetails = noOfSub.getJSONObject(i)

                                    if (currentCategoryId ==0 &&newsListDBHelper.readCategoryDetails(newsDetails.getInt("news_category"))[0][2].toInt() == 0){//skip removed cats
                                    }else{
                                        if(newsDetails.getString("news_id") !in newsListDBHelper.readDeletedNewsIds()){//skip if already deleted...
                                            q_data_array.add(NewsListClassBinder(
                                                    1,
                                                    newsDetails.getInt("news_id"),
                                                    newsDetails.getInt("news_list_id"),
                                                    newsDetails.getInt("news_category"),
                                                    newsDetails.getString("news_url"),
                                                    newsDetails.getString("news_title"),
                                                    newsDetails.getString("news_img_urls"),
                                                    newsDetails.getString("news_content_body"),
                                                    newsDetails.getInt("news_date_int"),
                                                    newsDetails.getInt("news_no_of_views"),
                                                    newsDetails.getInt("news_no_of_comments"),
                                                    newsListDBHelper.getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(newsDetails.getInt("news_id")))//1->bookmarked, 0-> otherwise
                                            ))

                                        }
                                    }
                                    if (q_data_array.size >=10)break//fetch only 10
                                }
                                ADAPTER.addItems(q_data_array)


                            }else if(!isReloading){
                                no_data_tag?.visibility = View.VISIBLE
                            }else{
                                Toast.makeText(this, "No more data...", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "An error occurred while loading data", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { _ ->
                    pDialog.dismissDialog()
                    loadingProgressbar?.visibility = View.GONE
                    isLoadingDataFromServer = false
                    loadFromSavedJson()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "download_news"
                params["start_page_from"] = start_page_from
                params["cat_id"] = currentCategoryId.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end

    }

    //remove the loading progress dialog
    override fun onRemoveDialog() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.news_cat, menu)

        val categoryFilterMenu = menu.findItem(R.id.menu_cat_filter).subMenu
        categoryFilterMenu?.clear() // delete place holder

        categoryFilterMenu?.add(
                339,      //grp id
                0,          //item id
                0,      //Item order
                "Latest News"   //Item Title
        )?.setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
//        for category
        val newsCats = newsListDBHelper.readCategoryDetails()
        for (element in newsCats) {
//                element[0][2].toInt() != 0//Skipp for cat
//                element[0][3].toInt() != 0//Skipp for news sources

            categoryFilterMenu?.add(
                    339,      //grp id
                    element[0].toInt(),          //item id
                    0,      //Item order
                    element[1]   //Item Title
            )?.setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
        }
        return true
    }
    //action on the dot dot menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.groupId) {
            339 -> {//for news cat
                ClassSharedPreferences(this).setCurrentCategoryId(item.itemId)
                refreshData()
            }
            0 ->{
                when(item.itemId){
                    android.R.id.home -> {super.onBackPressed();return true }
                    R.id.menu_settings->{
                        startActivity(Intent(this, ActivitySettings::class.java))
                    }
                    R.id.menu_refresh -> refreshData()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
