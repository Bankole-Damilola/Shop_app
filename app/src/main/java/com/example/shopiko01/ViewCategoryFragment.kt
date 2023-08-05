package com.example.shopiko01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopiko01.databinding.FragmentViewCategoryBinding
import com.example.shopiko01.models.ItemCategory
import com.example.shopiko01.models.ShopikoViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale.Category

/**
 * A simple [Fragment] subclass.
 * Use the [ViewCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewCategoryFragment : Fragment(), AddCategoryPopUpFragment.OnCategoryProvided {

    private var _binding : FragmentViewCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ShopikoViewModel by activityViewModels()

    private var addToCategoryPopUp : AddCategoryPopUpFragment? = null

    private val categoryList = mutableListOf<ItemCategory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewCategoryBinding.inflate(inflater, container, false)

        viewModel.categoriesFromFirebase.observe(this.viewLifecycleOwner) {

            myCategory ->
            if (myCategory != null) {
                for (category in myCategory) {
                    categoryList.add(category)
                }
                Log.v("Category Item Fragment", "List not empty")
            } else {
                Toast.makeText(context, "There is no category available", Toast.LENGTH_LONG).show()
                Log.v("Category Item Fragment", "List is empty")
            }
        }

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryAdapter = CategoryAdapter {}
        binding.viewCategoryRecyclerList.adapter = categoryAdapter
        categoryAdapter.submitList(categoryList)

        binding.viewCategoryRecyclerList.layoutManager = LinearLayoutManager(this.context)

        binding.viewCategoryAddFab.setOnClickListener {
            callPopUpAndGetCategory()
        }

    }

    private fun callPopUpAndGetCategory() {
        addToCategoryPopUp = AddCategoryPopUpFragment()
        addToCategoryPopUp!!.setCategoryPageListener(this)
        addToCategoryPopUp!!.show(
            childFragmentManager,
            addToCategoryPopUp!!.tag
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun getCategory(category: String, categoryTextField: TextInputEditText) {
        viewModel.insertCategory(ItemCategory(
            viewModel.categoryDatabaseRef.push().key!!, category
        ))
        categoryTextField.text = null
        addToCategoryPopUp!!.dismiss()
    }
}