package com.shyptsolution.webscrapperautomation

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shyptsolution.webscrapperautomation.Activities.ScrapLinkedInComments
import com.shyptsolution.webscrapperautomation.DataClasses.ScrapEntity
import com.shyptsolution.webscrapperautomation.RecyclerView.HomeAdapter

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter:HomeAdapter
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
    
    fun addData():ArrayList<ScrapEntity>{
        var listOfData = arrayListOf<ScrapEntity>()
        val scrapLinkedInComment = ScrapEntity("Download LinkedIn Comments","https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/LinkedIn_icon_circle.svg/800px-LinkedIn_icon_circle.svg.png",ScrapLinkedInComments())
        listOfData.add(scrapLinkedInComment)
        return listOfData
    }
}