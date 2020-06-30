package com.example.sunflower.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sunflower.Plant
import com.example.sunflower.databinding.ListItemPlantBinding

class PlantAdapter : ListAdapter<Plant, RecyclerView.ViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlantViewHolder(
            ListItemPlantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant = getItem(position)
        (holder as PlantViewHolder).bind(plant)
    }

    class PlantViewHolder(
        private val binding: ListItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plant) {
            binding.apply {
                plant = item
                executePendingBindings()
            }
        }
    }

}

private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean =
        oldItem.plantId == newItem.plantId

    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean =
        oldItem == newItem

}