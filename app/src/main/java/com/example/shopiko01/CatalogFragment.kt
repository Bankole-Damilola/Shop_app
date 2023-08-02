package com.example.shopiko01

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopiko01.R
import com.example.shopiko01.databinding.FragmentCatalogListBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ShopikoViewModel
import java.util.Locale

/**
 * A fragment representing a list of Items.
 */
class CatalogFragment : Fragment() {

    private lateinit var binding : FragmentCatalogListBinding

    private val viewModel : ShopikoViewModel by activityViewModels()

//    private lateinit var navController: NavController
    private lateinit var filteredList : MutableList<Item>
    private lateinit var listItems : MutableList<Item>
    private lateinit var catalogAdapter: CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        init(view)
        bottomNavDestinations()
        listItems = mutableListOf()
        filteredList = mutableListOf()

        catalogAdapter = CatalogAdapter {
            val bundle = bundleOf(
                "itemId" to it.itemId,
                "itemName" to it.itemName,
                "itemDescription" to it.itemDescription,
                "itemCategory" to it.itemCategory,
                "itemCostPrice" to it.itemCostPrice,
                "itemSellingPrice" to it.itemSellingPrice,
                "itemQuantity" to it.itemQuantity
            )
            findNavController().navigate(R.id.action_catalogFragment3_to_viewFragment, bundle)
            // val action = CatalogFragmentDirections.actionCatalogFragment3ToViewFragment(it.itemId)
            // findNavController().navigate(action)
        }

        binding.catalogPageRecyclerView.adapter = catalogAdapter
        viewModel.itemsFromFirebase.observe(this.viewLifecycleOwner
        ) { items -> items.let {
                catalogAdapter.submitList(it)
            }
            listItems = items
        }

        binding.catalogPageRecyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.catalogIcon.setImageResource(R.drawable.ic_inventory)


        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_catalogFragment3_to_addProductFragment2)
        }

        requiredEvents()
    }

    private fun requiredEvents() {
        binding.searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(query : String?) {
        filteredList.clear()

        if (query != null) {
            for (i in listItems) {
                if (i.itemName.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(context, "Item is not found", Toast.LENGTH_LONG).show()
            } else {
                catalogAdapter.submitList(filteredList)
                catalogAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun bottomNavDestinations() {
        binding.apply {
            homeIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_catalogFragment3_to_homeFragment3)
            }

            sellIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_catalogFragment3_to_sellPageFragment3)
            }

            analyticIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_catalogFragment3_to_analyticsPageFragment3)
            }

            profileIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_catalogFragment3_to_profilePageFragment3)
            }
        }
    }

//    private fun init(view: View) {
//        navController = Navigation.findNavController(view)
//    }
}