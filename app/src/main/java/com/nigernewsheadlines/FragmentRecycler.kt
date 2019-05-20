package com.nigernewsheadlines


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_design_newslist1.view.*
import kotlinx.android.synthetic.main.fragment_recycler.*
import java.util.ArrayList
import com.bumptech.glide.request.RequestOptions




class FragmentRecycler : Fragment() {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    private var newsList: MutableList<NewsListClassBinder>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        newsListDBHelper = SQLiteNewsDBHelper(activity!!.applicationContext)
        news_list_recycler.layoutManager = LinearLayoutManager(activity)



        //checking for already sent argument
        if(arguments != null){
            val cat_name = arguments?.getCharSequence("cat_name").toString()
            CATEGORY_ID = arguments!!.getInt("cat_id")
            FRAGMENT_POSITION = arguments!!.getInt("frag_position")

            getNewsList(newsListDBHelper.readAllNewsList(1,"category"))
        }
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
//        clearNewsListDataClassBinder()


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
            news_list_recycler.adapter = context?.let { ListAdapter(newsList!!, it,CATEGORY_ID, FRAGMENT_POSITION) }
        }
    }


    class ListAdapter(items:MutableList<NewsListClassBinder>, ctx: Context, category_id: Int, fragment_position: Int): RecyclerView.Adapter<ListAdapter.viewHolder>(){

        private var bgColorList = mutableListOf("#d9b51c","#cd8313","#0067A3","#519839","#F6696C","#0079BF","#064e71","#84499B")//original
        private var bgColorList2 = mutableListOf("#f0f4c3","#e0f2f1","#fce4ec","#ef6c00","#ef6c00","#e0e0e0","#efebe9","#bbbbbb")
        private var bgColorList3 = mutableListOf("#f0f4c3","#e0f2f1","#fce4ec","#efebe9","#cccccc","#bbbbbb","#ffffff")
        private var diffLayouts = mutableListOf (R.layout.adapter_design_newslist1
                                ,R.layout.adapter_design_newslist3,R.layout.adapter_design_newslist2
                                ,R.layout.adapter_design_newslist4,R.layout.adapter_design_newslist1
                                ,R.layout.adapter_design_newslist5,R.layout.adapter_design_newslist4
                                ,R.layout.adapter_design_newslist2)
        private var list = items
        var context = ctx
        var cat_id = category_id
        var frag_position = fragment_position//POSITION OF THE FRAGMENTS LIKE 0,1,2,3,4,5,6,7 ...
        var newsListDBHelper = SQLiteNewsDBHelper(context)

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): viewHolder {
            return viewHolder(LayoutInflater.from(context).inflate(
                    diffLayouts[frag_position],
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
            holder.headlineNewsDate.text     = ClassDateAndTime().getDateTime(newsDetails.news_date_int!!.toLong())
//            holder.headlineNewsNoOfViews.text     = newsDetails.news_no_of_views.toString()
            holder.headlineNewsNoOfViews.text     = cat_id.toString()

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
            // for news sorce display web oe source name
            if(frag_position==1||frag_position==5){
                holder.headlineNewsSource.text = newsListDBHelper.readNewsListDetails(newsDetails.news_list_id)[0][2]
            }else{
                holder.headlineNewsSource.text = newsListDBHelper.readNewsListDetails(newsDetails.news_list_id)[0][1]
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
            if (frag_position==3||frag_position==6){
                holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList2[(0 until bgColorList2.size-1).shuffled().last()]))
            }else if(frag_position==1||frag_position==5){
                holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList3[(0 until bgColorList3.size-1).shuffled().last()]))
            }else{
                holder.headlineNewsSource.setTextColor(Color.parseColor(bgColorList[(0 until bgColorList.size-1).shuffled().last()]))
            }
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



    //Object to bind the datas
    companion object {
        private var CATEGORY_ID: Int = 1
        private var FRAGMENT_POSITION: Int = 1

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

}
