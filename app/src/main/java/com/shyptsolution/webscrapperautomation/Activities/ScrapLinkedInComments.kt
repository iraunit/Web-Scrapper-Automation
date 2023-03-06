package com.shyptsolution.webscrapperautomation.Activities

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shyptsolution.webscrapperautomation.DataClasses.LinkedInCommentEntity
import com.shyptsolution.webscrapperautomation.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.codec.DecoderException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*


class ScrapLinkedInComments : AppCompatActivity() {

    init {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    companion object {
        var currComments = 0
        var totalComments = Int.MAX_VALUE
        lateinit var allCommentsList:ArrayList<LinkedInCommentEntity>
        var fileName = "www.codingkaro.in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrap_linked_in_comments)

        val wv = findViewById<WebView>(R.id.webView)
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true
        wv.settings.loadsImagesAutomatically=false
        wv.settings.blockNetworkImage=true
        WebView.setWebContentsDebuggingEnabled(true)
        val url = "https://www.linkedin.com/posts/love-babbar-38ab2887_socialmedia-students-mentalhealth-activity-7038479013845053440-EfA1?utm_source=share&utm_medium=member_desktop"
            wv.addJavascriptInterface(MyInterface(), "HtmlHandler")
//        val document: Document = Jsoup.connect(url).get()
////        document.getElementsByClass("")[0]
//        Toast.makeText(this,document.title(),Toast.LENGTH_LONG).show()
        wv.loadUrl(url)
        val btn = findViewById<Button>(R.id.button)
        setDesktopMode(wv, true)

        btn.setOnClickListener {
//            val f = File(this.filesDir,"file.csv")
//            val workbook = Workbook()
//            workbook.worksheets[0].cells["A1"].putValue("Hello World!")
//            workbook.save(this.filesDir.toString()+"Excel.xlsx")
//            workbook.worksheets.removeAt(1)
//            workbook.save(this.filesDir.toString()+"Excel.xlsx")
//            val fw = FileWriter(f)
//            wv.evaluateJavascript("document.getElementsByClassName('comments-comments-list__load-more-comments-button')[0].click();",null)
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    wv.loadUrl(
                        "javascript:window.HtmlHandler.getTotalComments" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                    )
                }
                Thread.sleep(5000)
                var cnt = 0
                var prev = 0
                while (cnt < 10 && currComments < totalComments) {
                    if(prev == currComments)cnt++
                    else cnt = 0
                    withContext(Dispatchers.Main) {
                        wv.loadUrl(
                            "javascript:(function(){" +
                                    "l=document.getElementsByClassName('comments-comments-list__load-more-comments-button');" +
                                    "e=document.createEvent('HTMLEvents');" +
                                    "e.initEvent('click',true,true);" +
                                    "if(l.length>0)"+
                                    "l[0].dispatchEvent(e);" +
                                    "})()"
                        )
                        wv.loadUrl(
                            "javascript:window.HtmlHandler.getTotalComments" +
                                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                        )
                        prev = currComments
                    }
                    Thread.sleep(5000)
                    Log.d("HTML", "Loaded Comments Count is $currComments")
                    cnt++
                }

                withContext(Dispatchers.Main){
                    wv.loadUrl(
                        "javascript:window.HtmlHandler.getAllComments" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                    )
                    createExcel(createWorkbook())
                }
//                fw.write(sw.toString())
//                fw.close()
            }

        }

        wv.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                //view.loadUrl(url);
                return false
            }
        })
        wv.loadUrl(url)
    }

    private fun setDesktopMode(webView: WebView, enabled: Boolean) {
        var newUserAgent: String? = webView.settings.userAgentString
        if (enabled) {
            try {
                val ua: String = webView.settings.userAgentString
                val androidOSString: String = webView.settings.userAgentString.substring(
                    ua.indexOf("("),
                    ua.indexOf(")") + 1
                )
                newUserAgent =
                    webView.settings.userAgentString.replace(androidOSString, "(X11; Linux x86_64)")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            newUserAgent = null
        }
        webView.settings.apply {
            userAgentString = newUserAgent
            useWideViewPort = enabled
            loadWithOverviewMode = enabled
        }
        webView.reload()
    }

    private fun createWorkbook(): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Shypt Solution")
        sheet.addMergedRegion(CellRangeAddress(0,0,0,4))
        createSheetHeader(getHeaderStyle(workbook), sheet)
        createShyptSolutionHeader(getShyptSolutionHeader(workbook),sheet)
        sheet.createFreezePane(0,2)
        sheet.protectSheet("donate_to_shypt_solution")
        addData(sheet, allCommentsList,workbook)
        return workbook
    }

    private fun getHeaderStyle(workbook: XSSFWorkbook): CellStyle {
        val cellStyle: CellStyle = workbook.createCellStyle()
        val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
        var color = XSSFColor(IndexedColors.BLUE, colorMap).indexed
        cellStyle.fillForegroundColor = color
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        val whiteFont = workbook.createFont()
        color = XSSFColor(IndexedColors.WHITE, colorMap).indexed
        whiteFont.color = color
        whiteFont.bold = true
        cellStyle.setFont(whiteFont)
        return cellStyle
    }

    private fun getShyptSolutionHeader(workbook: XSSFWorkbook): CellStyle {
        val cellStyle: CellStyle = workbook.createCellStyle()
        val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
        var color = XSSFColor(IndexedColors.RED, colorMap).indexed
        cellStyle.fillForegroundColor = color
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        val whiteFont = workbook.createFont()
        color = XSSFColor(IndexedColors.WHITE, colorMap).indexed
        whiteFont.color = color
        whiteFont.bold = true
        cellStyle.locked=true
        cellStyle.setFont(whiteFont)
        return cellStyle
    }


    private fun addData(sheet: Sheet,listOfComments:ArrayList<LinkedInCommentEntity>,wb:XSSFWorkbook) {
        val unlockedCellStyle: CellStyle = wb.createCellStyle()
        unlockedCellStyle.locked = false
        for(i in 0 until listOfComments.size){
            val current=listOfComments[i]
            val row = sheet.createRow(i+2)
            row.rowStyle=unlockedCellStyle
            createUnlockedCell(row, 0, current.name,wb)
            createUnlockedCell(row, 1, current.headline,wb)
            createUnlockedCell(row, 2, current.profileLink,wb)
            createUnlockedCell(row, 3, current.comment,wb)
            createUnlockedCell(row, 4, current.email,wb)
        }
    }

    private fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {
        val row = sheet.createRow(1)
        val HEADER_LIST = listOf("Name", "Headline", "Profile Link","Comment","Email Found")
        for ((index, value) in HEADER_LIST.withIndex()) {
            val columnWidth = (15 * 500)
            sheet.setColumnWidth(index, columnWidth)
            val cell = row.createCell(index)
            cell?.setCellValue(value)
            cell.cellStyle = cellStyle
        }
    }

    private fun createShyptSolutionHeader(cellStyle: CellStyle, sheet: Sheet) {
        val row = sheet.createRow(0)
        val HEADER_LIST = listOf("Rate Us 5 ⭐ on Play Store and share with Your Friends.")
        for ((index, value) in HEADER_LIST.withIndex()) {
            val columnWidth = (15 * 500)
            sheet.setColumnWidth(index, columnWidth)
            val cell = row.createCell(index)
            cell?.setCellValue(value)
            cell.cellStyle = cellStyle
        }
    }

    private fun createCell(row: Row, columnIndex: Int, value: String?) {
        val cell = row.createCell(columnIndex)
        cell?.setCellValue(value)
    }

    private fun createUnlockedCell(row: Row, columnIndex: Int, value: String?,wb:XSSFWorkbook) {
        val unlockedCellStyle: CellStyle = wb.createCellStyle()
        unlockedCellStyle.locked = false
        val cell = row.createCell(columnIndex)
        cell?.setCellValue(value)
        cell.cellStyle=unlockedCellStyle
    }

    private fun createExcel(workbook: XSSFWorkbook) {
        val excelFile = File(this@ScrapLinkedInComments.filesDir, "$fileName.xlsx")
        try {
            val fileOut = FileOutputStream(excelFile)
            workbook.write(fileOut)
            fileOut.close()
            Log.d("HTML","Writing Done")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}