package com.nigernewsheadlines

import android.text.Html
import android.text.Spanned



class ClassHtmlFormater {

    @Suppress("DEPRECATION")
    fun fromHtml(html: String?): Spanned {
        val result: Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            result = Html.fromHtml(html!!.replace("&lt;", "<").replace("&gt;", ">"))
//            result = Html.fromHtml("<![CDATA[$html]]>");
        }
        return result
    }
}