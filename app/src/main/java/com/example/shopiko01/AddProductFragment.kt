package com.example.shopiko01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlin.math.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shopiko01.databinding.FragmentAddProductBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ShopikoViewModel
import com.example.shopiko01.placeholder.DropdownAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [AddProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductFragment : Fragment() {

    private var _binding : FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ShopikoViewModel by activityViewModels()
    private var item : Item? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            addPageDisplayProfit.setOnClickListener {
                val itemCostPrice = addPageCostPriceText.text.toString()
                val itemSellingPrice = addPageSellPriceText.text.toString()

                if (itemCostPrice.isNotEmpty() && itemSellingPrice.isNotEmpty()) {
                    val profit = setProfit(itemCostPrice, itemSellingPrice)
                    addPageProfitMarginInput.text = profit
                }
            }
        }

        if (arguments != null) {
            item = viewModel.itemAttr(
                arguments?.getString("itemId").toString(),
                arguments?.getString("itemName").toString(),
                arguments?.getString("itemCategory").toString(),
                arguments?.getString("itemDescription").toString(),
                arguments?.getString("itemCostPrice").toString(),
                arguments?.getString("itemSellingPrice").toString(),
                arguments?.getString("itemQuantity").toString()
            )

            binding.apply {
                addProductPageProductNameEditText.setText(item?.itemName)
                addPageCategoryDropdownText.setText(item!!.itemCategory)
                addPageProductDescriptionText.setText(item?.itemDescription)
                addPageCostPriceText.setText(item?.itemCostPrice)
                addPageSellPriceText.setText(item?.itemSellingPrice)
                addPageProductQtyText.setText(item?.itemQuantity)
                addPageAddProductBtn.setText(R.string.update_item)
            }
        }
        requiredEvents()
    }

    private fun requiredEvents() {
        binding.apply {
            addPageAddProductBtn.setOnClickListener {
                val itemName = addProductPageProductNameEditText.text.toString()
                val itemCategory = addPageCategoryDropdownText.text.toString()
                val itemDescription = addPageProductDescriptionText.text.toString()
                val itemCostPrice = addPageCostPriceText.text.toString()
                val itemSellingPrice = addPageSellPriceText.text.toString()
                val itemQuantity = addPageProductQtyText.text.toString()

                if (
                    itemName.isNotEmpty() && itemCategory.isNotEmpty() && itemDescription.isNotEmpty()
                    && itemCostPrice.isNotEmpty() && itemSellingPrice.isNotEmpty()
                    && itemQuantity.isNotEmpty()
                ) {

                    if (item == null) {
                        val itemAttribute = viewModel.itemAttr(viewModel.databaseRef.push().key!!,
                            itemName, itemCategory, itemDescription, itemCostPrice, itemSellingPrice, itemQuantity
                        )
                        viewModel.insertItem(itemAttribute)
                    } else {
                        val itemAttribute = viewModel.itemAttr(item?.itemId.toString(),
                            itemName, itemCategory, itemDescription, itemCostPrice, itemSellingPrice, itemQuantity
                        )
                        viewModel.updateItem(itemAttribute)
                    }
                    findNavController().navigateUp()

                    setFieldsToNull()
                }
            }
        }
    }

    private fun setFieldsToNull () {
        Toast.makeText(context, "Item added successfully", Toast.LENGTH_LONG).show()
        binding.apply {
            addProductPageProductNameEditText.text = null
            addPageCategoryDropdownText.text = null
            addPageProductDescriptionText.text = null
            addPageCostPriceText.text = null
            addPageSellPriceText.text = null
            addPageProductQtyText.text = null
        }
    }

    override fun onResume() {
        super.onResume()
        val itemCategories = arrayListOf<String>()

        viewModel.categoriesFromFirebase.observe(this.viewLifecycleOwner) {
            categories -> for (category in categories) {
                itemCategories.add(category.category)
        }
        }
        binding.apply {
            val adapter = DropdownAdapter(requireContext(), R.layout.dropdown_layout, itemCategories)
            addPageCategoryDropdownText.setAdapter(adapter)
        }
    }

    private fun setProfit(cp: String, sp: String): String {
        return "%.2f".format(((sp.toDouble() - cp.toDouble()) / cp.toDouble() * 100))
    }
}
