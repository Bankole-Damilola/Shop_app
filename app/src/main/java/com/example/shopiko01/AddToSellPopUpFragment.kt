package com.example.shopiko01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.shopiko01.databinding.FragmentAddToSellPopUpBinding
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass.
 * Use the [AddToSellPopUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddToSellPopUpFragment : DialogFragment() {

    private var _binding : FragmentAddToSellPopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var listener : OnQuantityProvided

    fun setListener (listener : ViewFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddToSellPopUp"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToSellPopUpBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requiredEvents()
    }

    private fun requiredEvents() {
        binding.apply {
            sellPopUpFab.setOnClickListener {
                val qty = sellPopUpText.text.toString()
                if (qty != null) {
                    listener.getQty(qty, binding.sellPopUpText)
                }
            }

            sellPopUpCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    interface OnQuantityProvided {
        fun getQty(productQty : String, qtyField: TextInputEditText)
    }
}