package com.nigernewsheadlines

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class ActivityHome : AppCompatActivity(){
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    lateinit var dialog:ClassProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        dialog= ClassProgressDialog(this,"Refreshing...")
//        dialog.createDialog()

//        supportActionBar?.title = "9ja News Updates"
//        supportActionBar?.subtitle = "Read all the Nig news offline"
//        supportActionBar?.subtitle = ClassDateAndTime().dateTimePast(1553108828)
//        supportActionBar?.setLogo(R.drawable.logo)
//        startActivity(Intent(this,   ActivityViewPost::class.java))

//        this.deleteDatabase("all_niger_news.db")//delete database
//        ClassDownloadDataFromServer(this).downLoadNews()
//        ClassDownloadDataFromServer(this).downLoadNewsListAndNewsCategory()


//        ClassDownloadDataFromServer(this).downImg()

        newsListDBHelper = SQLiteNewsDBHelper(this)
//        ImageDownload(this,testImgBitmap).execute("https://i2-prod.football.london/incoming/article16105366.ece/ALTERNATES/s615b/1_GettyImages-1141527393.jpg")

//        testImgBitmap?.setImageBitmap(newsListDBHelper.getImage(14))

        addFragmentsAndViewpager()//for view pager adding anf toolbar management
        ClassAlarmSettings(this).scheduleAlarm()//alarm sschedulingfor background intent
    }


    private fun addFragmentsAndViewpager(){
        val adapter = NewsToggler(supportFragmentManager)
        adapter.addFragment(FragmentRecycler(), "Latest",0)
        adapter.addFragment(FragmentRecycler(), "Sports",1)
        adapter.addFragment(FragmentRecycler(), "Entertainments",2)
        adapter.addFragment(FragmentRecycler(), "Politics",3)
        adapter.addFragment(FragmentRecycler(), "News",13)
        adapter.addFragment(FragmentRecycler(), "Technology",4)
        adapter.addFragment(FragmentRecycler(), "Health",10)
        adapter.addFragment(FragmentRecycler(), "World",11)


        // Finally, data bind the view pager widget with pager adapter
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 7

        // Set up tab layout with view pager widget
        news_tabs.setupWithViewPager(view_pager)
//        news_tabs.addTab(news_tabs.newTab().setIcon(android.R.drawable.ic_dialog_email).setText("Feedback"))

    }
    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)

//        val webFilterMenu = menu.addSubMenu("Website Filter").setIcon(R.drawable.ic_tune_black_24dp) //or
        val webFilterMenu = menu.findItem(R.id.menu_news_sources).subMenu
        val categoryFilterMenu = menu.findItem(R.id.menu_cat_filter).subMenu
        webFilterMenu?.clear() // delete place holder
        categoryFilterMenu?.clear() // delete place holder

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
//        for category
        val newsCats = newsListDBHelper.readCategoryDetails()
        for (element in newsCats) {
            categoryFilterMenu?.add(
                    339,      //grp id
                    element[0].toInt(),          //item id
                    0,      //Item order
                    element[1]   //Item Title
            )?.setIcon(R.drawable.ic_access_time_black_24dp)
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
                startActivity(Intent(this, ActivityListNewsSources::class.java))
            }
            339 -> {//for category list
                ClassSharedPreferences(this).setCurrentCategoryId(item.itemId)
                startActivity(Intent(this, ActivityListNewsCategory::class.java))
            }
            0 ->{
                when(item.itemId){
                    R.id.menu_bookmarks ->{
                        startActivity(Intent(this, ActivityBookmarks::class.java))
                    }
                    R.id.menu_settings ->{

                    }
                    R.id.menu_about_app ->{

                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (view_pager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
//            view_pager.currentItem = view_pager.currentItem - 1
            view_pager.currentItem = 0
        }
    }

    class NewsToggler(manager: FragmentManager) : FragmentPagerAdapter(manager) {
//    class NewsToggler(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

        private val fragments: MutableList<Fragment> = ArrayList()
        private val titles: MutableList<String> = ArrayList()
        private val categoryId: MutableList<Int> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return FragmentRecycler.newInstance(titles[position],categoryId[position],position)

//            return when (position) {
//
//                0 -> FragmentRecycler.newInstance(titles[position],0,position)
//                1 -> FragmentRecycler.newInstance(titles[position],1,position)
//                2 -> FragmentRecycler.newInstance(titles[position],2,position)
//                3 -> FragmentRecycler.newInstance(titles[position],3,position)
//                4 -> FragmentRecycler.newInstance(titles[position],13,position)
//                5 -> FragmentRecycler.newInstance(titles[position],4,position)
//                6 -> FragmentRecycler.newInstance(titles[position],10,position)
//                7 -> FragmentRecycler.newInstance(titles[position],11,position)
//                else -> fragments[position]
//            }
        }
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }


        fun addFragment(fragment: Fragment, title: String, cat_id: Int) {
            fragments.add(fragment)
            titles.add(title)
            categoryId.add(cat_id)
        }
    }

}