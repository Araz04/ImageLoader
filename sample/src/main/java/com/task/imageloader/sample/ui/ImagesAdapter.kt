package com.task.imageloader.sample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.task.imageloader.core.ImageLoader
import com.task.imageloader.sample.R
import com.task.imageloader.sample.databinding.ItemImageBinding
import com.task.imageloader.sample.model.ImageItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImagesAdapter : ListAdapter<ImageItem, ImagesAdapter.ImageViewHolder>(DIFF_CALLBACK),
    KoinComponent {

    private val imageLoader: ImageLoader by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {
            binding.tvImageId.text = item.id.toString()
            imageLoader.load(
                target = binding.ivImage,
                url = item.imageUrl,
                placeholderRes = R.drawable.ic_placeholder
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageItem>() {
            override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id
            override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
        }
    }
}
