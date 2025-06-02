package com.example.admin_shopswift

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.cloudinary.android.MediaManager
import com.example.admin_shopswift.databinding.ActivityAdminMainActivittyBinding

class AdminMainActivitty : AppCompatActivity() {
    private lateinit var binding: ActivityAdminMainActivittyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainActivittyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initconfig() // Initialize Cloudinary config

        val navController = findNavController(R.id.fragmentContainerView2)
        NavigationUI.setupWithNavController(binding.bottomMenu, navController)
    }

    private fun initconfig() {
        val config: MutableMap<Any?, Any?> = HashMap()
        config["cloud_name"] = "dlcwn72j1"
        config["api_key"] = "117125321335488"
        config["api_secret"] = "50fp77HDOaVkLeras4YhMEdizgE"
        MediaManager.init(applicationContext, config)
    }
}
