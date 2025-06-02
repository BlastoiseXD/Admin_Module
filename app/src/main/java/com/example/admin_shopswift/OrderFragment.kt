package com.example.admin_shopswift

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.admin_shopswift.adapter.AdapterOrders
import com.example.admin_shopswift.databinding.FragmentOrderBinding
import com.example.admin_shopswift.models.OrderedItems
import com.example.admin_shopswift.viewmodels.AdminViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList


class orderFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentOrderBinding
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)
        getAllOrders()

        return binding.root


    }
    private fun getAllOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllOrders().collect { orderList ->
                if (orderList.isNotEmpty()) {
                    val orderedList = ArrayList<OrderedItems>()
                    for (orders in orderList) {
                        val title = StringBuilder()
                        var totalPrice = 0
                        for (products in orders.orderList!!) {
                            val price = products.productPrice?.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)
                            title.append("${products.productCategory},")
                        }
                        val orderedItems = OrderedItems(
                            orders.orderId,
                            orders.orderDate,
                            orders.orderStatus,
                            title.toString(),
                            totalPrice,
                            orders.userAddress
                        )
                        orderedList.add(orderedItems)
                    }
                    adapterOrders = AdapterOrders(::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                }
            }
        }
    }

    fun onOrderItemViewClicked(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status",orderedItems.itemStatus!!)
        bundle.putString("orderID",orderedItems.orderId)
        bundle.putString("userAddress",orderedItems.userAddress)
        findNavController().navigate(R.id.action_orderFragment2_to_orderDetailFragment,bundle)


    }



}