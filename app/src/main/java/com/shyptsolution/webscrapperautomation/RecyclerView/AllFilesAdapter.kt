package com.shyptsolution.webscrapperautomation.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.shyptsolution.webscrapperautomation.R
import java.io.File

class AllFilesAdapter(var listOfFiles:ArrayList<File>, var context: Context,var listener:AllFilesAdapter.onClick): RecyclerView.Adapter<AllFilesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.imageView)
        val title = itemView.findViewById<TextView>(R.id.textView)
        val card = itemView.findViewById<CardView>(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_row_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = listOfFiles[position]
        holder.title.text=current.name
        holder.card.setOnClickListener {listener.openPDF(current)  }
    }

    override fun getItemCount(): Int {
        return listOfFiles.size
    }

    interface onClick{
        fun openPDF(file:File)
    }
}