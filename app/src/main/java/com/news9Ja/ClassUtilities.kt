package com.news9Ja

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import java.util.*


class ClassUtilities() {




    fun descOrder(list: MutableList<NewsListClassBinder>) :MutableList<NewsListClassBinder>{

        Collections.sort(list, object : Comparator<NewsListClassBinder> {
            override fun compare(o1: NewsListClassBinder?, o2: NewsListClassBinder?): Int {
                val comparator1:Int?
                val comparator2:Int?

                comparator1 = (o1 as NewsListClassBinder).news_id
                comparator2 = (o2 as NewsListClassBinder).news_id

                val sComp = comparator1.compareTo(comparator2)//comparing each other

                if (sComp != 0) {
                    return sComp
                }

//                val x1 = (o1 as Person).getAge()//additional (secondary) sort
//                val x2 = (o2 as Person).getAge()//additional (secondary) sort
                return comparator1.compareTo(comparator2)

            }

        })

//        return  list//in asc of news id
        return  list.asReversed()//in dsc of news id
    }
    fun runnable(milliSecInterval:Long){
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.i("tagcfcfececec", "A Kiss every 5 seconds")
            }

        }, 0, milliSecInterval)
    }
    fun runOnce(milliSecInterval:Long){
        val h = Handler()
        h.postDelayed(object:Runnable {
            private var time:Long = 0
            override fun run() {
                // do stuff then
                // can call h again after work!
                time += 1000
                Log.d("TimerExample", "Going for... $time")
//                upDateBadge(0,(time%1000).toInt())
            }
        }, milliSecInterval) // 1 second delay (takes millis)}
    }
    fun runTwice(milliSecInterval:Long){
        val h = Handler()
        h.postDelayed(object:Runnable {
            private var time:Long = 0
            private var i:Int = 1
            override fun run() {
                // do stuff then
                // can call h again after work!
                time += 1000
                Log.d("TimerExample", "Going for... $time")
                if (i<=2){//twice but the result is 3 times
                    h.postDelayed(this, milliSecInterval)
                }
                i++
            }
        }, milliSecInterval) // 1 second delay (takes millis)}
    }
    fun upDateBadge2(cat_pos:Int,badge_no: Int, context:Context){

        val tabCatIdtoPosition = hashMapOf(0 to 0,1 to 1, 2 to 2, 3 to 3, 13 to 4, 4 to 5, 10 to 6, 11 to 7)
        val tabTitles = listOf<String?>("Latest", "Sports", "Entertainments", "Politics", "News", "Technology", "Health", "World")
        val custom_tablayout = LayoutInflater.from(context).inflate(R.layout.custom_tablayout, null)
        val tab_title = custom_tablayout.findViewById(R.id.tab_text) as TextView
        tab_title.text = tabTitles[tabCatIdtoPosition[cat_pos]!!]
        val tab_badge = custom_tablayout.findViewById(R.id.tab_badge) as TextView
        tab_badge.text = badge_no.toString()
//        val tab = news_tabs.getTabAt(cat_pos)
//        if (tab!!.isSelected){
//            tab_title.setTextColor(Color.parseColor("#212121"))
//        }
//        tab.customView = null
//        tab.customView = custom_tablayout
    }



    // Setup the callback for when data is received from the service
    fun setupServiceReceiver(context:Context) {
        val receiverForTest = ReceiverHomeActivity(Handler())
        // This is where we specify what happens when data is received from the service
        receiverForTest.setReceiver(object : ReceiverHomeActivity.Receiver {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == Activity.RESULT_OK) {
                    val resultValue = resultData.getString("resultValue")
                    Toast.makeText(context, "$resultValue eee", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}


fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }

    return inSampleSize
}