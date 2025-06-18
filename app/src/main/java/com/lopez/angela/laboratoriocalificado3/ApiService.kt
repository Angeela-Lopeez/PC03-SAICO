package com.lopez.angela.laboratoriocalificado3

import com.lopez.angela.laboratoriocalificado3.Teacher
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("list/teacher-b")
    suspend fun getTeachers(): Response<TeacherResponse>
}