package com.example.shopiko01.authetication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.R
import com.example.shopiko01.databinding.FragmentSignInBinding
import com.example.shopiko01.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var user : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance()
        requiredEvent()

    }

    private fun requiredEvent() {
        binding.signUpPageSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.signUpPageBtn.setOnClickListener {
            val shopName = binding.signUpShopName.text.toString().trim()
            val email = binding.signUpPageEmailText.text.toString().trim()
            val password = binding.signUpPagePasswordText.text.toString().trim()
            val retypePassword = binding.signUpPageRePasswordText.text.toString().trim()

            if (shopName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                if (password == retypePassword) {
                    val bundle = bundleOf(
                        "shopName" to shopName
                    )
                    user.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Your shop has been created successfully", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_signUpFragment_to_homeFragment3, bundle)
                        } else {
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    binding.signUpPageRePasswordLayout.error
                    binding.signUpPageRePasswordLayout.errorContentDescription = "Password does not match"
                }
            } else {
                Toast.makeText(context, "No field should be left blank", Toast.LENGTH_LONG).show()
            }
        }
    }
}