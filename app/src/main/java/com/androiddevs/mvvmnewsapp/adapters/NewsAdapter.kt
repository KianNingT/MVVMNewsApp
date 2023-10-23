package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>()  {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val ivArticleImage = itemView.findViewById(R.id.ivArticleImage) as ImageView
        val tvSource = itemView.findViewById(R.id.tvSource) as TextView
        val tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
        val tvDescription = itemView.findViewById(R.id.tvDescription) as TextView
        val tvPublishedAt = itemView.findViewById(R.id.tvPublishedAt) as TextView
    }

    //AsyncListDiffer callback is a callback used by the tool that compares our two list and only updates those items that changed.
    //Will execute in background thread
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        //Checks if two items are the same
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        //Checks if two items have the same content
        override fun areContentsTheSame(oldItemContent: Article, newItemContent: Article): Boolean {
            return oldItemContent == newItemContent
        }
    }

    //create async list differ. the tool that compares our two list and only updates those items that changed.
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        //create view holder
        //parent is the recycler view
        //false means we don't want to attach it to the root
        //we will attach it to the recycler view ourselves
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        //set view accordingly
        val article = differ.currentList[position]
        //Glide.with(this).load(article.urlToImage).into(holder.ivArticleImage)
        holder.apply {
            Glide.with(itemView).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            itemView.setOnClickListener {
                //invoke on item click listener
                onItemClickListener?.let { it(article) }
            }
        }
    }

    override fun getItemCount(): Int {
        //we don't have the item list that we pass in constructor usually. So we will use differ.currentList.size
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    //set on item click listener
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}