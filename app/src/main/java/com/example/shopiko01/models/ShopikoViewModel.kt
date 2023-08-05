package com.example.shopiko01.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShopikoViewModel : ViewModel() {

    private var user : FirebaseAuth = FirebaseAuth.getInstance()

    // ToDo : This sets of variables are the way the data are structured in the DB.
    //  From the parentDatabaseRef which provides unique shops for various users,
    //  to variables like databaseRef which is a child to the parent and provides a path to access our products,
    //  to categoryDatabaseRef which provides the application with the list of category created by individual shop owners
    // to soldItemsDatabaseRef with the DB ref to the items in the application they are considered sold.
    private var parentDatabaseRef : DatabaseReference =
        FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(user.currentUser?.uid.toString())

    // This path contains the information on individual items in the store
    private var _databaseRef = parentDatabaseRef.child("Shop Items")
    val databaseRef get() = _databaseRef
    // These variables works with the above mentioned data path variables
    lateinit var itemsFromFirebase: LiveData<MutableList<Item>>
    private var  _itemsFromFirebase = flowOf<MutableList<Item>>()

    // This path contains the category in which individual products can fall in
    private var _categoryDatabaseRef = parentDatabaseRef.child("Item Categories")
    val categoryDatabaseRef get() = _categoryDatabaseRef

    private var _categoriesFromFirebase = flowOf<MutableList<ItemCategory>>()
    lateinit var categoriesFromFirebase: LiveData<MutableList<ItemCategory>>

    // This path contains the information needed about a items sold, date, toldQtyOfItem, toWhomItIsSold
    private var _soldItemsDatabaseRef = parentDatabaseRef.child("Sold Items")
    val soldItemsDatabaseRef get() = _soldItemsDatabaseRef

    private var _itemsSoldFromFirebase = flowOf<MutableList<ItemSold>>()
    lateinit var itemsSoldFromFirebase: LiveData<MutableList<ItemSold>>

    // Variables that interacts with the sell view as regards the items to be sold that are in the list
    private var _sellPageRecyclerList : Flow<MutableList<Item>> = flowOf()
    val sellPageRecyclerList get() = _sellPageRecyclerList.asLiveData()
    val itemsAddedToSellList : MutableList<Item>  = mutableListOf()

    // Variables that interact with the sell page and all other connecting pages that
    // requires the total price of items that are in the list to be sold
    var totalPriceOfProductToBeSold : Double = 0.0

    init {
        getItemsFromFirebase()
        itemsFromFirebase = _itemsFromFirebase.asLiveData() // dataFromFirebase().asLiveData()
        _sellPageRecyclerList = flowOf(itemsAddedToSellList)

        getCategoryFromFirebase()
        categoriesFromFirebase = _categoriesFromFirebase.asLiveData()

        getItemSoldFromFirebase()
        itemsSoldFromFirebase = _itemsSoldFromFirebase.asLiveData()
    }

    // This function gets the data on individual items stored in the DB in a list
    private fun getItemsFromFirebase () {
        viewModelScope.launch {
            val allItem = mutableListOf<Item>()
            _databaseRef.addValueEventListener(
                object  : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        allItem.clear()
                        for (itemSnapshot in snapshot.children) {

                            val itemData = itemSnapshot.key?.let {
                                itemAttr(
                                    it, itemSnapshot.child("itemName").value.toString(),
                                    itemSnapshot.child("itemCategory").value.toString(),
                                    itemSnapshot.child("itemDescription").value.toString(),
                                    itemSnapshot.child("itemCostPrice").value.toString(),
                                    itemSnapshot.child("itemSellingPrice").value.toString(),
                                    itemSnapshot.child("itemQuantity").value.toString()
                                )
                            }

                            if (itemData != null) {
                                allItem.add(itemData)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.v("Catalog Fragment", "Couldn't get data")
                    }

                }
            )
            // collectedData = allItem
            _itemsFromFirebase = flowOf(allItem)
        }
    }

    // This function gets the data stored in the category path of the parent data in the DB into a list
    private fun getCategoryFromFirebase () {
        viewModelScope.launch {
            val allCategory = mutableListOf<ItemCategory>()
            _categoryDatabaseRef.addValueEventListener(
                object  : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        allCategory.clear()
                        for (itemCategorySnapshot in snapshot.children) {

                            val categoryData = itemCategorySnapshot.key?.let {
                                ItemCategory(
                                    it,
                                    itemCategorySnapshot.child("category").value.toString()
                                )
                            }

                            if (categoryData != null) {
                                allCategory.add(categoryData)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.v("Catalog Fragment", "Couldn't get data")
                    }

                }
            )
            _categoriesFromFirebase = flowOf(allCategory)
        }
    }

    // This function gets the data stored about different purchased items and the time it was purchased considering
    // the cumulative of qty sold, profits generated, and by whom it is purchased.
    private fun getItemSoldFromFirebase() {
        viewModelScope.launch {
            val allItemSold = mutableListOf<ItemSold>()
            _soldItemsDatabaseRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        allItemSold.clear()
                        for (itemSoldSnapshot in snapshot.children) {

                            val itemSoldData = itemSoldSnapshot.key?.let {
                                ItemSold(
                                    it,
                                    itemSoldSnapshot.child("itemName").value.toString(),
                                    itemSoldSnapshot.child("itemQuantity").value.toString(),
                                    itemSoldSnapshot.child("itemCumulativeQty").value.toString(),
                                    itemSoldSnapshot.child("itemCategory").value.toString(),
                                    itemSoldSnapshot.child("profitsOnItemSold").value.toString(),
                                    itemSoldSnapshot.child("cumulativeProfitsOnItemSold").value.toString(),
                                    itemSoldSnapshot.child("timeItemIsSold").value.toString(),
                                    itemSoldSnapshot.child("itemCostPrice").value.toString(),
                                    itemSoldSnapshot.child("cumulativeItemCostPrice").value.toString(),
                                    itemSoldSnapshot.child("itemSellingPrice").value.toString(),
                                    itemSoldSnapshot.child("cumulativeItemSellingPrice").value.toString(),
                                    itemSoldSnapshot.child("purchasedBy").value.toString()
                                )
                            }

                            if (itemSoldData != null) {
                                allItemSold.add(itemSoldData)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )
            _itemsSoldFromFirebase = flowOf(allItemSold)
        }
    }


    fun itemAttr(itemId : Any,
        itemName: Any, itemCategory: Any, itemDescription: Any, itemCostPrice: Any,
        itemSellingPrice: Any, itemQuantity: Any
    ) : Item {
        return  Item(
            itemId = itemId.toString(),
            itemName = itemName.toString(),
            itemCategory = itemCategory.toString(),
            itemDescription = itemDescription.toString(),
            itemCostPrice = itemCostPrice.toString(),
            itemSellingPrice = itemSellingPrice.toString(),
            itemQuantity = itemQuantity.toString()
        )
    }

    // This functions enables the insertion of the newly created item in the DB
    fun insertItem (itemAttribute : Item) {
        viewModelScope.launch {
            _databaseRef.push().setValue(itemAttribute).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("AddFragment", "Operation successful")
                } else {
                    Log.v("AddFragment", "Not successfully added")
                }
            }
        }
    }

    // This function enables the insertion of the newly defined category in the DB
    fun insertCategory (category: ItemCategory) {
        viewModelScope.launch {
            _categoryDatabaseRef.push().setValue(category).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("CategoryPopUpFragment", "Category operation successful")
                } else {
                    Log.v("CategoryPopUpFragment", "Category not successfully added")
                }
            }
        }
    }

    fun insertItemSold (itemSold : ItemSold) {
        viewModelScope.launch {
            _soldItemsDatabaseRef.push().setValue(itemSold).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("CategoryPopUpFragment", "Category operation successful")
                } else {
                    Log.v("CategoryPopUpFragment", "Category not successfully added")
                }
            }
        }
    }

    fun updateSellRecyclerListFromViewPage (item : Item) {
        val itemPrice = item.itemQuantity.toInt() * item.itemSellingPrice.toDouble()
        if (itemsAddedToSellList.isEmpty()) {
            itemsAddedToSellList.add(
                itemAttr(
                    item.itemId.toString(),
                    item.itemName,
                    item.itemCategory.toString(),
                    item.itemDescription,
                    item.itemCostPrice,
                    itemPrice.toString(),
                    item.itemQuantity
                )
            )
            totalPriceOfProductToBeSold += itemPrice
        } else {
            var isItemInSellList = false
            for (eachItem in itemsAddedToSellList) {
                if (item.itemName == eachItem.itemName) {
                    isItemInSellList = true
                    Log.v("ViewModelToSellItem", "Item Exist")
                    break
                }
            }
            if (!isItemInSellList) {
                itemsAddedToSellList.add(
                    itemAttr(
                        item.itemId.toString(),
                        item.itemName,
                        item.itemCategory.toString(),
                        item.itemDescription,
                        item.itemCostPrice,
                        itemPrice.toString(),
                        item.itemQuantity
                    )
                )
                totalPriceOfProductToBeSold += itemPrice
            }
        }
        _sellPageRecyclerList = flowOf(itemsAddedToSellList)
    }

    fun secondUpdateFunc (itemAttribute: Item) {
        viewModelScope.launch {
            _databaseRef.child(itemAttribute.itemId.toString()).setValue(itemAttribute).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("AddFragment", "Update operation successful")
                } else {
                    Log.v("AddFragment", "Item not successfully updated")
                }
            }
        }
    }

    fun updateItem (itemAttribute: Item) {
        viewModelScope.launch {
            val map = HashMap<String, Any>()
            map[itemAttribute.itemId.toString()] = itemAttribute

            _databaseRef.updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("AddFragment", "Update operation successful")
                } else {
                    Log.v("AddFragment", "Item not successfully updated")
                }
            }
        }
    }

    fun deleteItem (itemAttribute: Item) {
        viewModelScope.launch {
            _databaseRef.child(itemAttribute.itemId.toString()).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.v("AddFragment", "Update operation successful")
                } else {
                    Log.v("AddFragment", "Item not successfully updated")
                }
            }
        }
    }

    fun getCurrentDateAndTime () : String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = Date()
        return simpleDateFormat.format(currentDateAndTime)
    }

    fun setProfit(cp: String, sp: String): String {
        return "%.2f".format(((sp.toDouble() - cp.toDouble()) / cp.toDouble() * 100))
    }
}

//class ShopikoViewModelFactory(val context: Context) :
//        ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ShopikoViewModel::class.java)) {
//            @Suppress("UNCHECKED CAST")
//            return ShopikoViewModel(context) as T
//        }
//        throw IllegalArgumentException("Unable to construct view model")
//    }
//}