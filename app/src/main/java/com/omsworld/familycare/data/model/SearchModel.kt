package com.omsworld.familycare.data.model

import java.util.Comparator

data class SearchModel(
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var comment: String = "",
    var added_date: String = "",
    var added_by: String = "",
    var url: String = ""
) {
    val ascending: Comparator<SearchModel> = Comparator { s1, s2 ->
        s1.name.uppercase().compareTo(s2.name.uppercase())
    }

    val descending: Comparator<SearchModel> = Comparator { s1, s2 ->
        s2.name.uppercase().compareTo(s1.name.uppercase())
    }
}