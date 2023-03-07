package com.shyptsolution.webscrapperautomation.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.shyptsolution.webscrapperautomation.DataClasses.ScrapEntity
import com.shyptsolution.webscrapperautomation.R
import com.squareup.picasso.Picasso

class AllFilesAdapter(var listOfAutomation:ArrayList<ScrapEntity>, var context: Context): RecyclerView.Adapter<AllFilesAdapter.ViewHolder>() {

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
        val current = listOfAutomation[position]
        Picasso.get().load(current.pic_url).into(holder.image)
        holder.title.text = current.name
        holder.card.setOnClickListener {
            context.startActivity(Intent(context.applicationContext,current.activity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return listOfAutomation.size
    }
}