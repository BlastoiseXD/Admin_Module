package com.example.admin_shopswift.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_shopswift.databinding.ItemViewImageSelectionBinding

class AdapterSelectedImage(private val imageUris: ArrayList<Uri>) : RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {

    class SelectedImageViewHolder(val binding: ItemViewImageSelectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val binding = ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val image = imageUris[position]
        holder.binding.ivImage.setImageURI(image)

        holder.binding.closeButton.setOnClickListener {
            if (position < imageUris.size) {
                imageUris.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, imageUris.size)
            }
        }
    }
}
