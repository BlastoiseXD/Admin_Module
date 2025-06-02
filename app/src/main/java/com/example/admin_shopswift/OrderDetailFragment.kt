package com.example.admin_shopswift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.admin_shopswift.adapter.AdapterCartProducts
import com.example.admin_shopswift.databinding.FragmentOrderDetailBinding
import com.example.admin_shopswift.viewmodels.AdminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var status = 0
    private var currentStatus = 0
    private var orderId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        getValues()
        settingStatus(status)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnChangeStatus.setOnClickListener { showStatusPopup(it) }
        binding.tbOrderFragmentDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment2)
        }
    }

    private fun showStatusPopup(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menuRecieved -> updateOrderStatus(1, "Order is already received")
                R.id.menuDispatched -> updateOrderStatus(2, "Order is already dispatched")
                R.id.menuDelivered -> updateOrderStatus(3, "Order is already delivered")
                else -> false
            }
        }
    }

    private fun updateOrderStatus(newStatus: Int, errorMessage: String): Boolean {
        if (newStatus > status) {
            status = newStatus
            settingStatus(newStatus)
            orderId?.let { viewModel.updateOrderStatus(it, newStatus) }
            return true
        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun settingStatus(status: Int) {
        val colorList = listOf(R.color.red, R.color.blue, R.color.blue, R.color.orange)
        val color = ContextCompat.getColorStateList(requireContext(), colorList[status])

        binding.apply {
            iv1.backgroundTintList = color
            if (status >= 1) iv2.backgroundTintList = color
            if (status >= 2) {
                iv3.backgroundTintList = color
                view1.backgroundTintList = color
            }
            if (status == 3) {
                iv4.backgroundTintList = color
                view3.backgroundTintList = color
            }
        }
    }

    private fun getValues() {
        arguments?.let {
            status = it.getInt("status", 0)
            orderId = it.getString("orderId")
            binding.tvUserAddress.text = it.getString("userAddress")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
