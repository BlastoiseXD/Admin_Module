package com.example.admin_shopswift

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admin_shopswift.databinding.FragmentOTPBinding
import com.example.admin_shopswift.models.Users
import com.example.admin_shopswift.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(inflater, container, false)
        getUserNumber()
        customizeEditText()
        sendOTP()
        onLoginButtonClicked()

        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.login.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing.....You")

            val editTexts = arrayOf(
                binding.otp1,
                binding.ot2,
                binding.ot3,
                binding.otp4,
                binding.otp5,
                binding.op6
            )
            val otp = editTexts.joinToString("") { it.text.toString() }
            if (otp.length < editTexts.size) {
                Toast.makeText(requireContext(), "Please Enter Correct OTP", Toast.LENGTH_LONG).show()
                Utils.hideDialog()
            } else {
                verifyOTP(otp)
            }
        }
    }
    private fun verifyOTP(otp: String) {
        val user = Users(uid = null, userPhoneNumber = userNumber, userAddress = null)
        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect {
                if (it) {
                    Utils.hideDialog()
                    Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), AdminMainActivitty::class.java))
                    requireActivity().finish()
                } else {

                    Utils.hideDialog()

                }
            }
        }
    }


    private fun customizeEditText() {
        val editTexts = arrayOf(
            binding.otp1,
            binding.ot2,
            binding.ot3,
            binding.otp4,
            binding.otp5,
            binding.op6
        )

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.isEmpty() == true) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }
            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number") ?: ""
    }

    private fun sendOTP() {
        Toast.makeText(requireContext(), "Sending OTP", Toast.LENGTH_SHORT).show()
        viewModel.apply {
            sendOtp(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {
                    if (it) {
                        Utils.hideDialog()
                        Toast.makeText(requireContext(), "OTP SENT.....", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
