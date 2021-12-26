package com.example.nss.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Activity(
    var activityName: String? = null,
    var activityIdentity: String? = null,
    var activityDescription: String? = null,
    var activityDate: String? = null,
    var activityTime: String? = null,
    var link: String? = null,
    var creation_time_ms: String? = null,
) : Parcelable
