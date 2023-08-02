package com.example.shopiko01.placeholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.databinding.DropdownLayoutBinding

class SellDropDownAdapter (
    private val onItemClicked : (String) -> Unit
): ListAdapter<String, SellDropDownAdapter.SellDropDownViewHolder>(DiffItemCallBack) {

    class SellDropDownViewHolder(val binding: DropdownLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: String) {
            binding.spinnerText.text = itemName
        }
    }

    override fun onBindViewHolder(holder: SellDropDownViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellDropDownViewHolder {
        val viewHolder = SellDropDownViewHolder(DropdownLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return  viewHolder
    }

    companion object{
            private val DiffItemCallBack = object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return (oldItem == newItem)
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return (oldItem == newItem)
                }

            }
    }
}
