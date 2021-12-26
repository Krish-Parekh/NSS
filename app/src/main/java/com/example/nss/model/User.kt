package com.example.nss.model

data class User(
    var name: String? = null,
    var email: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var core: Boolean? = false,
    var member: Boolean? = false,
    var Ereport: Boolean? = false,
    var Ephoto: Boolean? = false
)
