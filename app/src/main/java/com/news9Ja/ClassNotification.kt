package com.news9Ja

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import android.widget.RemoteViews
import android.graphics.Bitmap
import com.google.gson.Gson


class ClassNotification(val context:Context) {
    private lateinit var notificationChannel : NotificationChannel
    private lateinit var builder : androidx.core.app.NotificationCompat.Builder
    private val channelId = context.packageName
    private val description = "9ja News Notification"
    private var notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val appPackageName:String? = context.packageName // getPackageName() from Context or Activity object
    private  var newsNo:Int = 0
    private  var notificationContentText:String = ""
    private var inboxStyle:NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()/* Add Big View Specific Configuration */
    private var inboxStyle2= NotificationCompat.BigPictureStyle()/* Add Big View Specific Configuration */
//    private var newsTitles = ClassSharedPreferences(context).getNewsTitleForNotification()

    init {

        //BUILD THE INTENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        builder = NotificationCompat.Builder(context,channelId)
    }

    private fun setUpInboxStyle(){
        val newsDetails = ClassSharedPreferences(context).getSavedServer()
        val dataArray = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()

        newsNo = 15
//        inboxStyle.setBigContentTitle("News details")// Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("$newsNo news updates")// Sets a title for the Inbox style big view

        // Moves events into the big view
        for (i in 0 until dataArray.size) {
            val nTitle = dataArray[i].news_title
            inboxStyle.addLine("${i+1}. $nTitle")
            notificationContentText += "$nTitle, "


            if (i>=newsNo)break
        }

        //for big picture style
        inboxStyle2.setBigContentTitle("News updates")
        inboxStyle2.bigPicture(BitmapFactory.decodeResource(context.resources,R.drawable.n9_logo2))
    }
    fun sendNewsUpdates(){
        setUpInboxStyle()
//        val _when = System.currentTimeMillis()
        val notificationsId = (1000..9999).shuffled().last()
//        // Add as notification intent
        val backIntent = Intent(context, ActivityHome::class.java)
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)//changed from FLAG_ACTIVITY_NEW_TASK

        val notificationIntent = Intent(context, ActivityListNewsCategory::class.java)
        notificationIntent.putExtra("notify_cat_id", "0")
        val pendingIntent = PendingIntent.getActivities(context, notificationsId,//unique request code
                arrayOf(backIntent, notificationIntent), PendingIntent.FLAG_UPDATE_CURRENT)//you can try ==> FLAG_CANCEL_CURRENT,FLAG_ONE_SHOT,FLAG_UPDATE_CURRENT

        //scale img bitmap
        val bitmapResult = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.n9_banner), 100, 100, true) //100 x 100 image size
//        val bitmapResult = Bitmap.createScaledBitmap(last_news_img_bitmap, 100, 100, true) //100 x 100 image size
        builder = NotificationCompat.Builder(context,channelId)
//                .setCustomBigContentView(contentView)
//                .setContentTitle(context.getString(R.string.app_name))
                .setContentTitle("$newsNo Unread News")
                .setContentText(notificationContentText)//$newsNo just arrived
//                .setContentText("Recent News are now available. Click to view... ")
//                .setSubText("Recent news")
//                .setNumber(newsNo)/* Increase notification number every time a new notification arrives */
                .setSmallIcon(R.drawable.n9_logo1)
//                .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_menu_gallery))
//                .setLargeIcon(getBitmapFromURL2("http://192.168.44.236/news/banner.jpg"))
                .setLargeIcon(bitmapResult)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)//hide notification when users clicks on it
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)//for lock screen control
                .setShowWhen(true)
//                .setWhen(_when)
                .setTimeoutAfter(50000)
                .setOnlyAlertOnce(true)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)


        notificationManager.notify(1234, builder.build())
    }



    fun sendNewsUpdatesSingle(news_title: String, notify_news_id: String, news_image_url: Bitmap){
        val _when = System.currentTimeMillis()
        val notificationsId = (1000..9999).shuffled().last()
//        // Add as notification intent
        val backIntent = Intent(context, ActivityHome::class.java)
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)//changed from FLAG_ACTIVITY_NEW_TASK

        val notificationIntent = Intent(context, ActivityViewPost::class.java)
        notificationIntent.putExtra("notify_news_id", notify_news_id)
        val pendingIntent = PendingIntent.getActivities(context, notificationsId,//unique request code
                                        arrayOf(backIntent, notificationIntent), PendingIntent.FLAG_UPDATE_CURRENT)//you can try ==> FLAG_CANCEL_CURRENT,FLAG_ONE_SHOT,FLAG_UPDATE_CURRENT

        //SET CUSTOM VIEW FOR NOTIFICATION
        val contentView = RemoteViews(appPackageName,R.layout.notification_layout_single)
        contentView.setTextViewText(R.id.tv_title, ClassHtmlFormater().fromHtml(news_title))
        contentView.setTextViewText(R.id.logo_wrapper,"")
        contentView.setTextViewText(R.id.tv_when,ClassDateAndTime().getDateTimeForNotification())
        contentView.setImageViewResource(R.id.tv_logo, R.drawable.n9_logo2)
//        contentView.setImageViewResource(R.id.tv_news_image, R.drawable.logo2)
        contentView.setImageViewBitmap(R.id.tv_news_image, news_image_url)

        builder.setContent(contentView)
//                .setCustomBigContentView(contentView)
                .setSmallIcon(R.drawable.n9_logo1)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)//hide notification when users clicks on it
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)//for lock screen control
                .setShowWhen(true)
//                .setWhen(_when)
                .setTimeoutAfter(50000)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)

        notificationManager.notify(notify_news_id.toInt(), builder.build())//notificationsId changed to notify_news_id
    }

}
