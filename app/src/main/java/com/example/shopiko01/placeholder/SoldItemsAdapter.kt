package com.example.shopiko01.placeholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.databinding.EachSoldItemTemplateBinding
import com.example.shopiko01.models.ItemSold

class SoldItemsAdapter (
    val onItemClicked : (ItemSold) -> Unit
        ) : ListAdapter<ItemSold, SoldItemsAdapter.SoldItemsViewHolder>(DiffItemCallBack) {

    class SoldItemsViewHolder (val binding : EachSoldItemTemplateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind (itemSold: ItemSold) {
            binding.eachItemSoldDate.text = itemSold.timeItemIsSold
            binding.eachItemSoldBuyerText.text = itemSold.purchasedBy
            binding.eachItemSoldNameText.text = itemSold.itemName
            binding.eachItemSoldQtyText.text = itemSold.itemQuantity
            binding.eachItemSoldSpText.text = itemSold.itemSellingPrice
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoldItemsAdapter.SoldItemsViewHolder {

        val viewHolder = SoldItemsViewHolder(
            EachSoldItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SoldItemsAdapter.SoldItemsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        val DiffItemCallBack = object  : DiffUtil.ItemCallback<ItemSold>() {
            override fun areItemsTheSame(oldItem: ItemSold, newItem: ItemSold): Boolean {
                return (oldItem.itemSoldId == newItem.itemSoldId)
            }

            override fun areContentsTheSame(oldItem: ItemSold, newItem: ItemSold): Boolean {
                return (oldItem == newItem)
            }

        }
    }


}