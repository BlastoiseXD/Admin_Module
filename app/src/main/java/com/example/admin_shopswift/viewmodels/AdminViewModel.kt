package com.example.admin_shopswift.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.admin_shopswift.Utils
import com.example.admin_shopswift.models.CartProducts
import com.example.admin_shopswift.models.Orders
import com.example.admin_shopswift.models.Products
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class AdminViewModel : ViewModel() {
    private val _isImageUploaded = MutableStateFlow(false)
    var isImageUploaded: StateFlow<Boolean> = _isImageUploaded

    private val _downloadUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadUrls: StateFlow<ArrayList<String?>> = _downloadUrls

    private val _isProductSaved = MutableStateFlow(false)
    val isProductSaved: StateFlow<Boolean> = _isProductSaved

    fun saveImageInDB(imageUri: ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId())
                .child("images")
                .child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->

                val url = task.result
                downloadUrls.add(url.toString())

                if (downloadUrls.size == imageUri.size) {
                    _isImageUploaded.value = true
                    _downloadUrls.value = downloadUrls
                }
            }
        }
    }

    fun saveProducts(product: Products) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products/${product.productRandomId}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins")
                    .child("Product Category/${product.productCategory}/${product.productRandomId}").setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child("Product Types/${product.productType}/${product.productRandomId}").setValue(product).addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }
    }

    fun fetchAllProducts(category: String): Flow<List<Products>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products") // ✅ Fixed Firebase path

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Products>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Products::class.java)
                    if (category == "All" || prod?.productCategory == category) {
                        products.add(prod!!)
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {}

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun savingUpdateProducts(product: Products) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("Product Category/${product.productCategory}/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("Product Types/${product.productType}/${product.productRandomId}").setValue(product)
    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getOrderedProducts(orderID: String): Flow<List<CartProducts>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderID)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList ?: emptyList()) // ✅ Added null safety
            }

            override fun onCancelled(error: DatabaseError) {}

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun updateOrderStatus(orderID: String, status: Int) {
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .child(orderID).child("orderStatus").setValue(status)
    }
}
