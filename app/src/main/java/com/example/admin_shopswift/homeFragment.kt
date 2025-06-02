package com.example.admin_shopswift


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin_shopswift.adapter.AdapterProduct
import com.example.admin_shopswift.adapter.CategoriesAdapter
import com.example.admin_shopswift.databinding.EditProductLayoutBinding
import com.example.admin_shopswift.databinding.FragmentHomeBinding
import com.example.admin_shopswift.models.Categories
import com.example.admin_shopswift.models.Products
import com.example.admin_shopswift.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterProduct: AdapterProduct
    private var alertDialog: AlertDialog? = null  // ✅ Prevent memory leaks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // ✅ Initialize adapterProduct once
        adapterProduct = AdapterProduct(::onEditButtonClicked)
        binding.rvproducts.adapter = adapterProduct

        searchProducts()
        setCategories()
        getAllTheProducts("All")  // ✅ Fetch products initially

        return binding.root
    }

    private fun searchProducts() {
        binding.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                if (::adapterProduct.isInitialized && adapterProduct.filter != null) {
                    adapterProduct.filter?.filter(query)
                }
            }
        })
    }

    private fun getAllTheProducts(category: String) {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts(category).collect { products ->
                requireActivity().runOnUiThread {
                    adapterProduct.submitList(products)
                    binding.rvproducts.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
                    binding.tvText.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun setCategories() {
        val categoryList = ArrayList<Categories>()
        for (i in Constant.allProductCategoryIcon.indices) {
            categoryList.add(Categories(Constant.allproductCategory[i], Constant.allProductCategoryIcon[i]))
        }
        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryClicked)
    }

    private fun onCategoryClicked(categories: Categories) {
        getAllTheProducts(categories.category)
    }

    private fun onEditButtonClicked(product: Products) {
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductTitle.setText(product.productTitle)
            etProductQuantity.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductPrice.setText(product.productPrice.toString())
            etProductStock.setText(product.productStock.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog?.show()

        editProduct.btnEdit.setOnClickListener {
            editProduct.apply {
                etProductTitle.isEnabled = true
                etProductQuantity.isEnabled = true
                etProductUnit.isEnabled = true
                etProductPrice.isEnabled = true
                etProductStock.isEnabled = true
                etProductCategory.isEnabled = true
                etProductType.isEnabled = true
            }
        }

        setAutoCompleteTextView(editProduct)

        editProduct.btnsave.setOnClickListener {
            lifecycleScope.launch {
                product.apply {
                    productTitle = editProduct.etProductTitle.text.toString()
                    productQuantity = editProduct.etProductQuantity.text.toString().toIntOrNull() ?: 0
                    productUnit = editProduct.etProductUnit.text.toString()
                    productPrice = editProduct.etProductPrice.text.toString().toIntOrNull() ?: 0
                    productStock = editProduct.etProductStock.text.toString().toIntOrNull() ?: 0
                    productCategory = editProduct.etProductCategory.text.toString()
                    productType = editProduct.etProductType.text.toString()
                }
                viewModel.savingUpdateProducts(product)
            }

            Toast.makeText(requireContext(), "Saving changes", Toast.LENGTH_SHORT).show()
            alertDialog?.dismiss()
        }
    }

    private fun setAutoCompleteTextView(editProduct: EditProductLayoutBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allUnitsOfProduct)
        val category = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allproductCategory)
        val productType = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allProductType)
        editProduct.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()  // ✅ Prevent memory leaks
    }
}
