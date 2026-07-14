package com.example.dchat

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/signup")
    suspend fun signup(
        @Body request:SignupRequest
    ):SignupResponse

    @FormUrlEncoded
    @POST("/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ):LoginResponse

    @POST("/messages/send")
    suspend fun sendMessage(
        @Body request: MessageRequest
    ):MessageResponse

    @GET("/users")
    suspend fun getAllUsers(): List<User>

    @GET("/messages/{id}")
    suspend fun getMessages(
        @Path("id") receiverId: Int
    ): List<UserMessageResponse>
}
