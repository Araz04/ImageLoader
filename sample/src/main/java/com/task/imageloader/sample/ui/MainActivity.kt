package com.task.imageloader.sample.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.task.imageloader.sample.databinding.ActivityMainBinding
import com.task.imageloader.sample.stateholder.ImageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImageViewModel by viewModel()
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ImagesAdapter()
        binding.rvImages.layoutManager = GridLayoutManager(this, 2)
        binding.rvImages.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.images.observe(this) { images ->
            adapter.submitList(images)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvImages.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { error ->
            if (!error.isNullOrBlank()) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error
                binding.btnRetry.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
                binding.btnRetry.visibility = View.GONE
            }
        }

        viewModel.cacheClearedEvent.observe(this) { cleared ->
            if (cleared) {
                Toast.makeText(this, "Cache invalidated — images will reload", Toast.LENGTH_SHORT)
                    .show()
                adapter.notifyDataSetChanged()
                viewModel.onCacheClearedEventConsumed()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnInvalidateCache.setOnClickListener {
            viewModel.invalidateCache()
        }
        binding.btnRetry.setOnClickListener {
            binding.tvError.visibility = View.GONE
            binding.btnRetry.visibility = View.GONE
            viewModel.loadImages()
        }
    }
}