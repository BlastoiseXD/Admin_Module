package com.example.admin_shopswift
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.admin_shopswift.adapter.AdapterSelectedImage
import com.example.admin_shopswift.databinding.FragmentAddproductBinding
import com.example.admin_shopswift.models.Products
import com.example.admin_shopswift.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddproductBinding
    private val imageUris: ArrayList<Uri> = arrayListOf()

    val selectedImage =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listofUri ->
            val fiveImage = listofUri.take(5)
            imageUris.clear()
            imageUris.addAll(fiveImage)
            binding.ProductImage.adapter = AdapterSelectedImage(imageUris)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddproductBinding.inflate(inflater, container, false)
        setAutoCompleteTextView()
        onImageSelectClicked()
        onAddButtonClicked()

        return binding.root
    }

    private fun onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading Images")
            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etProductQuantity.text.toString()
            val productUnit = binding.etProductUnit.text.toString()
            val productPrice = binding.etProductPrice.text.toString()
            val productStock = binding.ProductStock.text.toString()
            val productCategory = binding.ProductCategory.text.toString()
            val productType = binding.ProductType.text.toString()

            // Validate if any field is empty
            if (productTitle.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty() ||
                productPrice.isEmpty() || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty()
            ) {
                Utils.apply {
                    hideDialog()
                    Toast.makeText(requireContext(), "Empty field cannot be taken", Toast.LENGTH_SHORT).show()
                }
            } else if (imageUris.isEmpty()) {
                Utils.apply {
                    hideDialog()
                }
                Toast.makeText(requireContext(), "Please upload some images", Toast.LENGTH_SHORT).show()
            } else {
                val product = Products(
                    productTitle = productTitle,
                    productQuantity = productQuantity.toInt(),
                    productUnit = productUnit,
                    productStock = productStock.toInt(),
                    productCategory = productCategory,
                    productType = productType,
                    itemCount = 0,
                    adminUid = Utils.getCurrentUserId(),
                    productRandomId = Utils.getRandomId()
                )

                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Products) {
        viewModel.saveImageInDB(imageUris)

        lifecycleScope.launch {
            // Observe the image upload state only once
            val isUploaded = viewModel.isImageUploaded.first()
            if (isUploaded) {
                Utils.apply {
                    hideDialog()
                    Toast.makeText(requireContext(), "Image Saved", Toast.LENGTH_SHORT).show()
                }
                getUrls(product)
            }
        }
    }

    private fun getUrls(product: Products) {
        Utils.showDialog(requireContext(), "Publishing Product...")

        lifecycleScope.launch {
            val urls = viewModel.downloadUrls.first()
            val nonNullUrls = urls.filterNotNull()
            product.productImageUris = ArrayList(nonNullUrls)
            saveProduct(product)
        }
    }

    private fun saveProduct(product: Products) {
        viewModel.saveProducts(product)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isProductSaved.collectLatest {
                    if (it) {
                        Utils.hideDialog()
                        startActivity(Intent(requireActivity(), AdminMainActivitty::class.java))
                        Toast.makeText(requireContext(), "Your Product is live", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun onImageSelectClicked() {
        binding.btnSelectImage.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextView() {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allUnitsOfProduct)
        val category = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allproductCategory)
        val productType = ArrayAdapter(requireContext(), R.layout.show_list, Constant.allProductType)
        binding.apply {
            etProductUnit.setAdapter(units)
            ProductCategory.setAdapter(category)
            ProductType.setAdapter(productType)
        }
    }
}
