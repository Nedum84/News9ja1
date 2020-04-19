package com.news9Ja

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.content.res.AppCompatResources
//import androidx.browser.customtabs.CustomTabsIntent

//import androidx.browser.customtabs.CustomTabsIntent


class ActivityViewWeb : AppCompatActivity() {
    private val NEWS_URL = ClassSharedPreferences(this).getUrlForWebview()



//    private val defaultCustomTabsIntentBuilder: CustomTabsIntent.Builder
//        get() {
//            val builder = CustomTabsIntent.Builder()
//                    .addDefaultShareMenuItem()
//                    .setToolbarColor(resources.getColor(R.color.colorPrimary))
//                    .setShowTitle(true)
////                    .setCloseButtonIcon(R.drawable.close)
//                    getBitmapFromVectorDrawable(R.drawable.close)?.let {
//                        builder.setCloseButtonIcon(it)
//                    }
//            return builder
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_web)
//        setSupportActionBar(toolbar)

//        fab.setOnClickListener { startGitHubProjectCustomTab() }
    }

    /**
     * Start GitHub project custom tab
     *
     * See https://developer.chrome.com/multidevice/android/customtabs
     */
    private fun startGitHubProjectCustomTab() {
        // Apply some fancy animation to show off
//        val customTabsIntent = defaultCustomTabsIntentBuilder
//                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
//                .setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
//                .build()
//
//        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent)
//
//        // This is where the magic happens...
//        CustomTabsHelper.openCustomTab(this, customTabsIntent, Uri.parse(NEWS_URL), WebViewFallback())
    }

    /**
     * Converts a vector asset to a bitmap as required by [CustomTabsIntent.Builder.setCloseButtonIcon]
     *
     * @param drawableId The drawable ID
     * @return Bitmap equivalent
     */
    private fun getBitmapFromVectorDrawable(@DrawableRes drawableId: Int): Bitmap? {
        var drawable = AppCompatResources.getDrawable(this, drawableId) ?: return null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
//        private const val NEWS_URL = "https://github.com/saschpe/android-customtabs"
    }
}
