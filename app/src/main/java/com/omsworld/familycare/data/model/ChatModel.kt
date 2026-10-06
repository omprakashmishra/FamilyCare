package com.omsworld.familycare.data.model

data class ChatModel(
    var message_id: String = "",
    var message: String = "",
    var sender_id: String = "",
    var time: String = "",
    var type: String = "",
    var freind_id: String = "",
    var freind_fullname: String = "",
    var freind_img: String = ""
)