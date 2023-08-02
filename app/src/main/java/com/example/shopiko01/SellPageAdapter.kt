package com.example.shopiko01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.databinding.FragmentCatalogBinding
import com.example.shopiko01.databinding.FragmentSellPageBinding
import com.example.shopiko01.models.Item

import com.example.shopiko01.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class SellPageAdapter (val onItemClicked : (Item) -> Unit)
    : ListAdapter<Item, SellPageAdapter.SellPageViewHolder>(DiffItemCallBack) {

    inner class SellPageViewHolder(val binding : FragmentSellPageBinding)
            : RecyclerView.ViewHolder(binding.root) {

                fun bind(item: Item) {
                    binding.sellPageSellListName.text = item.truncateWords(item.itemName, 10)
                    binding.sellPageSellListQty.text = item.itemQuantity
                    binding.sellPageSellListPrice.text = item.getFormattedPrice(item.itemSellingPrice.toDouble())
                }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellPageViewHolder {
        val viewHolder = SellPageViewHolder(
            FragmentSellPageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false,)
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SellPageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private  val DiffItemCallBack = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return (oldItem.itemId == newItem.itemId)
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return (oldItem == newItem)
            }
        }
    }
}