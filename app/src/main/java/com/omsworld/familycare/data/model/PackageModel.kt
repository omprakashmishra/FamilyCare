package com.omsworld.familycare.data.model

import java.io.Serializable

data class PackageModel(
    var PackageID: String = "",
    var PackageName: String = "",
    var Amount: String = "",
    var Type: String = "",
    var DisplayAmount: String = "",
    var DisplayAmountType: String = "",
    var PayableAmount: String = "",
    var setPackageDesc: String = ""
) : Serializable