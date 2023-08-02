package com.example.shopiko01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.databinding.FragmentAnalyticsPageBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AnalyticsPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalyticsPageFragment : Fragment() {

    private lateinit var binding : FragmentAnalyticsPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.analyticIcon.setImageResource(R.drawable.ic_analytics)

        bottomNavDestinations()
    }

    private fun bottomNavDestinations() {
        binding.apply {
            homeIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_homeFragment3)
            }

            sellIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_sellPageFragment3)
            }

            catalogIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_catalogFragment3)
            }

            profileIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_profilePageFragment3)
            }
        }
    }


}