package com.example.admin_shopswift.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_shopswift.R
import com.example.admin_shopswift.databinding.ItemViewOrdersBinding
import com.example.admin_shopswift.models.OrderedItems

class AdapterOrders(val onOrderItemViewClicked: (OrderedItems) -> Unit) :
    RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: ItemViewOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderedItems) {
            binding.apply {
                tvOrderTitles.text = order.itemTitle
                tvOrderDate.text = order.itemDate
                tvOrderAmount.text = "Npr ${order.itemPrice}" // Added space for better readability

                val context = itemView.context

                when (order.itemStatus) {
                    0 -> {
                        tvOrderStatus.text = "Ordered"
                        tvOrderStatus.backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.orange)
                    }
                    1 -> {
                        tvOrderStatus.text = "Received"
                        tvOrderStatus.backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.blue)
                    }
                    2 -> {
                        tvOrderStatus.text = "Dispatched"
                        tvOrderStatus.backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.red)
                    }
                    3 -> {
                        tvOrderStatus.text = "Delivered"
                        tvOrderStatus.backgroundTintList =
                            ContextCompat.getColorStateList(context, R.color.green) // Changed to green for better UX
                    }
                }

                root.setOnClickListener {
                    onOrderItemViewClicked(order)
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val binding = ItemViewOrdersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrdersViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}
