package com.lopez.angela.laboratoriocalificado3

import com.google.gson.annotations.SerializedName

data class TeacherResponse(
    @SerializedName("teachers")
    val teachers: List<Teacher>
)