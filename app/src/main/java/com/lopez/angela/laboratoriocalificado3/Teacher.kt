package com.lopez.angela.laboratoriocalificado3

import com.google.gson.annotations.SerializedName

data class Teacher(
    @SerializedName("name")
    val name: String,

    @SerializedName("last_name")
    val lastname: String,

    @SerializedName("image_url")
    val photoUrl: String,

    @SerializedName("phone_number")
    val phone: String,

    @SerializedName("email")
    val email: String
)
