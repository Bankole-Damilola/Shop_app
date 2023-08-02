package com.example.shopiko01

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.shopiko01.databinding.FragmentAddCategoryPopUpBinding
import com.google.android.material.textfield.TextInputEditText


class AddCategoryPopUpFragment : DialogFragment() {

    private var _binding : FragmentAddCategoryPopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var listener : OnCategoryProvided

    // Listener to call pop up set in Home fragment
    fun setListener (listener : HomeFragment) {
        this.listener = listener
    }

    // Listener to call pop up set in Sell Fragment
    fun setSellListener (listener : SellPageFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddToCategoryPopUp"

        @JvmStatic
        fun newInstance(eventTitle: String) = AddCategoryPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("eventTitle", eventTitle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            binding.categoryPopUpTextView.text = requireArguments().getString("eventTitle").toString()
        }

        requiredEvents()
    }

    private fun requiredEvents() {
        binding.apply {
            categoryPopUpFab.setOnClickListener {
                val category = categoryPopUpText.text.toString()
                if (category != null) {
                    listener.getCategory(category, binding.categoryPopUpText)
                }
            }

            sellPopUpCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    interface OnCategoryProvided {
        fun getCategory(category: String, categoryTextField : TextInputEditText)
    }
}