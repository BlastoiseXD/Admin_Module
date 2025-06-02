package com.example.admin_shopswift.adapter

import com.example.admin_shopswift.FilteringProducts
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.admin_shopswift.databinding.ItemViewProductBinding
import com.example.admin_shopswift.models.Products

class AdapterProduct(
    val onEditButtonClicked: (Products) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {

    // ViewHolder class for binding the item view
    class ProductViewHolder(val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root)

    // DiffUtil for comparing items and their contents
    private val diffutil = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }

    // AsyncListDiffer to manage the list and updates efficiently
    val differ = AsyncListDiffer(this, diffutil)

    // Creating the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Return the size of the list
    override fun getItemCount(): Int = differ.currentList.size

    // Binding the data to the views
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()

            // Safe way to handle productImageUris
            product.productImageUris?.let { images ->
                for (imageUri in images) {
                    imageList.add(SlideModel(imageUri.toString()))
                }
            }

            // Set the image list in the image slider
            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.productTitle

            // Format and set product quantity
            val quantity = "${product.productQuantity} ${product.productUnit}"
            tvProductQuantity.text = quantity

            // Set product price
            tvProductPrice.text = "Npr ${product.productPrice}"
        }

        // Handle item click event
        holder.itemView.setOnClickListener {
            onEditButtonClicked(product)
        }
    }

    // Method to submit a new list to AsyncListDiffer
    fun submitList(list: List<Products>) {
        differ.submitList(list)
    }

    private var filter: FilteringProducts? = null
    var originalList = ArrayList<Products>()

    // Filtering logic using the com.example.admin_shopswift.FilteringProducts class
    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilteringProducts(this, originalList)
        }
        return filter!!
    }
}
