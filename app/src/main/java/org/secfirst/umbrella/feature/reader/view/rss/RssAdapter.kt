package org.secfirst.umbrella.feature.reader.view.rss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rss_item_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.reader.RSS

class RssAdapter(private val onClickPress: (RSS) -> Unit) : RecyclerView.Adapter<RssAdapter.RssHolder>() {

    private val rssList: MutableList<RSS> = mutableListOf()

    fun removeAt(position: Int) {
        rssList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun getAt(position: Int) = rssList[position]


    override fun getItemViewType(position: Int) = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rss_item_view, parent, false)
        return RssHolder(view)
    }

    override fun getItemCount() = rssList.size

    override fun onBindViewHolder(holder: RssHolder, position: Int) {
        holder.bind(rssList[position], clickListener = { onClickPress(rssList[position]) })
    }

    fun addAll(feedList: List<RSS>) {
        feedList.forEach { rssList.add(it) }
        notifyDataSetChanged()
    }

    fun add(rss: RSS) {
        rssList.add(rss)
        notifyItemInserted(rssList.size)
    }

    fun size() = rssList.size

    fun removeAll() {
        rssList.clear()
        notifyDataSetChanged()
    }

    fun getRss(): MutableList<RSS> = rssList

    fun remove(rss: RSS) {
        rssList.remove(rss)
        notifyDataSetChanged()
    }

    class RssHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(rss: RSS, clickListener: (RssHolder) -> Unit) {
            with(rss) {
                itemView.rssTitle.text = title
                itemView.rssDescription.text = description
                itemView.setOnClickListener { clickListener(this@RssHolder) }
            }
        }
    }
}

