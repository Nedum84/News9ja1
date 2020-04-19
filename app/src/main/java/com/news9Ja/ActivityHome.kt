package com.news9Ja

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import java.util.*
import android.view.View
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_home.*
import com.news9Ja.FragmentRecycler.FragmentRecyclerInteractionListener
import kotlinx.android.synthetic.main.inc_network_error_page.*


class ActivityHome : ActivityBaseActivity(), FragmentRecyclerInteractionListener, FragmentDialogLogin.FragmentDialogLoginInteractionListener
        , ListAdapter.ListAdapterCallbackInterface, ClassDownloadDataFromServer.DataDownloadCallbackInterface {

    private var newsList: MutableList<NewsListClassBinder> = mutableListOf()
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    lateinit var progressDialog: ClassProgressDialog
    lateinit var viewpagerAdapter: NewsToggler
    lateinit var thisContext: Context
    var TAB_POSITION = "POSITION"
    var currentTabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        toolbar.title = ""
        thisContext = this
        newsListDBHelper = SQLiteNewsDBHelper(this)
        progressDialog = ClassProgressDialog(this,"Refreshing...")//Or "Loading, please wait..."
//        this.deleteDatabase("all_niger_news.db")//delete database

        if(ClassSharedPreferencesSettings(this).getOpeningForTheFirstTime()) {
            ClassDownloadDataFromServer(this).downLoadNewsListAndNewsCategory(true)
        }else{

            if (ClassSharedPreferencesSettings(this).getIsNewsLoadedOffline()||ClassSharedPreferencesSettings(this).getNotificationStatus()){
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        ClassAlarmSettings(this).scheduleAlarm()//alarm scheduling for background intent
                    } catch (e: Exception) {

                    }
                }, 20000) //Delay 20 second}
            }else{
                ClassAlarmSettings(this).cancelAlarm()//alarm scheduling for background intent
            }

            //LOAD DATA...
            refresh()

            refreshPageFromNw.setOnClickListener {
                refresh()
            }

        }


        tapToSetup.setOnClickListener {
            finish()
            startActivity(intent)
        }
    }

    private fun refresh(){
//        nwErrorPageWrapper?.visibility = View.GONE
        addFragmentsAndViewpager()
    }


    override fun onRemoveDialog() {
        progressDialog.dismissDialog()
    }

    override fun onRemoveTab(position: Int) {
      news_tabs.removeTabAt(position)
    }

    override fun onReload() {
        finish()
        startActivity(intent)
    }

    override fun onFirstTimeErrorShow() {
        tapToSetup.visibility  = View.VISIBLE
    }

    private fun addFragmentsAndViewpager(){
        news_tabs.visibility = View.VISIBLE
        viewpagerAdapter = NewsToggler(supportFragmentManager,this)
        for (catId in UrlHolder.TAB_CATS){
            if (newsListDBHelper.readCategoryDetails(catId)[0][2].toInt() == 0)continue

            val catName = if(catId == 0){"Latest" }else{newsListDBHelper.readCategoryDetails(catId)[0][1]}
            viewpagerAdapter.addFragment(FragmentRecycler(), catName, catId)
        }

        // Finally, data bind the view pager widget with pager viewpagerAdapter
        view_pager.adapter = viewpagerAdapter
        view_pager.offscreenPageLimit = viewpagerAdapter.count-1
        news_tabs.setupWithViewPager(view_pager)
        view_pager.currentItem = currentTabPosition


        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
//                viewpagerAdapter.notifyDataSetChanged()
                currentTabPosition = position
//                e_download_view_pager.scrollY = currentTabScrollPosition
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                currentTabScrollPosition = positionOffsetPixels
            }
        })

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_POSITION, currentTabPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTabPosition = savedInstanceState.getInt(TAB_POSITION)
    }


    private var action_menu_login: MenuItem? = null
    private var action_menu_logout: MenuItem? = null
    private fun showHideMenuItems(){
        if (ClassSharedPreferences(this).isLoggedIn()){
            action_menu_login!!.isVisible = false
        }else{
            action_menu_logout!!.isVisible = false
        }
    }
    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        action_menu_login = menu.findItem(R.id.menu_login)
        action_menu_logout = menu.findItem(R.id.menu_logout)
        showHideMenuItems()

//        val webFilterMenu = menu.addSubMenu("Website Filter").setIcon(R.drawable.ic_tune_black_24dp) //or
        val webFilterMenu = menu.findItem(R.id.menu_news_sources).subMenu
        val categoryFilterMenu = menu.findItem(R.id.menu_cat_filter).subMenu
        webFilterMenu?.clear() // delete place holder
        categoryFilterMenu?.clear() // delete place holder

//        for web site filter

        val newsList = newsListDBHelper.readNewsListDetails()
        for (element in newsList) {
            if (element[3].toInt() == 0)continue//blocked from receiving news
            webFilterMenu?.add(
                    335,      //grp id
                    element[0].toInt(),          //item id
                    0,      //Item order
                    element[2]   //Item Title
            )?.setIcon(UrlHolder.getNewsSourceImage(this,element[0].toInt()))
        }
//        for category
        val newsCats = newsListDBHelper.readCategoryDetails()
        for (element in newsCats) {
            if (element[2].toInt() == 0)continue//blocked from receiving news
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
                    R.id.menu_refresh ->{
                        finish()
                        startActivity(intent)
                    }
                    R.id.menu_about_app ->{
                        startActivity(Intent(this,ActivityAboutApp::class.java))
                    }
                    R.id.menu_share_news_of_dDay ->{
                        ClassShareApp(this).shareNewsOfDday()
                    }
                    R.id.menu_share_app->{
                        ClassShareApp(this).shareApp()
                    }
                    R.id.menu_settings->{
                        startActivity(Intent(this,ActivitySettings::class.java))
                    }
                    R.id.menu_login ->{
                        if (ClassSharedPreferences(this).isLoggedIn()){
                            ClassAlertDialog(this).toast("You have already logged in...")
                            action_menu_login!!.isVisible = false
                            action_menu_logout!!.isVisible = true
                        }else
                            dialogShow()
                    }
                    R.id.menu_logout ->{
                        ClassSharedPreferences(thisContext).setUserId("")
                        finish()
                        startActivity(intent)
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        if (view_pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
//            view_pager.currentItem = view_pager.currentItem - 1
            view_pager.currentItem = 0
        }
    }

//    class NewsToggler(manager: FragmentManager,val ctx:Context) : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    class NewsToggler(manager: FragmentManager, var context:Context) : FragmentStatePagerAdapter(manager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val titles: MutableList<String> = ArrayList()
        private val categoryId: MutableList<Int> = ArrayList()
        private var newsListDBHelper  = SQLiteNewsDBHelper(context)


        override fun getItem(position: Int): Fragment {
            return FragmentRecycler.newInstance(titles[position], categoryId[position], position)
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





    private val dialogFragment = FragmentDialogLogin()
    private fun dialogShow(){
        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
        val prev = supportFragmentManager.findFragmentByTag(FragmentDialogLogin::class.java.name)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, FragmentDialogLogin::class.java.name)
    }
    override fun onCloseDialog(input: String) {
        dialogFragment.dismiss()
    }







    val timerHandler = Handler()
    private val myTimerRunnable = object:Runnable {
        override fun run() {
            if(ClassNetworkStatus(thisContext).isNetworkAvailable())
                startServiceWithReceiver()

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

    private fun startServiceWithReceiver() {
        val i = Intent(this, IntentServiceWithReceiver::class.java)
        i.putExtra("foo_from_activity", "bar boyszz")
        startService(i)
    }
    // Setup the callback for when data is received from the service
    override fun onResume() {
        super.onResume()
        // Register for the particular broadcast based on ACTION string
        val filter  = IntentFilter(IntentServiceWithReceiver.ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter)

        runnableBgService()
    }

    override fun onPause() {
        super.onPause()
        destroyTimeHandler()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver)
    }
    // Define the callback for what to do when data is received
    private val testReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED)
            if (resultCode == Activity.RESULT_OK) {
                val resultValue = intent.getStringExtra("resultValue")
//                Toast.makeText(thisContext, "$resultValue", Toast.LENGTH_SHORT).show()

//                if(resultValue.toInt()>1){
//                    refreshPage.visibility = View.VISIBLE
//                    refreshPage.text = "$resultValue+ more news"
//                }else{
//                    refreshPage.visibility = View.GONE
//                }
            }
        }

    }

}