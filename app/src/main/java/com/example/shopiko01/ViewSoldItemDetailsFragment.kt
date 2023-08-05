package com.example.shopiko01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopiko01.databinding.FragmentViewSoldItemDetailsBinding
import com.example.shopiko01.models.ItemSold

/**
 * A simple [Fragment] subclass.
 * Use the [ViewSoldItemDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewSoldItemDetailsFragment : Fragment() {

    private var _binding : FragmentViewSoldItemDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemSold : ItemSold

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentViewSoldItemDetailsBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {

            itemSold = ItemSold(
                arguments?.getString("itemSoldId").toString(),
                arguments?.getString("itemName").toString(),
                arguments?.getString("itemQuantity").toString(),
                arguments?.getString("itemCumulativeQty").toString(),
                arguments?.getString("itemCategory").toString(),
                arguments?.getString("profitsOnItemSold").toString(),
                arguments?.getString("cumulativeProfitsOnItemSold").toString(),
                arguments?.getString("timeItemIsSold").toString(),
                arguments?.getString("itemCostPrice").toString(),
                arguments?.getString("cumulativeItemCostPrice").toString(),
                arguments?.getString("itemSellingPrice").toString(),
                arguments?.getString("cumulativeItemSellingPrice").toString(),
                arguments?.getString("purchasedBy").toString(),
            )

            bind(itemSold)
        }

    }

    private fun bind (itemSold: ItemSold) {

        binding.apply {

            soldItemDate.text = resources.getString(R.string.transaction_performance, itemSold.timeItemIsSold)
            soldItemName.text = itemSold.itemName
            soldItemBuyerName.text = itemSold.purchasedBy
            soldItemQty.text = itemSold.itemQuantity
            soldItemCumQty.text = itemSold.itemCumulativeQty
            soldItemProfit.text = itemSold.profitsOnItemSold
            soldItemCategory.text = itemSold.itemCategory
            soldItemCp.text = itemSold.itemCostPrice
            soldItemSp.text = itemSold.itemSellingPrice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}