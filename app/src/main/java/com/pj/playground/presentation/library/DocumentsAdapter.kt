package com.pj.playground.presentation.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.pj.playground.R
import com.pj.playground.domain.Document
import com.pj.playground.util.readableFileSize
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentsAdapter(
    private val documents: MutableList<Document> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: (Document) -> Unit
) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
    )

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        glide.load(documents[position].thumbnail)
            .error(glide.load(R.drawable.preview_missing))
            .into(ivPreview)

        ivPreview.setImageResource(R.drawable.preview_missing)
        tvTitle.text = documents[position].name
        tvSize.text = readableFileSize(documents[position].size)
        itemView.setOnClickListener { itemClickListener(documents[position]) }
    }

    fun update(newDocuments: List<Document>) {
        documents.clear()
        documents.addAll(newDocuments)

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPreview = view.ivPreview
        val tvTitle = view.tvTitle
        val tvSize = view.tvSize
    }

}