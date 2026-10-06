package com.omsworld.familycare.core

/**
 * Global constants. Replaces the old GlobalConstants.java.
 * All keys used in SharedPreferences live here.
 */
object Constants {

    // ========== User ==========
    const val USER_ID = "userId"
    const val ASPNETUSERID = "AspnetUserID"
    const val PHONE_NUMBER = "phoneNumber"
    const val MOBILE_only = "MOBILE_only"
    const val SUBSCRIPTION_ID = "subscriptionId"
    const val EMAIL = "email"
    const val USER_NAME = "userName"
    const val USER_IMAGE = "userImage"
    const val USER_DOB = "dateOfBirth"
    const val USER_ABOUT = "about on self"

    // ========== Login / Session ==========
    const val LOGIN_STATUS = "loginstatus"
    const val PASSWORD = "password"

    // ========== Family ==========
    const val FAMILY_ID = "FamilyID"
    const val FAMILY_NAME = "FamilyName"
    const val IsFamilyAdmin = "FamilyAdmin"

    // ========== Package / Subscription ==========
    const val PACKAGE_ID = "packageId"
    const val PACKAGE_NAME = "packageName"
    const val PACKAGE_AMOUNT = "packageAmount"
    const val PROMO_CODE = "promoCode"
    const val PAYABLE_AMOUNT_TYPE = "AmountType"

    // ========== Firebase / Notifications ==========
    const val Firebasetoken = "Firebasetoken"
    const val NotificationData = "NotificationData"
    const val NOTIFICATION = "Notification"
    const val ISDOZEDISABLED = "IsDozeDisabled"

    // ========== Job / Location ==========
    const val ASSIGN_JOB_STATUS = "AssignJobStatus"
    const val JOB_NEW_LATITUDE = "newlattitude"
    const val JOB_NEW_LONGITUDE = "newlongitude"
    const val JOB_NEW_ACCURACY = "newaccuracy"
    const val JOB_NEW_PROVIDER = "newprovider"
    const val JOB_CurrentAddress = "CurrentAddress"
    const val MyJOB_START_TIME_LAT = "MyJobSOSorAcceptTimeLatitude"
    const val MyJOB_START_TIME_LONG = "MyJobSOSorAcceptTimeLongitude"

    // ========== Payment ==========
    const val PaymentStatusCode = "PaymentStatusCode"
    const val PAYMENTSTATUS = "PaymentStatus"
    const val PAYUMONEYKEY = "PayumoneyKey"
    const val PaymentDoneSeverFailed = "paymentDoneServerUpdateFailed"
    const val PAYMENTsubscriptionID = "PAYMENTsubscriptionID"
    const val paymentStatus = "paymentStatus"
    const val PAYMENTpaymentId = "PAYMENTpaymentId"
    const val PAY_SUB_RESPONSE = "PaymentSubscriptionResponse"

    // ========== Alarm / Tracking ==========
    const val ALARM_MUTE_STATUS = "AlarmMuteStatus"
    const val ALARM_RING_STOP = "AlarmRingStop"
    const val FriendRequestCount = "FriendRequestCount"
    const val ISFRIENDSTRACKING = "IsFriendsTracking"

    // ========== Service ==========
    const val RESPOSE_CLG_JB_SERVICE = "RESPOSE_CLG_JB_SERVICE"

    // ========== Account ==========
    const val ACCOUNT_ISDELETED = "ACCOUNT_IS_DELETED"
    const val LEGALAGREEMENTCHECK = "legalagreement"

    // ========== FTP / FTP config ==========
    const val Ftpadress = "Ftpadress"
    const val WebsiteUrl = "WebsiteUrl"
    const val ftpUserName = "ftpUserName"
    const val ftpPassword = "ftpPassword"

    // ========== Scan ==========
    const val LAST_SCAN_RESULT = "last_scan_was"

    // ========== Misc static ==========
    const val fromPage = "FROMPAGE"
    const val safeJone = "YourLocationOnOFF"

    // ========== Organization ==========
    const val ORGANIZATIONID = "ORGANIZATIONID"
    const val ACTIVATIONCODE = "ActivationCode"
    const val COUNTYCD = "countyCd"
    const val COUNTRYCODEID = "countrycodeID"
    const val USER_PAYED = "user_free_or_payed"
    const val INVITEE_STATUSID = "inviteestatusid"

    // ========== Mutable static (kept as @JvmField for cross-language use) ==========
    @JvmField var scan_result: String = ""
    @JvmField var sosBymeClick: String = "0"
}