package com.pj.playground.presentation.reader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pj.playground.R
import com.pj.playground.domain.Bookmark
import kotlinx.android.synthetic.main.item_bookmark.view.*

class BookmarkAdapter(
    private val bookmarks: MutableList<Bookmark> = mutableListOf(),
    private val itemClickListener: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = holder.itemView.resources.getString(
            R.string.page_bookmark_format,
            bookmarks[position].page
        )
        holder.itemView.setOnClickListener { itemClickListener(bookmarks[position]) }
    }

    override fun getItemCount() = bookmarks.size

    fun update(newBookmarks: List<Bookmark>) {
        bookmarks.clear()
        bookmarks.addAll(newBookmarks)

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.bookmarkNameTextView
    }
}