package com.example.admin_shopswift

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.admin_shopswift.viewmodels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        lifecycleScope.launch {
            delay(500)
            viewModel.isACurrentUser.collect { isLoggedIn ->
                if (isAdded) {
                    if (isLoggedIn) {
                        startActivity(Intent(requireActivity(),AdminMainActivitty::class.java))
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_signinFragment)
                    }
                }
            }
        }

        return view
    }
}
