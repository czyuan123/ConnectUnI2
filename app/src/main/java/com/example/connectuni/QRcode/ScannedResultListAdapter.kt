package com.example.connectuni.QRcode

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectuni.R


class ScannedResultListAdapter(
    var updateResultList: ArrayList<resultData>,
    var context: Context?
) : RecyclerView.Adapter<ScannedResultListAdapter.ScannedResultListViewHolder>() {

    //Returns the total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return updateResultList.size
        //return updateFavouriteList.size
    }

    // create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedResultListViewHolder {
        return ScannedResultListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.scanhistoryqrresult, parent, false)
        )
    }

    //display the RecyclerView.ViewHolder contents with the item at the given position
    override fun onBindViewHolder(holder: ScannedResultListViewHolder, position: Int) {
        holder.QRresult.text = updateResultList[position].CheckInLocation
        holder.tvTime.text = updateResultList[position].Date
    }

        inner class ScannedResultListViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
                var QRresult : TextView = view.findViewById(R.id.QRresult)
                var tvTime : TextView = view.findViewById(R.id.tvTime)
        }


    }


