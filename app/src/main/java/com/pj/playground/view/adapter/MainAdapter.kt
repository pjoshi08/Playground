package com.pj.playground.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pj.playground.R
import com.pj.playground.data.Step
import com.pj.playground.databinding.ListItemBinding


class MainAdapter(val data: List<Step>) : RecyclerView.Adapter<MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}

class MainViewHolder(private val item: ListItemBinding) : RecyclerView.ViewHolder(item.root) {

    fun bind(step: Step) {
        with(item) {
            header.text = step.number
            description.text = step.name
            caption.text = step.caption

            val context = item.root.context

            cardView.setOnClickListener {
                Intent(context, step.activity.java).also { context.startActivity(it) }
            }

            val color = if (step.highlight) {
                ContextCompat.getColor(context, R.color.secondaryLightColor)
            } else {
                ContextCompat.getColor(context, R.color.primaryTextColor)
            }
            header.setTextColor(color)
            description.setTextColor(color)
        }
    }
}