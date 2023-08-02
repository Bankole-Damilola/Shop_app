package com.example.shopiko01.authetication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.R
import com.example.shopiko01.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {


    private var _binding : FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var user : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance()

        Handler(Looper.myLooper()!!).postDelayed({

            if (user.currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment3)
            } else findNavController().navigate(R.id.action_splashFragment_to_requestFragment)
        }, 3000)
    }
}