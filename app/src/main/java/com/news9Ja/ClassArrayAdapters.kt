package com.news9Ja

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_design_newslist1.view.*
import java.util.ArrayList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.adapter_design_comments.view.*


class ListAdapter(val list:MutableList<NewsListClassBinder>, val context: Context, val fragment_position: Int = 2222): RecyclerView.Adapter<ListAdapter.ViewHolder>(){

    private var bgColorList = mutableListOf("#d9b51c","#cd8313","#0067A3","#519839","#F6696C","#0079BF","#064e71","#84499B","#9C27B0")//original
    private var bgColorList2 = mutableListOf("#f0f4c3","#e0f2f1","#fce4ec","#ef6c00","#ef6c00","#e0e0e0","#efebe9","#bbbbbb")
    private var bgColorList3 = mutableListOf("#f0f4c3","#e0f2f1","#fce4ec","#efebe9","#cccccc","#bbbbbb","#ffffff")
    private var diffLayouts = mutableListOf (R.layout.adapter_design_newslist1,R.layout.adapter_design_newslist2,R.layout.adapter_design_newslist4,R.layout.adapter_design_newslist5)
    private var adapterCallbackInterface: ListAdapterCallbackInterface? = null
    var newsListDBHelper = SQLiteNewsDBHelper(context)
    private val layoutRand1 = (0..1).shuffled().last()
    private val layoutRand3 = (0..3).shuffled().last()
    var inflater = R.layout.adapter_design_newslist1
    private val FIRST_ROW = 1
    private val OTHER_ROWS = 2


    init {
        try {
            adapterCallbackInterface = context as ListAdapterCallbackInterface
        } catch (e: ClassCastException) {
//            throw RuntimeException(context.toString() + "Activity must implement ListAdapterCallbackInterface.", e)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == FIRST_ROW)
            inflater = R.layout.adapter_design_newslist6//for the first one
        else if(fragment_position ==2222)//for others
            inflater = diffLayouts[layoutRand3]
        else//for the one in the tabs
            inflater = diffLayouts[layoutRand1]


        return ViewHolder(LayoutInflater.from(context).inflate(inflater,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if((position==0)&&(fragment_position !=2222)){
            FIRST_ROW
        }else if(position==0 && (fragment_position == 1111 && (inflater !=R.layout.adapter_design_newslist4 || inflater !=R.layout.adapter_design_newslist5))){
            FIRST_ROW
        }else{
            OTHER_ROWS
        }
    }

    fun addItems(items: MutableList<NewsListClassBinder>) {
        val lastPos = list.size - 1
        list.addAll(items)
        try {
            notifyItemRangeInserted(lastPos, items.size)
        } catch (e: Exception) {
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val newsDetails = list[position]
        holder.headlineNewsTitle.text     = ClassHtmlFormater().fromHtml(newsDetails.news_title)
        holder.headlineNewsDate.text     = ClassDateAndTime().checkDateTimeFirst(newsDetails.news_date_int!!.toLong())
        holder.headlineNewsNoOfViews.text     = newsDetails.news_no_of_comments.toString()
//        holder.headlineNewsNoOfViews.text     = newsDetails.news_id.toString()
        holder.headlineNewsCat.text = " | "+ newsListDBHelper.readCategoryDetails(newsDetails.news_category)[0][1]//Sun news
        holder.newsSourceIcon.setImageResource(UrlHolder.getNewsSourceImage(context, newsDetails.news_list_id))

        if(newsDetails.news_img_urls !=""){
            LoadImg(context,holder.headlineNewsImg,newsDetails.news_id).execute(newsDetails.news_img_urls)//loading img in bg
        }else{
            holder.headlineNewsImg.setImageResource(UrlHolder.getNewsSourceImage(context, newsDetails.news_list_id.toInt()))
        }
        //Show category only for latest news, and news from NEWS_SOURCES
        if(fragment_position == 1111||fragment_position == 0||fragment_position == 3333){//111 is the latest news coming from category activity
            holder.headlineNewsCat.visibility = View.VISIBLE
        }else{
            holder.headlineNewsCat.visibility = View.GONE
        }


        if (newsDetails.news_is_bookmarked == 1){
            holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
        }
        holder.headlineNewsBookmarkStatus.setOnClickListener{

            if (newsListDBHelper.getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(newsDetails.news_id)) == 1){//bookmarked
                newsListDBHelper.updateNewsBookmarkStatus(newsDetails.news_id,"unstar")
                holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
            }else{//not bookmarked
                //insert news if not already inserted
                if (!newsListDBHelper.checkIfNewsExist(newsDetails.news_list_id)){
                    newsListDBHelper.insertNews(newsDetails)
                }
                newsListDBHelper.updateNewsBookmarkStatus(newsDetails.news_id,"star")
                holder.headlineNewsBookmarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
            }

        }
        holder.headlineNewsHide.setOnClickListener{
            try {
                list.removeAt(position)
                notifyItemRemoved(position)
            } catch (e: Exception) {
            }

            newsListDBHelper.deleteNews(newsDetails.news_id.toString())
        }
        holder.newsListWrapper.setOnClickListener {
            ClassSharedPreferences(context).setCurrentNewsId(newsDetails.news_id)

            //Set the values
            val newsList = ArrayList<NewsListClassBinder>()
            newsList.add(newsDetails)
            val jsonText = Gson().toJson(newsList)
            ClassSharedPreferences(context).setCurrentViewPostDetails(jsonText)

            val intent          = Intent(context, ActivityViewPost::class.java)
            ContextCompat.startActivity(context, intent, Bundle())
        }

        // for news source display web oe source name
        if(inflater == R.layout.adapter_design_newslist6||inflater ==R.layout.adapter_design_newslist5||inflater == R.layout.adapter_design_newslist3){
            holder.headlineNewsSource.text = newsListDBHelper.readNewsListDetails(newsDetails.news_list_id)[0][2]//Sun news
        }else{
            holder.headlineNewsSource.text = newsListDBHelper.readNewsListDetails(newsDetails.news_list_id)[0][1]//www.sunnews.com
        }
        //changing web source colors
        if(inflater == R.layout.adapter_design_newslist6||inflater == R.layout.adapter_design_newslist5||inflater == R.layout.adapter_design_newslist3){
            holder.headlineNewsSource.setTextColor(Color.parseColor("#f1f1f1"))
        }else if (inflater == R.layout.adapter_design_newslist4){
            holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList2[(0 until bgColorList2.size-1).shuffled().last()]))
        }else{
            holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList[(0 until bgColorList.size-1).shuffled().last()]))
        }




        //updated news as listed in the recycler view for tab layout badge numbering
        newsListDBHelper.updateListedNews(newsDetails.news_id)
    }


    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val headlineNewsImg = v.headlineNewsImg!!
        val headlineNewsTitle = v.headlineNewsTitle!!
        val headlineNewsSource = v.headlineNewsSource!!
        val headlineNewsDate = v.headlineNewsDate!!
        val headlineNewsNoOfViews = v.headlineNewsNoOfViews!!
        val headlineNewsBookmarkStatus = v.headlineNewsBookmarkStatus!!
        val headlineNewsHide = v.headlineNewsHide!!
        val newsListWrapper = v.newsListWrapper!!
        val headlineNewsCat = v.headlineNewsCat!!
        val newsSourceIcon = v.newsSourceIcon!!
    }



    //interface declaration
    interface ListAdapterCallbackInterface {
        fun onRemoveDialog()
    }

}

class CommentsAdapter(items:MutableList<NewsComments>, ctx: Context): RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){

    private var list = items
    private var context = ctx

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.adapter_design_comments,
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

    fun addItems(items: MutableList<NewsComments>) {
        val lastPos = list.size - 1
        list.addAll(items)
        notifyItemRangeInserted(lastPos, items.size)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val commentDetails = list[position]
        holder.commentUsername.text     = ClassHtmlFormater().fromHtml(commentDetails.username)
        holder.commentDate.text     = " | "+ClassDateAndTime().checkDateTimeFirst(commentDetails.comment_date!!.toLong())
        holder.commentContent.text     = commentDetails.comment_content

    }


    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val commentUsername = v.commentUsername!!
        val commentDate = v.commentDate!!
        val commentContent = v.commentContent!!
    }

}

class ClassBackgroundActivity (var context: Context, var newsListRecycler: RecyclerView, var bg_category_id: Int, var bg_frag_position: Int, private var listener: FragmentRecycler.FragmentRecyclerInteractionListener?)
    : AsyncTask<Void, Void, MutableList<NewsListClassBinder>>() {

    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder> = mutableListOf()

    override fun onPreExecute() {
//            listener?.onTaskStarted()
//        listener?.onTaskStarted()
        newsListDBHelper = SQLiteNewsDBHelper(context)
    }


    override fun doInBackground(vararg params: Void): MutableList<NewsListClassBinder> {
        val devotionLists = newsListDBHelper.readAllNewsList(1,"category",0)
        for (newsRow: NewsListClassBinder in devotionLists) {
            newsList.add(newsRow)
        }

        return newsList
    }

    override fun onPostExecute(data: MutableList<NewsListClassBinder>) {
        newsList.addAll(data)
//            news_list_recycler.notifyDataSetChanged()
        newsListRecycler.adapter = ListAdapter(newsList, context, bg_frag_position)
//            FragmentRecycler().adapt?.notifyDataSetChanged()
//            newsListRecycler.viewpagerAdapter = ADAPTER
        Thread.sleep(2000)
//        listener?.onTaskFinished("")
    }
}

class LoadNews (var ADAPTER : ListAdapter): AsyncTask<MutableList<NewsListClassBinder>, Void, String?>() {

    override fun onPreExecute() {    }

    override fun doInBackground(vararg params: MutableList<NewsListClassBinder>?): String? {


        ADAPTER.addItems(ClassUtilities().descOrder(params[0]!!))

        return null
    }

    override fun onPostExecute(data: String?) {

//        ADAPTER.notifyDataSetChanged()
    }
}