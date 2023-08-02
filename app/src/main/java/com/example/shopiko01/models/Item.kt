package com.example.shopiko01.models

import java.text.NumberFormat

data class Item(
    var itemId : String? = null,
    var itemName : String,
    var itemCategory : String,
    var itemDescription : String,
    var itemCostPrice : String,
    var itemSellingPrice : String,
    var itemQuantity : String
) {

    fun truncateWords (wordToBeTruncated: String, length : Int) : String {
        return wordToBeTruncated.smartTruncate(length).toString()
    }

    fun getFormattedPrice(numberToBeFormatted: Double) : String {
        return NumberFormat.getCurrencyInstance().format(numberToBeFormatted)
    }
}

data class ItemSold (
    var itemSoldId : String? = null,
    val itemName : String,
    val itemQuantity: String,
    val itemCumulativeQty: String,
    val itemCategory: String,
    val profitsOnItemSold : String,
    val cumulativeProfitsOnItemSold : String,
    val timeItemIsSold : String,
    val itemCostPrice : String,
    val cumulativeItemCostPrice : String,
    val itemSellingPrice: String,
    val cumulativeItemSellingPrice: String,
    val purchasedBy: String? = null
) {
    fun truncateWords (wordToBeTruncated: String, length : Int) : String {
        return wordToBeTruncated.smartTruncate(length).toString()
    }
}

data class ItemCategory (var categoryId : String? = null, var category: String)