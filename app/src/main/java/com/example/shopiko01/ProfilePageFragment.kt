package com.example.shopiko01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.databinding.FragmentProfilePageBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilePageFragment : Fragment() {

    private lateinit var binding : FragmentProfilePageBinding

    private lateinit var user : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance()

        if (user.currentUser != null) {
            user.currentUser?.let {
                binding.profilePageShopEmail.text = it.email
            }
        }

        binding.profilePageLogOutCardView.setOnClickListener {
            signOutUser()
        }
        bottomNavDestinations()

        binding.profileIcon.setImageResource(R.drawable.ic_profile)

    }

    private fun signOutUser () {
        user.signOut()
        findNavController().navigate(R.id.action_profilePageFragment3_to_signInFragment)
    }

    private fun bottomNavDestinations() {
        binding.apply {
            homeIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_homeFragment3)
            }

            sellIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_sellPageFragment3)
            }

            analyticIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_analyticsPageFragment3)
            }

            catalogIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_catalogFragment3)
            }

            profilePageCategoryCardView.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_viewCategoryFragment)
            }

            profilePageSalesHistoryCardView.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_soldItemsFragment)
            }

            profilePageUpdateProfileCardView.setOnClickListener {
                findNavController().navigate(R.id.action_profilePageFragment3_to_updateProfileDetailsFragment)
            }
        }
    }
}