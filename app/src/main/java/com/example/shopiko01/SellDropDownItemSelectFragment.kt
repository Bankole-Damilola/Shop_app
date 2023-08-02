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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiko01.databinding.FragmentSellDropDownItemSelectBinding
import com.example.shopiko01.models.Item
import com.example.shopiko01.models.ShopikoViewModel
import com.example.shopiko01.placeholder.SellDropDownAdapter
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [SellDropDownItemSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellDropDownItemSelectFragment : Fragment() {

    private var _binding : FragmentSellDropDownItemSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ShopikoViewModel by activityViewModels()

    private lateinit var sellDropDownAdapter : SellDropDownAdapter
    private var itemName = arrayListOf<String>()
    private var filteredItemName = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellDropDownItemSelectBinding.inflate(inflater, container, false)

        viewModel.itemsFromFirebase.observe(this.viewLifecycleOwner) {
                myData -> for (eachData in myData) {
                    itemName.add(eachData.itemName)
                }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sellDropDownAdapter = SellDropDownAdapter{
            if (itemName.contains(it)) {
                val bundle = bundleOf(
                    "dropDownItemName" to it
                )
                findNavController().navigate(R.id.action_sellDropDownItemSelectFragment_to_sellPageFragment3, bundle)
            }
        }
        binding.sellDropDownRecyclerList.layoutManager = LinearLayoutManager(context)
        binding.sellDropDownRecyclerList.adapter = sellDropDownAdapter

        sellDropDownAdapter.submitList(itemName)

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
    private fun filterList(newText: String?) {
        filteredItemName.clear()

        if (newText != null) {
            for (i in itemName) {
                if (i.lowercase(Locale.ROOT).contains(newText)) {
                    filteredItemName.add(i)
                }
            }
            if (filteredItemName.isEmpty()) {
                Toast.makeText(context, "Item is not found", Toast.LENGTH_LONG).show()
            } else {
                sellDropDownAdapter.submitList(filteredItemName)
                sellDropDownAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}