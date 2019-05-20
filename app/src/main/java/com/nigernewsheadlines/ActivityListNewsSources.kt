package com.nigernewsheadlines

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.adapter_design_newslist1.view.*
import kotlinx.android.synthetic.main.activity_list_news_sources.*
import java.util.ArrayList

class ActivityListNewsSources : AppCompatActivity() {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_news_sources)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        newsListDBHelper = SQLiteNewsDBHelper(this)
        loadNewsData()
    }
    private fun loadNewsData(){

        val currentNewsListId = ClassSharedPreferences(this).getCurrentNewsListId()
        toolbar?.title = newsListDBHelper.readNewsListDetails(currentNewsListId)[0][2]

        news_list_recycler.layoutManager = LinearLayoutManager(this)
//        news_list_recycler.layoutManager = GridLayoutManager(context,1)

        getNewsList(newsListDBHelper.readAllNewsList(1,"category"))
        Slidr.attach(this)//for slidr swipe lib
    }

    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.news_sources, menu)

//        val webFilterMenu = menu.addSubMenu("Website Filter").setIcon(R.drawable.ic_tune_black_24dp) //or
        val webFilterMenu = menu.findItem(R.id.menu_news_sources).subMenu
        webFilterMenu?.clear() // delete place holder

//        for web site filter

        val newsList = newsListDBHelper.readNewsListDetails()
        for (element in newsList) {
            webFilterMenu?.add(
                    335,      //grp id
                    element[0].toInt(),          //item id
                    0,      //Item order
                    element[2]   //Item Title
            )?.setIcon(R.drawable.ic_tune_black_24dp)
        }
        return true
    }

    //action on the dot dot menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.groupId) {
            335 -> {//for news list
                ClassSharedPreferences(this).setCurrentNewsListId(item.itemId)
                loadNewsData()
            }
            0 ->{
                when(item.itemId){
                    android.R.id.home -> {super.onBackPressed();return true }
                    R.id.menu_settings->{
                        
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
    private fun clearNewsListDataClassBinder() {
        val size = newsList!!.size
        if (size > 0) {
            for (i in 0 until size) {
                newsList?.removeAt(0)
            }
        }
    }
    private fun getNewsList(devotionLists: ArrayList<NewsListClassBinder>) {
        clearNewsListDataClassBinder()


        for (newsRow: NewsListClassBinder in devotionLists) {
            val getEachNewsRow = NewsListClassBinder(
                    newsRow.table_id,
                    newsRow.news_id,
                    newsRow.news_list_id,
                    newsRow.news_category,
                    newsRow.news_url,
                    newsRow.news_title,
                    newsRow.news_img_urls,
//                    newsRow.news_img_url_download_name,
                    newsRow.news_content_body,
                    newsRow.news_date_int,
                    newsRow.news_no_of_views,
                    newsRow.news_no_of_comments,
                    newsRow.news_hide,
                    newsRow.news_is_bookmarked
            )
            newsList!!.add(getEachNewsRow)
            news_list_recycler.adapter = ListAdapter(newsList!!, this)
        }
    }


    class ListAdapter(items:MutableList<NewsListClassBinder>, ctx: Context): RecyclerView.Adapter<ListAdapter.viewHolder>(){

        private var bgColorList = mutableListOf("#d9b51c","#cd8313","#0067A3","#519839","#F6696C","#0079BF","#064e71","#84499B")
        private var list = items
        var context = ctx
        var newsListDBHelper = SQLiteNewsDBHelper(context)
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): viewHolder {
            return viewHolder(LayoutInflater.from(context).inflate(
                    R.layout.adapter_design_newslist3,
                    parent,
                    false
            ))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
        override fun onBindViewHolder(holder: viewHolder, position: Int) {

            val newsDetails = list[position]
            holder.headlineNewsTitle.text     = ClassHtmlFormater().fromHtml(newsDetails.news_title)
            holder.headlineNewsSource.text = newsListDBHelper.readNewsListDetails(newsDetails.news_list_id)[0][1]
            holder.headlineNewsDate.text     = ClassDateAndTime().getDateTime(newsDetails.news_date_int!!.toLong())
//            holder.headlineNewsNoOfViews.text     = newsDetails.news_no_of_views.toString()
            holder.headlineNewsNoOfViews.text     = newsDetails.news_category.toString()

            if (newsListDBHelper.checkIfNewsImgExist(newsDetails.news_id)){
                holder.headlineNewsImg.setImageBitmap(newsListDBHelper.getNewsImage(newsDetails.news_id))
            }else{
                Glide.with(context)
                        .load(newsDetails.news_img_urls)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.ic_tune_black_24dp)//default image on loading
                                .error(R.drawable.ic_clear_white_24dp)//without n/w, this img shows
                                .dontAnimate()
                                .fitCenter()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .priority(Priority.HIGH)
                        )
                        .thumbnail(.1f)
                        .into(holder.headlineNewsImg)
            }

            if (newsDetails.news_is_bookmarked == 1){
                holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
            }

            holder.headlineNewsBookmarkStatus.setOnClickListener{

                if (getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(newsDetails.news_id)) == 1){
                    newsListDBHelper.updateNewsBookmarkStatus(newsDetails.news_id,"unstar")
                    holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                }else{
                    newsListDBHelper.updateNewsBookmarkStatus(newsDetails.news_id,"star")
                    holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
                }

            }
            holder.headlineNewsHide.setOnClickListener{
                list.removeAt(position)
                notifyItemRemoved(position)

                newsListDBHelper.deleteNews(newsDetails.news_id.toString())
            }
            holder.newsListWrapper.setOnClickListener {
                ClassSharedPreferences(context).setCurrentNewsId(newsDetails.news_id)
                val intent          = Intent(context, ActivityViewPost::class.java)
                ContextCompat.startActivity(context, intent, Bundle())
//                FragmentRecycler().addAnim(context)
            }

            //changing web source colors
            holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList[(0 until bgColorList.size-1).shuffled().last()]))
        }


        inner class viewHolder(v: View): RecyclerView.ViewHolder(v){
            val headlineNewsImg = v.headlineNewsImg!!
            val headlineNewsTitle = v.headlineNewsTitle!!
            val headlineNewsSource = v.headlineNewsSource!!
            val headlineNewsDate = v.headlineNewsDate!!
            val headlineNewsNoOfViews = v.headlineNewsNoOfViews!!
            val headlineNewsBookmarkStatus = v.headlineNewsBookmarkStatus!!
            val headlineNewsHide = v.headlineNewsHide!!
            val newsListWrapper = v.newsListWrapper!!
        }
        private fun getNewsBookMarkStatus(newsLists: ArrayList<NewsListClassBinder>):Int? {
            var newsIsBookmarked:Int? = null
            for (newsRow: NewsListClassBinder in newsLists) {
                newsIsBookmarked = newsRow.news_is_bookmarked
            }
            return newsIsBookmarked
        }

    }



}
