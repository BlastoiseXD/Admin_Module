package com.example.admin_shopswift.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin_shopswift.databinding.ItemViewCartProductsBinding
import com.example.admin_shopswift.models.CartProducts

class AdapterCartProducts : RecyclerView.Adapter<AdapterCartProducts.CartProductsViewHolder>() {

    class CartProductsViewHolder(val binding: ItemViewCartProductsBinding) : RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            ItemViewCartProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivProdudctImage)
            tvProductTitle.text = product.productTitle
            tvProductQuantity.text = product.productQuantity
            tvProductPrice.text = product.productPrice
            tvProductCount.text = product.productCount.toString()
        }
    }
}
