package com.omsworld.familycare.data.model

import java.io.Serializable

data class NewsEventsModel(
    var id: String = "",
    var added_date: String = "",
    var title: String = "",
    var category: String = "",
    var discription: String = "",
    var news_type: String = "",
    var image: String = "",
    var like_count: String = "",
    var like: String = ""
) : Serializable