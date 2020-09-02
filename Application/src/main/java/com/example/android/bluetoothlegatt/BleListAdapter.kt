package com.example.android.bluetoothlegatt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BleListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<BleListAdapter.BleViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bles = emptyList<BleListEntity>() // Cached copy of words

    inner class BleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return BleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        val current = bles[position]
        holder.wordItemView.text = current.name
    }

    internal fun setBles(bles: List<BleListEntity>) {
        this.bles = bles
        notifyDataSetChanged()
    }

    override fun getItemCount() = bles.size
}