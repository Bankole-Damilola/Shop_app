package com.example.shopiko01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.databinding.DropdownLayoutBinding
import com.example.shopiko01.databinding.FragmentCatalogBinding
import com.example.shopiko01.models.ItemCategory
import com.example.shopiko01.models.ItemSold


class CategoryAdapter (
        val onItemClicked : (ItemCategory) -> Unit
) : ListAdapter<ItemCategory, CategoryAdapter.CategoryViewHolder>(DiffItemCallBack) {

    inner class CategoryViewHolder (val binding: DropdownLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category : ItemCategory) {
            binding.spinnerText.text = category.category.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val viewHolder = CategoryViewHolder(
            DropdownLayoutBinding
                .inflate(LayoutInflater.from(parent.context), parent, false,)
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private  val DiffItemCallBack = object : DiffUtil.ItemCallback<ItemCategory>() {
            override fun areItemsTheSame(oldItem: ItemCategory, newItem: ItemCategory): Boolean {
                return (oldItem.categoryId == newItem.categoryId)
            }

            override fun areContentsTheSame(oldItem: ItemCategory, newItem: ItemCategory): Boolean {
                return (oldItem == newItem)
            }

        }
    }


}