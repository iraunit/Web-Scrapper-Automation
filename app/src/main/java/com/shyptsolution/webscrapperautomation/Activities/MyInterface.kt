package com.shyptsolution.webscrapperautomation.Activities

import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.annotation.RequiresApi
import com.shyptsolution.webscrapperautomation.DataClasses.LinkedInCommentEntity
import com.shyptsolution.webscrapperautomation.DataClasses.ScrapEntity
import com.shyptsolution.webscrapperautomation.MainActivity
import de.siegmar.fastcsv.writer.CsvWriter
import de.siegmar.fastcsv.writer.LineDelimiter
import de.siegmar.fastcsv.writer.QuoteStrategy
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.StringWriter
import java.util.regex.Pattern


public class MyInterface {
    @JavascriptInterface
    fun handleHtml(html: String?) {
        // Use jsoup on this String here to search for your content.
        val doc: Document = Jsoup.parse(html)
        Log.d("HTML","here")
        // Now you can, for example, retrieve a div with id="username" here
//        val usernameDiv: Element = doc.select("#username").first()
        Log.d("HTML","this is the start" + doc.getElementsByClass("block text-sm text-color-text-low-emphasis font-bold mb-2").outerHtml())
    }

    @JavascriptInterface
    fun getTotalComments(html: String?) {
        // Use jsoup on this String here to search for your content.
        val doc: Document = Jsoup.parse(html)
        val re = "[^0-9]".toRegex()
        val tot = doc.getElementsByClass("social-details-social-counts")[0].getElementsByTag("li")[1].text()
        val totalCo = re.replace(tot,"").trim().toInt()
        val com = doc.getElementsByClass("comments-comment-item__main-content").size
        ScrapLinkedInComments.totalComments=totalCo
        ScrapLinkedInComments.currComments=com
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JavascriptInterface
    fun getAllComments(html: String?){
        val doc = Jsoup.parse(html)
//        ScrapLinkedInComments.fileName=doc.title()
        val comments = doc.getElementsByClass("comments-comment-item__main-content")
        val commenterProfile = doc.getElementsByClass("comments-post-meta__profile-info-wrapper display-flex")
        val allCommentsList = arrayListOf<LinkedInCommentEntity>()
        for(i in 0 until comments.size){
            try{
                val commentText= comments[i].text()
                val commenterProfileLink = commenterProfile[i].getElementsByTag("a")[0].attr("href")
                val commenterName = commenterProfile[i].getElementsByTag("h3")[0].getElementsByClass("comments-post-meta__name-text hoverable-link-text mr1")[0].text().split("View")[0]
                val headline = commenterProfile[i].getElementsByTag("h3")[0].getElementsByClass("comments-post-meta__headline")[0].text()
                val emails = getEmailAddressesInString(commentText)
                var email = "Spread Us to New Users"
                if (emails != null) {
                    if(emails.size>0)email=emails[0]
                }
                allCommentsList.add(LinkedInCommentEntity(commenterName,headline,commentText,email,commenterProfileLink))
            }
            catch (e:Exception){
                e.stackTrace
            }
        }
        ScrapLinkedInComments.allCommentsList=allCommentsList
    }

    fun getEmailAddressesInString(text: String): ArrayList<String>? {
        val emails: ArrayList<String> = ArrayList()
        val matcher =
            Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
                .matcher(text)
        while (matcher.find()) {
            emails.add(matcher.group())
        }
        return emails
    }

    fun convertToTitle(n: Int): String {
        return if (n === 0) "" else {val k = n-1; convertToTitle(k / 26) + ('A' + k % 26) as Char }
    }
}