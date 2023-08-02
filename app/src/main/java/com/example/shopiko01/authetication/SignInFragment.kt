package com.example.shopiko01.authetication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.R
import com.example.shopiko01.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment() {

    private var _binding : FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var user : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance()
        requiredEvent()
    }

    private fun requiredEvent() {
        binding.signInPageSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.signInPageBtn.setOnClickListener {
            val email = binding.signInPageEmail.text.toString().trim()
            val password = binding.signInPagePassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                user.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Login successfully", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_signInFragment_to_homeFragment3)
                    } else {
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "No field should be left blank", Toast.LENGTH_LONG).show()
            }
        }
    }
}