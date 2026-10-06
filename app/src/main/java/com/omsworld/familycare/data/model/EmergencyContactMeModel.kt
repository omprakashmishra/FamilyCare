package com.omsworld.familycare.data.model

data class EmergencyContactMeModel(
    var AspnetUserID: String = "",
    var UserName: String = "",
    var MobileNumber: String = "",
    var Latitude: String = "",
    var Longitude: String = "",
    var Address: String = "",
    var Battery: String = "",
    var SOSStatus: String = "",
    var OnlineStatus: String = "",
    var IsAlertAccept: String = "",
    var IsAlertCancel: String = "",
    var UserInteractionID: String = "",
    var Lastseen: String = ""
)