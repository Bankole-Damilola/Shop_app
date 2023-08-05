package com.example.shopiko01

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiko01.databinding.FragmentAnalyticsPageBinding
import com.example.shopiko01.models.ItemSold
import com.example.shopiko01.models.ShopikoViewModel
import com.example.shopiko01.placeholder.DropdownAdapter
import com.example.shopiko01.placeholder.SoldItemsAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * A simple [Fragment] subclass.
 * Use the [AnalyticsPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalyticsPageFragment : Fragment() {

    private lateinit var binding : FragmentAnalyticsPageBinding
    private val viewModel : ShopikoViewModel by activityViewModels()

    private var listOfSoldItems = mutableListOf<ItemSold>()
    private var listSeperatedByCategory = mutableListOf<ItemSold>()
    // private val newListOfItemSold = mutableListOf<ItemSold>()

    private lateinit var analyticAdapter: SoldItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsPageBinding.inflate(inflater, container, false)

        viewModel.itemsSoldFromFirebase.observe(this.viewLifecycleOwner) {
            itemSoldList ->
            if (itemSoldList != null) {
                listOfSoldItems = itemSoldList
            } else {
                Toast.makeText(context, "There is nothing to analyse yet", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analyticAdapter = SoldItemsAdapter {  }

        binding.analyticGoBtn.setOnClickListener {
            onCategorySelected()
        }

        bottomNavDestinations()
    }

    private fun onCategorySelected() {
        if (binding.analyticCategoryDropdownText.text.toString().isNotEmpty()) {

            for (itemSold in listOfSoldItems) {
                if (itemSold.itemCategory == binding.analyticCategoryDropdownText.text.toString()) {
                    listSeperatedByCategory.add(itemSold)
                }
            }

            if (isCategoryListItemGreaterThanOne(listSeperatedByCategory.size)) {
                val tempCategoryList = sortCategoryList(listSeperatedByCategory)
                listSeperatedByCategory = tempCategoryList

                if (isCategoryListItemGreaterThanOne(listSeperatedByCategory.size)) {

                    val pieChartList = mutableListOf<ItemSold>()
                    val analyticRecyclerList = mutableListOf<ItemSold>()

                    val listDividedByTwo = (listSeperatedByCategory.size / 2).toInt()
                    for ((i, dividedCatList) in listSeperatedByCategory.withIndex()) {

                        if (i <= listDividedByTwo) {
                            pieChartList.add(dividedCatList)
                        } else {
                            analyticRecyclerList.add(dividedCatList)
                        }
                    }

                    listForPieChart(pieChartList)
                    listForRecyclerList(analyticRecyclerList)

                } else {
                    listForPieChart(listSeperatedByCategory)
                }
            } else {
                listForPieChart(listSeperatedByCategory)
            }
        }
    }

    private fun listForRecyclerList(recyclerList: MutableList<ItemSold>) {

        binding.apply {

            analyticRecyclerList.layoutManager = LinearLayoutManager(context)
            analyticRecyclerList.adapter = analyticAdapter

            analyticAdapter.submitList(recyclerList)
        }
    }

    private fun listForPieChart(pieChartList: MutableList<ItemSold>) {

        binding.apply {

            val listOfPieEntry : ArrayList<PieEntry> = arrayListOf()

            for (sliceInPieChart in pieChartList) {
                listOfPieEntry.add(PieEntry(sliceInPieChart.itemCumulativeQty.toFloat(), sliceInPieChart.itemName))
            }

            val pieDataSet = PieDataSet(listOfPieEntry, "List")

            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
            pieDataSet.valueTextColor = Color.BLACK
            pieDataSet.valueTextSize = 15f

            val pieData = PieData(pieDataSet)
            // pieChart.setFitPie(true)
            analyticPieChart.data = pieData
            analyticPieChart.centerText = "List"
            analyticPieChart.description.text = "Pie Chart"
            analyticPieChart.animateY(2000)
        }

    }

    private fun sortCategoryList(categoryList: MutableList<ItemSold>) : MutableList<ItemSold> {

        val newCategoryList = mutableListOf<ItemSold>()
        var i = categoryList.size - 1
                while (i > 0) {

                    var j = 0
                    while (j < i) {

                        if (categoryList[j].itemCumulativeQty.toInt() > categoryList[j + 1].itemCumulativeQty.toInt()) {

                            val temItemSOld = categoryList[j]
                            categoryList[j] = categoryList[j + 1]
                            categoryList[j + 1] = temItemSOld
                        }
                        j++
                    }

                    if (newCategoryList.isNotEmpty()) {
                        var doesItemExistInList = false

                        for (soldItemName in newCategoryList) {

                            if(soldItemName.itemName == categoryList[j].itemName) {
                                doesItemExistInList = true
                                break
                            }
                        }

                        if (!doesItemExistInList) {

                            newCategoryList.add(categoryList[j])
                        }
                    } else {
                        newCategoryList.add(categoryList[j])
                    }
                    i--
                }

        return newCategoryList
    }

    private fun isCategoryListItemGreaterThanOne(size: Int) : Boolean {
        return (size > 1)
    }

    override fun onResume() {
        super.onResume()

        val itemCategoryList = arrayListOf<String>()

        for (itemCategory in listOfSoldItems) {

            if (itemCategoryList.isEmpty()) {
                itemCategoryList.add(itemCategory.itemCategory)
            } else {
                var isItemCategoryAdded = false
                for (itemCat in itemCategoryList) {
                    if (itemCat == itemCategory.itemCategory) {
                        isItemCategoryAdded = true
                        break
                    }
                }

                if (!isItemCategoryAdded) {
                    itemCategoryList.add(itemCategory.itemCategory)
                }
            }

        }

        binding.apply {
            val adapter = DropdownAdapter(requireContext(), R.layout.dropdown_layout, itemCategoryList)
            analyticCategoryDropdownText.setAdapter(adapter)
        }
    }

    private fun bottomNavDestinations() {
        binding.apply {
            homeIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_homeFragment3)
            }

            sellIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_sellPageFragment3)
            }

            catalogIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_catalogFragment3)
            }

            profileIconLayout.setOnClickListener {
                findNavController().navigate(R.id.action_analyticsPageFragment3_to_profilePageFragment3)
            }

            analyticIcon.setImageResource(R.drawable.ic_analytics)
        }
    }


}