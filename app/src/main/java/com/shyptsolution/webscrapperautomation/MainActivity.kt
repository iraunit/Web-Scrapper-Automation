package com.shyptsolution.webscrapperautomation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aspose.cells.PdfCompliance
import com.aspose.cells.PdfSaveOptions
import com.aspose.cells.Workbook
import com.shyptsolution.webscrapperautomation.Activities.AllFiles
import com.shyptsolution.webscrapperautomation.Activities.ScrapLinkedInComments
import com.shyptsolution.webscrapperautomation.DataClasses.ScrapEntity
import com.shyptsolution.webscrapperautomation.RecyclerView.HomeAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:HomeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = GridLayoutManager(this,2)
        adapter = HomeAdapter(addData(),this@MainActivity)
        recyclerView.adapter = adapter

    }
    
    private fun addData():ArrayList<ScrapEntity>{
        val listOfData = arrayListOf<ScrapEntity>()
        val scrapLinkedInComment = ScrapEntity("Download LinkedIn Comments","https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/LinkedIn_icon_circle.svg/800px-LinkedIn_icon_circle.svg.png",ScrapLinkedInComments())
        val allFilesActivity = ScrapEntity("See All Generated Files","https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/LinkedIn_icon_circle.svg/800px-LinkedIn_icon_circle.svg.png",AllFiles())
        listOfData.add(scrapLinkedInComment)
        listOfData.add(allFilesActivity)
        return listOfData
    }
}