package com.example.shopiko01.placeholder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.shopiko01.R

class DropdownAdapter(
    context: Context,
    resource: Int,
    objects: MutableList<String>
) : ArrayAdapter<String>(context, resource, objects) {

    private var layoutInflater =  LayoutInflater.from(context)

    @SuppressLint("ViewHolder", "InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowView = layoutInflater.inflate(R.layout.dropdown_layout, null, true)
        val spinnerItem = getItem(position)
        val currentSpinnerItem = rowView.findViewById<TextView>(R.id.spinner_text)
        if (spinnerItem != null) {
            currentSpinnerItem.text = spinnerItem.toString()
        }
        return rowView
    }

    @SuppressLint("SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var convertViewTwo : View
        if(convertView == null)
            convertViewTwo = layoutInflater.inflate(R.layout.dropdown_layout, parent, false)
        val spinnerItem = getItem(position)
        val currentSpinnerItem =
        convertView?.findViewById<TextView>(R.id.spinner_text)?:convertViewTwo.findViewById(R.id.spinner_text)
        if (spinnerItem != null) {
            currentSpinnerItem.text = spinnerItem.toString()
                // "${spinnerItem.truncateWords(spinnerItem.itemName, 10)} (${spinnerItem.itemQuantity})"
        }
        return convertView?:convertViewTwo
    }
}