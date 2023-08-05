package com.example.shopiko01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgument
import com.example.shopiko01.databinding.FragmentViewBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ShopikoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass.
 * Use the [ViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewFragment : Fragment(), AddToSellPopUpFragment.OnQuantityProvided {

    private lateinit var binding : FragmentViewBinding
    private lateinit var item: Item

    private var addToSellPopUp : AddToSellPopUpFragment? = null

    private val viewModel : ShopikoViewModel by activityViewModels()

    // val args: ConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            binding.apply {
                item = viewModel.itemAttr(
                    arguments?.getString("itemId").toString(),
                    arguments?.getString("itemName").toString(),
                    arguments?.getString("itemCategory").toString(),
                    arguments?.getString("itemDescription").toString(),
                    arguments?.getString("itemCostPrice").toString(),
                    arguments?.getString("itemSellingPrice").toString(),
                    arguments?.getString("itemQuantity").toString()
                )
                bind(item)
            }

            binding.viewPageEditBtn.setOnClickListener {
                val bundle = bundleOf(
                    "itemId" to item.itemId,
                    "itemName" to item.itemName,
                    "itemCategory" to item.itemCategory,
                    "itemDescription" to item.itemDescription,
                    "itemCostPrice" to item.itemCostPrice,
                    "itemSellingPrice" to item.itemSellingPrice,
                    "itemQuantity" to item.itemQuantity
                )
                findNavController().navigate(R.id.action_viewFragment_to_addProductFragment2, bundle)
            }

            binding.viewPageDeleteBtn.setOnClickListener {
                showConfirmationDialog()
            }

            binding.viewPageAddToSellBtn.setOnClickListener {
                callPopUpAndGetQty()
            }

        }

//        if (id != null) {
//            viewModel.myData.observe(this.viewLifecycleOwner) { selectedItem ->
//                item = selectedItem
//                bind(item)
//            }
//        }
    }

    private fun callPopUpAndGetQty() {
            addToSellPopUp = AddToSellPopUpFragment()
            addToSellPopUp!!.setListener(this)
            addToSellPopUp!!.show(
                childFragmentManager,
                addToSellPopUp!!.tag
            )
    }

    private fun deleteItem() {
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    private fun bind(item: Item) {
        binding.apply {
            viewPageProductName.text = item.itemName.toString()
            viewPageCP.text = item.itemCostPrice.toString()
            viewPageSP.text = item.itemSellingPrice.toString()
            viewPageProfit.text = (viewModel.setProfit(item.itemCostPrice, item.itemSellingPrice))
            viewPageQty.text = item.itemQuantity.toString()
            viewPageDescription.text = item.truncateWords(item.itemDescription, 150)
        }
    }

    private fun showConfirmationDialog () {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Attention")
            .setMessage("Are you sure you want to delete")
            .setNeutralButton("No") {_, _ -> }
            .setPositiveButton("Yes") {
                    _, _ -> deleteItem()
            }
            .show()
    }

    override fun getQty(productQty: String, qtyField: TextInputEditText) {
        // This function sterns from the interface created in the pop fragment.
        // It gets the value
        // update the DB with a new value
        // and add the item to the sell list to the sell list
        if (item != null) {
            viewModel.updateItem(viewModel.itemAttr(
                item.itemId.toString(),
                item.itemName,
                item.itemCategory.toString(),
                item.itemDescription,
                item.itemCostPrice,
                item.itemSellingPrice,
                (item.itemQuantity.toInt() - productQty.toInt()).toString()
            ))
            viewModel.updateSellRecyclerListFromViewPage(
                viewModel.itemAttr(
                    item.itemId.toString(),
                    item.itemName,
                    item.itemCategory.toString(),
                    item.itemDescription,
                    item.itemCostPrice,
                    item.itemSellingPrice,
                    productQty
                )
            )
            val changedQty = item.itemQuantity
            item.itemQuantity = (changedQty.toInt() - productQty.toInt()).toString()
            qtyField.text = null
            addToSellPopUp!!.dismiss()
        }
        bind(item)
        snackbarToSellPage()
    }

    private fun snackbarToSellPage() {
        Snackbar.make(binding.viewPageForSnackPurpose, "Go to sell page", Snackbar.LENGTH_LONG)
            .setAction(
                "Click Here",
                View.OnClickListener {
                    findNavController().navigate(R.id.action_viewFragment_to_sellPageFragment3)
                }
            ).show()
        Log.v("ViewFragment", "View Page SnackBar Executed")
    }
}