package org.secfirst.umbrella.feature.reader.view.rss

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_article_view.view.*
import org.jsoup.Jsoup
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.reader.Article
import org.secfirst.umbrella.data.database.reader.RSS
import org.secfirst.umbrella.misc.convertDateToString
import org.secfirst.umbrella.misc.shareLink

class ArticleCardAdapter(private val onClickLearnMore: (Article) -> Unit) : RecyclerView.Adapter<ArticleCardAdapter.CardHolder>() {

    private lateinit var items: MutableList<Article>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_article_view, parent, false)
        return CardHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(items[position], clickListener = { onClickLearnMore(items[position]) })
    }

    fun addAll(rss: RSS) {
        items = rss.items_.toMutableList()
    }

    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(articleItem: Article, clickListener: (CardHolder) -> Unit) {
            with(articleItem) {
                itemView.cardTitle.text = title
                itemView.cardDescription.text = Jsoup.parse(description_).text()
                itemView.cardLastUpdate.text = convertDateToString(publicationDate)
                itemView.cardOpenLink.setOnClickListener { clickListener(this@CardHolder) }
                itemView.cardShare.setOnClickListener { itemView.context.shareLink(link) }
                if (imageLink_ != "")
                    Picasso.get()
                            .load(imageLink_)
                            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.default_image) ?: ColorDrawable(Color.TRANSPARENT))
                            .into(itemView.cardImage)
                else
                    Picasso.get()
                            .load("nothing")
                            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.default_image) ?: ColorDrawable(Color.TRANSPARENT))
                            .into(itemView.cardImage)
            }
        }
    }
}