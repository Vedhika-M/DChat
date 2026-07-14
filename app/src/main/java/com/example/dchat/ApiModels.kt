package com.example.dchat

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String
)
data class SignupResponse(
    val token: String
)

data class LoginRequest(
    val username: String,
    val email: String,
    val password: String
)
data class LoginResponse(
    val access_token: String,
    val token_type: String
)

data class MessageRequest(
    val receiverId: Int,
    val content: String
)
data class MessageResponse(
    val response: String
)

data class User(
    val id: Int,
    val username: String
)

data class UserMessageResponse(
    val id:Int,
    @SerializedName("sender_id")
    val senderId:Int,
    @SerializedName("receiver_id")
    val receiverId:Int,
    val content:String,
    @SerializedName("timestamp")
    val timeStamp:String

)