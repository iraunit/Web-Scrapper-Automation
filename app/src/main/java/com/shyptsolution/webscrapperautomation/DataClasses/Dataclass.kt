package com.shyptsolution.webscrapperautomation.DataClasses

import android.app.Activity
import android.provider.ContactsContract.CommonDataKinds.Email

data class ScrapEntity(
    var name:String,
    var pic_url:String,
    var activity: Activity
)

data class LinkedInCommentEntity(
    var name:String,
    var headline:String,
    var comment:String,
    var email:String,
    var profileLink:String
)

data class allFilesEntity(
    var name:String
)