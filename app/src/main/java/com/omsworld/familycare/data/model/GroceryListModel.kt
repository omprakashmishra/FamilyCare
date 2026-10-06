package com.omsworld.familycare.data.model

data class GroceryListModel(
    var added_date: String = "",
    var addedItemModels: ArrayList<AddedItemModel> = ArrayList()
)