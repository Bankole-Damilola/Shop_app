package com.example.shopiko01

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiko01.databinding.FragmentHomeBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ItemCategory
import com.example.shopiko01.models.ShopikoViewModel
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), AddCategoryPopUpFragment.OnCategoryProvided {

    private lateinit var  binding : FragmentHomeBinding
    private val viewModel : ShopikoViewModel by activityViewModels()

    private val listOfItemsOutOfStock = mutableListOf<Item>()

    private var addToCategoryPopUp : AddCategoryPopUpFragment? = null
    private lateinit var homeAdapter : CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.itemsFromFirebase.observe(this.viewLifecycleOwner) {
            items -> for (item in items) {
                if (item.itemQuantity.toInt() <= 5) {
                    listOfItemsOutOfStock.add(item)
                }
        }
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeAdapter = CatalogAdapter {
            val bundle = bundleOf(
                "itemId" to it.itemId,
                "itemName" to it.itemName,
                "itemDescription" to it.itemDescription,
                "itemCategory" to it.itemCategory,
                "itemCostPrice" to it.itemCostPrice,
                "itemSellingPrice" to it.itemSellingPrice,
                "itemQuantity" to it.itemQuantity
            )
            findNavController().navigate(R.id.action_homeFragment3_to_addProductFragment2, bundle)
        }
        binding.homePageRecyclerList.layoutManager = LinearLayoutManager(context)
        binding.homePageRecyclerList.adapter = homeAdapter
        if (listOfItemsOutOfStock != null) {
            // binding.homePageFrameLayout.visibility = View.VISIBLE
            binding.homePageStockFull.visibility = View.GONE

            homeAdapter.submitList(listOfItemsOutOfStock)
            homeAdapter.notifyDataSetChanged()
        } else {
            binding.homePageStockFull.visibility = View.VISIBLE
        }

        bottomNavDestinations()

        binding.homeIcon.setImageResource(R.drawable.ic_home)

        binding.homePageAddCategoryBtn.setOnClickListener {
            callPopUpAndGetCategory()
        }

    }

    private fun callPopUpAndGetCategory() {
        addToCategoryPopUp = AddCategoryPopUpFragment()
        addToCategoryPopUp!!.setListener(this)
        addToCategoryPopUp!!.show(
            childFragmentManager,
            addToCategoryPopUp!!.tag
        )
    }

    private fun bottomNavDestinations() {
        binding.apply {
            catalogIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment3_to_catalogFragment3)
            }

            sellIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment3_to_sellPageFragment3)
            }

            analyticIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment3_to_analyticsPageFragment3)
            }

            profileIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment3_to_profilePageFragment3)
            }
        }
    }

    override fun getCategory(category: String, categoryTextField: TextInputEditText) {
        viewModel.insertCategory(ItemCategory(
            viewModel.categoryDatabaseRef.push().key!!, category
        ))
        categoryTextField.text = null
        addToCategoryPopUp!!.dismiss()
    }
}