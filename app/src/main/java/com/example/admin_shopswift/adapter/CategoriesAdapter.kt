package com.example.admin_shopswift.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_shopswift.databinding.ItemViewProductCategoriesBinding
import com.example.admin_shopswift.models.Categories

class CategoriesAdapter(
    private val categoriesArrayList: ArrayList<Categories>,
   val  onCategoryClicked: (Categories) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {


    class CategoriesViewHolder(val binding: ItemViewProductCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = ItemViewProductCategoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoriesViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return categoriesArrayList.size
    }


    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoriesArrayList[position]
        holder.binding.apply {
        ivCategoryImage.setImageResource(category.icon)
            tvCategoryTitle.text = category.category

        }
        holder.itemView.setOnClickListener{
            onCategoryClicked(category)

        }
    }
}
