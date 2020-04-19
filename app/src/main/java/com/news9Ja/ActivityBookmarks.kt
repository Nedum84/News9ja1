package com.news9Ja

import android.content.Intent
import android.os.Bundle
import android.view.*
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_bookmarks.*

class ActivityBookmarks : ActivityBaseActivity(), ListAdapter.ListAdapterCallbackInterface  {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder>? = mutableListOf()
    var start_page_from:String = "0"
    var isLoadingDataFromServer = false  //for checking when data fetching is going on

    val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    lateinit var ADAPTER : ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        Slidr.attach(this)//for slidr swipe lib

        newsListDBHelper = SQLiteNewsDBHelper(this)
        toolbar?.title = "Bookmarks"



        ADAPTER = ListAdapter(newsList!!,this)
        news_list_recycler.layoutManager = linearLayoutManager
        news_list_recycler.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        news_list_recycler.adapter = ADAPTER
        loadNewsData()

        news_list_recycler.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // only load more items if it's currently not loading
                if (!isLoadingDataFromServer) {
//                    if (loadingProgressbar?.visibility == View.VISIBLE) {
                    // only load more items if the last visible item on the screen is the last item
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() >= linearLayoutManager.itemCount - 1 ) {
//                        isLoadingDataFromServer = true
//                        loadNewsData(true)//loading questions again
                    }
                    if(!recyclerView.canScrollVertically(1)){//1->bottom, -1 ->top
//                        Toast.makeText(applicationContext, "Bottom reached...", Toast.LENGTH_SHORT).show()
                        isLoadingDataFromServer = true
                        loadNewsData()//loading questions again
                    }
                }
            }
        })
    }

    private fun loadNewsData(){
        start_page_from = if(newsList!!.size==0){
            "0"
        }else{
            newsList!!.last().news_id.toString()
        }
        no_data_tag.visibility = View.GONE

        val data_array = newsListDBHelper.readAllNewsList(-1,"news_bookmark",start_page_from.toInt())
        if(data_array.size ==0 && newsList!!.size == 0)
            no_data_tag.visibility = View.VISIBLE
        else
            ADAPTER.addItems(data_array)
        isLoadingDataFromServer = false
    }

    //remove the loading progress dialog
    override fun onRemoveDialog() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bookmarks, menu)
        return true
    }
    //action on the dot dot menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when(item.itemId){
            android.R.id.home -> {super.onBackPressed();return true }
            R.id.menu_settings->{
                startActivity(Intent(this, ActivitySettings::class.java))
            }
            R.id.menu_share_app->{
                ClassAlertDialog(this).rateApp()
            }
            R.id.menu_about_app->{
            startActivity(Intent(this, ActivityAboutApp::class.java))
        }
        }

        return super.onOptionsItemSelected(item)
    }
}
