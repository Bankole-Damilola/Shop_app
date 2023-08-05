package com.example.shopiko01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiko01.databinding.FragmentSoldItemsBinding
import com.example.shopiko01.models.ItemSold
import com.example.shopiko01.models.ShopikoViewModel
import com.example.shopiko01.placeholder.SoldItemsAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [SoldItemsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoldItemsFragment : Fragment() {

    private var _binding : FragmentSoldItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ShopikoViewModel by activityViewModels()

    private lateinit var soldItemsAdapter : SoldItemsAdapter
    private val soldItemsList = mutableListOf<ItemSold>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoldItemsBinding.inflate(inflater, container, false)

        viewModel.itemsSoldFromFirebase.observe(this.viewLifecycleOwner) {

            myItemSold ->
            if (myItemSold != null) {
                for (itemSold in myItemSold) {
                    soldItemsList.add(itemSold)
                }
                Log.v("Sold Item Fragment", "List not empty")
            } else {
                Toast.makeText(context, "There hasn't been sales yet", Toast.LENGTH_LONG).show()
                Log.v("Sold Item Fragment", "List is empty")
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        soldItemsAdapter = SoldItemsAdapter {

            val bundle = bundleOf(
                "itemSoldId" to it.itemSoldId,
                "itemName" to it.itemName,
                "itemQuantity" to it.itemQuantity,
                "itemCumulativeQty" to it.itemCumulativeQty,
                "itemCategory" to it.itemCategory,
                "profitsOnItemSold" to it.profitsOnItemSold,
                "cumulativeProfitsOnItemSold" to it.cumulativeProfitsOnItemSold,
                "timeItemIsSold" to it.timeItemIsSold,
                "itemCostPrice" to it.itemCostPrice,
                "cumulativeItemCostPrice" to it.cumulativeItemCostPrice,
                "itemSellingPrice" to it.itemSellingPrice,
                "cumulativeItemSellingPrice" to it.cumulativeItemSellingPrice,
                "purchasedBy" to it.purchasedBy
            )

            findNavController().navigate(R.id.action_soldItemsFragment_to_viewSoldItemDetailsFragment, bundle)

        }

        binding.soldItemsRecyclerList.adapter = soldItemsAdapter
        soldItemsAdapter.submitList(soldItemsList)

        binding.soldItemsRecyclerList.layoutManager = LinearLayoutManager(this.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}