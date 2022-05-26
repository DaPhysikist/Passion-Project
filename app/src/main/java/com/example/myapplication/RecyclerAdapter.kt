package com.example.myapplication

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Points
import com.example.myapplication.data.inflate

class RecyclerAdapter(private val pointsList: List<Points>) : RecyclerView.Adapter<RecyclerAdapter.PointsHolder>(){
    override fun getItemCount() = pointsList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PointsHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return PointsHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.PointsHolder, position: Int) {
        val points = pointsList[position]
        holder.From.text = points.sender
        holder.TotalPoints.text = points.totalPoints.toString()
        holder.Description.text = points.description
    }


    class PointsHolder(v: View) : RecyclerView.ViewHolder(v) {
        val From: TextView = itemView.findViewById(R.id.From)
        val TotalPoints: TextView = itemView.findViewById(R.id.TotalPoints)
        val Description: TextView = itemView.findViewById(R.id.Description)
    }
}

