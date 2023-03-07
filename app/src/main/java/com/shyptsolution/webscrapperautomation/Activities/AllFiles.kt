package com.shyptsolution.webscrapperautomation.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.barteksc.pdfviewer.PDFView
import com.shyptsolution.webscrapperautomation.R
import com.shyptsolution.webscrapperautomation.RecyclerView.AllFilesAdapter
import java.io.File

class AllFiles : AppCompatActivity(), AllFilesAdapter.onClick {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:AllFilesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_files)
        recyclerView = findViewById(R.id.allFilesRecyclerView)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = GridLayoutManager(this,2)
        adapter = AllFilesAdapter(addData(),this@AllFiles,this)
        recyclerView.adapter = adapter
    }

    private fun addData(): ArrayList<File> {
        val listOfFiles = arrayListOf<File>()
        val directory = File(this@AllFiles.filesDir.absolutePath)
        val files: Array<File> = directory.listFiles()
        for(file in files)if(file.extension!="pdf")listOfFiles.add(file)
        return listOfFiles
    }

    override fun openPDF(file: File) {
        Toast.makeText(this,"This is just sample output.",Toast.LENGTH_LONG).show()
        val inflate = LayoutInflater.from(this)
        val popupview = inflate.inflate(R.layout.pdf_viewer, null, false)
        val builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, true
        )

        builder.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.background
            )
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.showAtLocation(
            findViewById(R.id.allFilesRecyclerView),
            Gravity.CENTER,
            0,
            0
        )
        val uri = FileProvider.getUriForFile(
            this,
            "com.shyptsolution.webscrapperautomation" + ".provider",
            File(this.filesDir, file.name.subSequence(0,file.name.lastIndexOf('.')).toString()+".pdf")
        )
        popupview.findViewById<PDFView>(R.id.pdfView).fromUri(uri).enableSwipe(true).enableDoubletap(true).load()

        popupview.findViewById<ImageView>(R.id.sharePdf).setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "application/pdf"
            val newuri = FileProvider.getUriForFile(
                this,
                "com.shyptsolution.webscrapperautomation" + ".provider",
                file
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Shared From Web Scrapper & Automation app by Shypt Solution, Download The app Now. https://url.codingkaro.in/mdmcalculator"
            )
            intent.putExtra(Intent.EXTRA_STREAM, newuri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
    }
}