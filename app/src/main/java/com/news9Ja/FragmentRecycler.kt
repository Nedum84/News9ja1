package com.news9Ja


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.json.JSONException
import org.json.JSONObject


class FragmentRecycler : Fragment() {
    var listener: FragmentRecyclerInteractionListener? = null
    private lateinit var thisContext: Context

    val linearLayoutManager = LinearLayoutManager(activity)
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder>? = mutableListOf()

    lateinit var ADAPTER : ListAdapter
    private var FRAG_POS = 0
    private var CAT_ID = 0
//    var firstTimeChecker = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        newsListDBHelper = SQLiteNewsDBHelper(activity!!.applicationContext)
        thisContext = activity!!

        ADAPTER = ListAdapter(newsList!!, thisContext, FRAG_POS)
        news_list_recycler?.layoutManager = linearLayoutManager
        news_list_recycler?.itemAnimator = DefaultItemAnimator()
        news_list_recycler?.adapter = ADAPTER

        runnableBgService()



        //checking for already sent argument
        if(arguments != null){
            val cat_name = arguments?.getCharSequence("cat_name").toString()
            CAT_ID = arguments!!.getInt("cat_id")
            FRAG_POS = arguments!!.getInt("frag_position")

//            Initialize Loading cat news from sqlite or from server
            loadFromSavedJson()
        }

        news_list_recycler.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){//1->bottom, -1 ->top
//                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() >= linearLayoutManager.itemCount - 1 ) {
                    if (newsList!!.size >=10)loadMore.visibility = View.VISIBLE
                    else loadMore.visibility = View.GONE
                }else if(!recyclerView.canScrollVertically(-1)){
                    loadMore.visibility = View.GONE
                }
            }
        })

//        swipe to refresh news
        swipe_to_refresh?.setOnRefreshListener {
            checkMoreNews.visibility = View.GONE

            swipe_to_refresh?.isRefreshing = false
            loadCategoryNewsInitialization()
        }
        no_data_tag.setOnClickListener {
            checkMoreNews.visibility = View.GONE

            swipe_to_refresh?.isRefreshing = false
            loadCategoryNewsInitialization()
        }
        loadMore.setOnClickListener {
            ClassSharedPreferences(thisContext).setCurrentCategoryId(CAT_ID)
            startActivity(Intent(thisContext, ActivityListNewsCategory::class.java))
        }


        checkMoreNews.setOnClickListener {
            checkMoreNews.visibility = View.GONE
            loadFromSavedJson()
        }
    }

    private fun loadCategoryNewsInitialization(){

        if (ClassSharedPreferencesSettings(context).getIsNewsLoadedOffline()){
            if (CAT_ID == 0)getNewsList(newsListDBHelper.readAllNewsList(CAT_ID,"all_news"))
            else getNewsList(newsListDBHelper.readAllNewsList(CAT_ID,"category"))
        }else{
            loadNewsFromServer()
        }
    }
    fun refreshAdapter(){
            newsList!!.clear()
        try {
            ADAPTER.notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }
    private  fun loadFromSavedJson(){
        val newsDetails = ClassSharedPreferences(thisContext).getSavedServer()
        if(newsDetails !=""){

            val dataArray = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()

            for (i in 0 until dataArray.size) {
                val eachNews = dataArray[i]
                if(eachNews.news_category != CAT_ID&&CAT_ID != UrlHolder.LATEST_CAT_ID) continue
                if(eachNews in newsList!!)continue


                newsList!!.add(eachNews)
                if (newsList!!.size >=10)break//fetch only 10
            }
        }else no_data_tag.visibility = View.VISIBLE


        getNewsList(newsList!!)
    }


    private fun getNewsList(newsLists: MutableList<NewsListClassBinder>) {
        val newData = newsLists.distinctBy { it.news_id to it.news_date_int } as MutableList//OR Pair(it.news_id, it.news_date_int) OR listOf(...)  //for removing distincts
        if (newsLists.size == 0){
            no_data_tag.visibility = View.VISIBLE
        }else{
            no_data_tag.visibility = View.GONE
            refreshAdapter()
            LoadNews(ADAPTER).execute(newData)
        }
    }


    private fun loadNewsFromServer(){
        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_DOWNLOAD_NEWS,
                Response.Listener<String> { sub ->

                    try {
                        val obj = JSONObject(sub)
                        if (!obj.getBoolean("error")) {
                            val newsArrays = obj.getJSONArray("all_news_arrayz")
                            val noOfNews = newsArrays.length()

                            if ((noOfNews!=0)){
                                val newData = mutableListOf<NewsListClassBinder>()

                                for (i in 0 until noOfNews) {
                                    val eachNews = newsArrays.getJSONObject(i)
                                    if(eachNews.getInt("news_category") != CAT_ID&&CAT_ID != UrlHolder.LATEST_CAT_ID) continue//only this cat or go only for latest news

//                                    skip if the new sources or news cat is not saved yet in sqlite
                                    if (newsListDBHelper.readNewsListDetails(eachNews.getInt("news_list_id")).size == 0
                                            || newsListDBHelper.readCategoryDetails(eachNews.getInt("news_category")).size == 0)continue
                                    //Checking if news sources and categories has been removed
                                    if (newsListDBHelper.readNewsListDetails(eachNews.getInt("news_list_id"))[0][3].toInt() == 0
                                            || newsListDBHelper.readCategoryDetails(eachNews.getInt("news_category"))[0][2].toInt() == 0)continue

                                    //                      Skip if already deleted...
                                    if(eachNews.getString("news_id") !in newsListDBHelper.readDeletedNewsIds()){
                                        val addEachNewsRow = NewsListClassBinder(
                                                1,
                                                eachNews.getInt("news_id"),
                                                eachNews.getInt("news_list_id"),
                                                eachNews.getInt("news_category"),
                                                eachNews.getString("news_url"),
                                                eachNews.getString("news_title"),
                                                eachNews.getString("news_img_urls"),
                                                eachNews.getString("news_content_body"),
                                                eachNews.getInt("news_date_int"),
                                                eachNews.getInt("news_no_of_views"),
                                                eachNews.getInt("news_no_of_comments"),
                                                newsListDBHelper.getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(eachNews.getInt("news_id")))//1->bookmarked, 0-> otherwise,
                                                ,(0..1).shuffled().last()
                                        )

                                        newData.add(addEachNewsRow)
                                    }

                                    if (newData.size >=10)break//fetch only 10
                                }

                                if(newData.size!=0)
                                    getNewsList(newData)

                            }else{
//                                loadFromSavedJson()
                            }

                        } else {
//                            loadFromSavedJson()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {_ ->
                    //                    loadFromSavedJson()
//                    ClassAlertDialog(thisContext).toast("No Network...")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "download_news"
                params["cat_id"] = CAT_ID.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }











    //Object to bind the datas
    companion object {

        fun newInstance(name: CharSequence, value: Int, frag_position: Int): FragmentRecycler {

            val args = Bundle().apply {
                putCharSequence("cat_name", name)
                putInt("cat_id", value)
                putInt("frag_position", frag_position)
            }
            val fragment = FragmentRecycler()
            fragment.arguments = args
            return fragment
        }
    }


    //Fragment communication with the Home Activity Starts
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentRecyclerInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentIndexInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FragmentRecyclerInteractionListener {


        fun onRemoveTab(position: Int)
    }
    //Fragment communication with the Home Activity Stops










    val timerHandler = Handler()
    private val myTimerRunnable = object:Runnable {
        override fun run() {

            val newsDetails = ClassSharedPreferences(thisContext).getSavedServer()
            if(newsDetails !="") {
                val dataArray = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()

                var resultValue=0
                val noOfNews = dataArray.size
                for (i in 0 until noOfNews) {
                    val eachNews = dataArray[i]
                    if(eachNews.news_category != CAT_ID&&CAT_ID != UrlHolder.LATEST_CAT_ID) continue

                    if(newsList!!.size==0)
                        resultValue++
                    else if(eachNews.news_category > newsList!![0].news_id)
                        resultValue++
                }

                if(resultValue>1){
                    checkMoreNews?.visibility = View.VISIBLE
                    checkMoreNews?.text = "$resultValue+ more news"
                }else{
                    checkMoreNews?.visibility = View.GONE
                }
            }


            timerHandler.postDelayed(this, 15000)
        }
    }
    private fun runnableBgService(){
        timerHandler.postDelayed(myTimerRunnable,15000) // 1 second delay (takes millis)}
    }
    private fun destroyTimeHandler(){
        timerHandler.removeCallbacks(myTimerRunnable)
        timerHandler.removeCallbacks(null)
        timerHandler.removeCallbacksAndMessages(null)
    }

    // Setup the callback for when data is received from the service
    override fun onResume() {
        super.onResume()
        runnableBgService()
    }

    override fun onPause() {
        super.onPause()
        destroyTimeHandler()
    }
}

