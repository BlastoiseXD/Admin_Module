package com.example.admin_shopswift

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.admin_shopswift.databinding.FragmentSigninBinding

class signinFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        getUserNumber()
        onContinueButtonClick()
        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.signIn.setOnClickListener {
            val number = binding.userNumber.text.toString()
            if (number.isEmpty() || number.length != 10) {
                Toast.makeText(context, "Please Enter a Valid Phone Number", Toast.LENGTH_LONG).show()
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signinFragment_to_OTPFragment, bundle)
            }
        }
    }

    private fun getUserNumber() {
        binding.userNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val len = s?.length ?: 0
                if (len == 10) {
                    binding.signIn.isEnabled = true
                    binding.signIn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Green_v1))
                } else {
                    binding.signIn.isEnabled = false
                    binding.signIn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }
}
