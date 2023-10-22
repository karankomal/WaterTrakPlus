package com.example.watertrak

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yangp.ypwaveview.YPWaveView


class ItemAdapter(private val context: Context, private val items: List<DisplayItem>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemDateTV: TextView = itemView.findViewById(R.id.itemDateTV)
        val waterDrankTV: TextView = itemView.findViewById(R.id.waterDrankTV)
        val itemDescTV: TextView = itemView.findViewById(R.id.itemDescTV)
        val waveView: YPWaveView = itemView.findViewById(R.id.waveView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = items.get(position)
        holder.itemDateTV.text = listItem.date
        holder.waterDrankTV.text = listItem.waterDrank + "L"
        holder.itemDescTV.text = listItem.desc
        var progress = (listItem.waterDrank!!.toDouble() / 3.7 * 100).toInt()
        if (progress > 100)
            progress = 100
        holder.waveView.progress = progress
        holder.waveView.startAnimation()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}