package com.example.shopiko01

import android.annotation.SuppressLint
import android.os.Bundle
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.databinding.FragmentSellPageListBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ItemSold
import com.example.shopiko01.models.ShopikoViewModel
import com.example.shopiko01.placeholder.DropdownAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections


/**
 * A fragment representing a list of Items.
 */
class SellPageFragment : Fragment(), AddCategoryPopUpFragment.OnCategoryProvided {

    private var _binding : FragmentSellPageListBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ShopikoViewModel by activityViewModels()
    private val items = arrayListOf<Item>()
    private val soldItems = arrayListOf<ItemSold>()
    private val dataNamesAndQty = arrayListOf<String>()
    private lateinit var sellPageAdapter: SellPageAdapter

    // For the sake of getting the name of an item purchaser
    private var addPurchaserNamePopUp : AddCategoryPopUpFragment? = null
    // private lateinit var myRecyclerList : MutableList<Item>
    // private var totalPriceOfProductToBeSold : Double = 0.0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellPageListBinding.inflate(inflater, container, false)

        viewModel.itemsFromFirebase.observe(this.viewLifecycleOwner) {
                myData -> for (eachData in myData) {
            items.add(
                Item(
                    eachData.itemId,
                    eachData.truncateWords(eachData.itemName, 10),
                    eachData.itemCategory,
                    eachData.itemDescription,
                    eachData.itemCostPrice,
                    eachData.itemSellingPrice,
                    eachData.itemQuantity
                )
            )
            dataNamesAndQty.add(eachData.itemName)
        }
        }

        viewModel.itemsSoldFromFirebase.observe(this.viewLifecycleOwner) {
            itemSoldData -> for (eachItemSoldData in itemSoldData) {
                soldItems.add(
                    ItemSold(
                        eachItemSoldData.itemSoldId,
                        eachItemSoldData.itemName,
                        eachItemSoldData.itemQuantity,
                        eachItemSoldData.itemCumulativeQty,
                        eachItemSoldData.itemCategory,
                        eachItemSoldData.profitsOnItemSold,
                        eachItemSoldData.cumulativeProfitsOnItemSold,
                        eachItemSoldData.timeItemIsSold,
                        eachItemSoldData.itemCostPrice,
                        eachItemSoldData.cumulativeItemCostPrice,
                        eachItemSoldData.itemSellingPrice,
                        eachItemSoldData.cumulativeItemSellingPrice,
                        eachItemSoldData.purchasedBy
                    )
                )
        }
        }

        if (viewModel.itemsAddedToSellList != null) {
            sellPageAdapter = SellPageAdapter{}
            sellPageAdapter.notifyDataSetChanged()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sellIcon.setImageResource(R.drawable.ic_sell)

        bottomNavDestinations()
        init()
        getItemName()

//        binding.sellPageRecyclerView.adapter = sellPageAdapter
//        binding.sellPageRecyclerView.layoutManager = LinearLayoutManager(this.context)
//        viewModel.sellPageRecyclerList.observe(this.viewLifecycleOwner) {
//                myRecList -> myRecList.let {
//            sellPageAdapter.submitList(it)
//        }
//        }

        binding.sellPageTextField.setOnClickListener {
            setProductQtyAvailable()
        }

        binding.sellPageAddToSell.setOnClickListener {
            addToTheSellRecyclerView()
        }

        binding.sellPageSellBtn.setOnClickListener {
            clearRecyclerList()
        }

        deleteBySwiping()
    }

    private fun getItemName () {
        var itemName = ""
        if (arguments != null) {
            itemName = requireArguments().getString("dropDownItemName").toString()

            binding.sellPageDropdownText.setText(itemName)
        }
    }

    private fun deleteBySwiping () {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem : Item = viewModel.itemsAddedToSellList[viewHolder.absoluteAdapterPosition]
                val position = viewHolder.absoluteAdapterPosition

                for (eachData in items) {
                    if (eachData.itemName == deletedItem.itemName) {
                        viewModel.updateItem(
                            viewModel.itemAttr(
                                eachData.itemId.toString(),
                                eachData.itemName,
                                eachData.itemCategory.toString(),
                                eachData.itemDescription,
                                eachData.itemCostPrice,
                                eachData.itemSellingPrice,
                                (eachData.itemQuantity.toInt() + deletedItem.itemQuantity.toInt()).toString()
                            )
                        )
                        binding.sellPageProductQty.text = resources.getString(R.string.qty_in_stock, eachData.itemQuantity.toInt() + deletedItem.itemQuantity.toInt())
                    }
                }

                viewModel.itemsAddedToSellList.removeAt(position)
                sellPageAdapter.notifyItemRemoved(position)
                val updatedTotalPrice = viewModel.totalPriceOfProductToBeSold - deletedItem.itemSellingPrice.toDouble()
                viewModel.totalPriceOfProductToBeSold = updatedTotalPrice
                binding.sellPageTotalPrice.text = resources.getString(R.string.total_price, viewModel.totalPriceOfProductToBeSold.toString())

                Snackbar.make(binding.sellPageRecyclerView, "Deleted ${deletedItem.itemName}", Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            for (eachData in items) {
                                if (eachData.itemName == deletedItem.itemName) {
                                    viewModel.updateItem(
                                        viewModel.itemAttr(
                                            eachData.itemId.toString(),
                                            eachData.itemName,
                                            eachData.itemCategory.toString(),
                                            eachData.itemDescription,
                                            eachData.itemCostPrice,
                                            eachData.itemSellingPrice,
                                            (eachData.itemQuantity.toInt() - deletedItem.itemQuantity.toInt()).toString()
                                        )
                                    )
                                    binding.sellPageProductQty.text = resources.getString(R.string.qty_in_stock, eachData.itemQuantity.toInt() - deletedItem.itemQuantity.toInt())
                                }
                            }
                            viewModel.itemsAddedToSellList.add(position, deletedItem)
                            val updatedTotalPricee = viewModel.totalPriceOfProductToBeSold + deletedItem.itemSellingPrice.toDouble()
                            viewModel.totalPriceOfProductToBeSold = updatedTotalPricee
                            binding.sellPageTotalPrice.text = resources.getString(R.string.total_price, viewModel.totalPriceOfProductToBeSold.toString())
                            sellPageAdapter.notifyItemInserted(position)
                        }
                    ).show()
            }

        }).attachToRecyclerView(binding.sellPageRecyclerView)
    }

    private fun clearRecyclerList() {
        callPopUpToGetPurchaserName()
        viewModel.totalPriceOfProductToBeSold = 0.0
        binding.sellPageTotalPrice.text = ""
        Toast.makeText(context, "Item(s) successfully sold", Toast.LENGTH_LONG).show()
    }

    private fun callPopUpToGetPurchaserName() {
        addPurchaserNamePopUp = AddCategoryPopUpFragment.newInstance("Enter buyer's name")
        addPurchaserNamePopUp!!.setSellListener(this)
        addPurchaserNamePopUp!!.show(
            childFragmentManager,
            addPurchaserNamePopUp!!.tag
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        sellPageAdapter = SellPageAdapter {
            if (viewModel.itemsAddedToSellList.contains(it)) {
                if (binding.sellPageDropdownText.text.toString().isEmpty() && binding.sellPageTextField.text.toString().isEmpty()) {
                    binding.sellPageDropdownText.setText(it.itemName)
                    binding.sellPageTextField.setText(it.itemQuantity)
                    val updatedTotalPrice = viewModel.totalPriceOfProductToBeSold - it.itemSellingPrice.toDouble()
                    viewModel.totalPriceOfProductToBeSold = updatedTotalPrice
                    binding.sellPageTotalPrice.text = resources.getString(R.string.total_price, viewModel.totalPriceOfProductToBeSold.toString())

                    for (eachData in items) {
                        if (eachData.itemName == it.itemName) {
                            viewModel.updateItem(
                                viewModel.itemAttr(
                                    eachData.itemId.toString(),
                                    eachData.itemName,
                                    eachData.itemCategory.toString(),
                                    eachData.itemDescription,
                                    eachData.itemCostPrice,
                                    eachData.itemSellingPrice,
                                    (eachData.itemQuantity.toInt() + it.itemQuantity.toInt()).toString()
                                )
                            )
                            binding.sellPageProductQty.text = resources.getString(R.string.qty_in_stock, eachData.itemQuantity.toInt() + it.itemQuantity.toInt())
                        }
                    }
                    viewModel.itemsAddedToSellList.remove(it)
                    sellPageAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Add edited item, then edit this item.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addToTheSellRecyclerView() {

        binding.apply {
            if(sellPageDropdownText.text != null) {
                for (eachItem in items) {
                    if (sellPageDropdownText.text.toString() == eachItem.itemName) {
                        if (eachItem.itemQuantity.isNotEmpty() && sellPageTextField.text.toString().toInt() <= eachItem.itemQuantity.toInt()) {
                            if (viewModel.itemsAddedToSellList.isEmpty()) {
                                viewModel.itemsAddedToSellList.add(Item(
                                    eachItem.itemId,
                                    eachItem.itemName,
                                    eachItem.itemCategory,
                                    eachItem.itemDescription,
                                    (sellPageTextField.text.toString().toInt() * eachItem.itemCostPrice.toDouble()).toString(),
                                    (sellPageTextField.text.toString().toInt() * eachItem.itemSellingPrice.toDouble()).toString(),
                                    sellPageTextField.text.toString()
                                ))
                                viewModel.updateItem(viewModel.itemAttr(
                                    eachItem.itemId.toString(),
                                    eachItem.itemName,
                                    eachItem.itemCategory.toString(),
                                    eachItem.itemDescription,
                                    eachItem.itemCostPrice,
                                    eachItem.itemSellingPrice,
                                    (eachItem.itemQuantity.toInt() - sellPageTextField.text.toString().toInt()).toString()
                                ))

                                viewModel.totalPriceOfProductToBeSold += sellPageTextField.text.toString().toInt() * eachItem.itemSellingPrice.toDouble()

                            } else {
                                var isItemInSellList = false
                                for (listData in viewModel.itemsAddedToSellList) {
                                    if (listData.itemName == eachItem.itemName) {
                                        isItemInSellList = true
                                        Toast.makeText(context, "Item exist in the sell list. Tap item to edit", Toast.LENGTH_LONG).show()
                                        break
                                    }
                                }
                                if (!isItemInSellList) {
                                        viewModel.itemsAddedToSellList.add(Item(
                                            eachItem.itemId,
                                            eachItem.itemName,
                                            eachItem.itemCategory,
                                            eachItem.itemDescription,
                                            (sellPageTextField.text.toString().toInt() * eachItem.itemCostPrice.toDouble()).toString(),
                                            (sellPageTextField.text.toString().toInt() * eachItem.itemSellingPrice.toDouble()).toString(),
                                            sellPageTextField.text.toString()
                                        ))
                                        viewModel.updateItem(viewModel.itemAttr(
                                            eachItem.itemId.toString(),
                                            eachItem.itemName,
                                            eachItem.itemCategory.toString(),
                                            eachItem.itemDescription,
                                            eachItem.itemCostPrice,
                                            eachItem.itemSellingPrice,
                                            (eachItem.itemQuantity.toInt() - sellPageTextField.text.toString().toInt()).toString()
                                        ))

                                    viewModel.totalPriceOfProductToBeSold += sellPageTextField.text.toString().toInt() * eachItem.itemSellingPrice.toDouble()
                                }
                            }
                               //}
                        } else
                            Toast.makeText(context, "You have ${eachItem.itemQuantity} quantity left", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.sellPageRecyclerView.adapter = sellPageAdapter
        binding.sellPageRecyclerView.layoutManager = LinearLayoutManager(this.context)
        viewModel.sellPageRecyclerList.observe(this.viewLifecycleOwner) {
                myRecList -> myRecList.let {
            sellPageAdapter.submitList(it)
        }
        }
        binding.sellPageTotalPrice.text = getString(R.string.total_price, viewModel.totalPriceOfProductToBeSold.toString())
        setFieldsToNull()
    }

    private fun setFieldsToNull() {
        binding.apply {
            sellPageDropdownText.text = null
            sellPageTextField.text = null
            sellPageProductQty.visibility = View.INVISIBLE
        }
    }

    private fun setProductQtyAvailable() {
        binding.apply {
            if(sellPageDropdownText.text != null) {
                for (eachItem in items) {
                    if (sellPageDropdownText.text.toString() == eachItem.itemName) {
                        sellPageProductQty.text = resources.getString(R.string.qty_in_stock, eachItem.itemQuantity.toInt())
                    }
                }
            }
            sellPageProductQty.visibility = View.VISIBLE
        }
    }

    private fun bottomNavDestinations() {
        binding.apply {
            catalogIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_sellPageFragment3_to_catalogFragment3)
            }

            homeIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_sellPageFragment3_to_homeFragment3)
            }

            analyticIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_sellPageFragment3_to_analyticsPageFragment3)
            }

            profileIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_sellPageFragment3_to_profilePageFragment3)
            }

            sellPageDropdownText.setOnClickListener {
                findNavController().navigate(R.id.action_sellPageFragment3_to_sellDropDownItemSelectFragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun getCategory(category: String, categoryTextField: TextInputEditText) {
        if (soldItems.isNullOrEmpty()) {
            for (itemSold in viewModel.itemsAddedToSellList) {
                viewModel.insertItemSold(
                    ItemSold(
                        viewModel.soldItemsDatabaseRef.push().key!!,
                        itemSold.itemName,
                        itemSold.itemQuantity,
                        itemSold.itemQuantity,
                        itemSold.itemCategory,
                        (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble()).toString(),
                        (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble()).toString(),
                        viewModel.getCurrentDateAndTime(),
                        itemSold.itemCostPrice,
                        itemSold.itemCostPrice,
                        itemSold.itemSellingPrice,
                        itemSold.itemSellingPrice,
                        category
                    )
                )
            }
        } else {
            for (soldItem in soldItems) {
                var isItemSoldInSoldItem = false
                var mySoldItem = ItemSold(
                    "", "", "", "",
                    "", "", "", "",
                    "", "", "", "",
                    ""
                )
                for (itemSold in viewModel.itemsAddedToSellList) {
                    if (itemSold.itemName == soldItem.itemName) {
                        isItemSoldInSoldItem = true
                        mySoldItem = ItemSold(
                            viewModel.soldItemsDatabaseRef.push().key!!,
                            itemSold.itemName,
                            itemSold.itemQuantity,
                            (itemSold.itemQuantity.toInt() + soldItem.itemQuantity.toInt()).toString(),
                            itemSold.itemCategory,
                            (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble()).toString(),
                            (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble() + soldItem.cumulativeProfitsOnItemSold.toDouble()).toString(),
                            viewModel.getCurrentDateAndTime(),
                            itemSold.itemCostPrice,
                            (itemSold.itemCostPrice.toDouble() + soldItem.cumulativeItemCostPrice.toDouble()).toString(),
                            itemSold.itemSellingPrice,
                            (itemSold.itemSellingPrice.toDouble() + soldItem.cumulativeItemSellingPrice.toDouble()).toString(),
                            category
                        )
                        break
                    } else {
                        mySoldItem = ItemSold(
                            viewModel.soldItemsDatabaseRef.push().key!!,
                            itemSold.itemName,
                            itemSold.itemQuantity,
                            itemSold.itemQuantity,
                            itemSold.itemCategory,
                            (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble()).toString(),
                            (itemSold.itemSellingPrice.toDouble() - itemSold.itemCostPrice.toDouble()).toString(),
                            viewModel.getCurrentDateAndTime(),
                            itemSold.itemCostPrice,
                            itemSold.itemCostPrice,
                            itemSold.itemSellingPrice,
                            itemSold.itemSellingPrice,
                            category
                        )
                        break
                    }
                }
                if (!isItemSoldInSoldItem) {
                    if (soldItem.itemName == mySoldItem.itemName) {
                        viewModel.insertItemSold(mySoldItem)
                    }
                } else {
                    viewModel.insertItemSold(mySoldItem)
                }
            }
        }
        viewModel.itemsAddedToSellList.clear()
        sellPageAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}