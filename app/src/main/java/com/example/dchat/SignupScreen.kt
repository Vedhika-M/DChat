package com.example.dchat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun SignupScreen(
    navController: NavController
){
    var username by remember {
        mutableStateOf("")
    }
    var passcode by remember {
        mutableStateOf("")
    }
    var email by remember{
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SIGN UP",
            fontSize = 50.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))

        TextField(
            value = username,
            onValueChange = {
                username=it
            },
            label = {
                Text("Username")
            },
            singleLine=true
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

        TextField(
            value = email,
            onValueChange = {
                email=it
            },
            label = {
                Text("Mail ID")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

        TextField(
            value = passcode,
            onValueChange = {
                passcode=it
            },
            label = {
                Text("Password")
            },
            singleLine = true
        )

        Button(
            onClick = {
                if (username.isNotEmpty() && passcode.isNotEmpty()){
                    scope.launch{
                        try {
                            val response= RetrofitInstance.api.signup(SignupRequest(
                                username = username,
                                email = email,
                                password = passcode
                            ))
                            navController.navigate("loginscreen")
                        }catch (e: HttpException) {
                            println("HTTP ${e.code()}")

                            val errorBody = e.response()?.errorBody()?.string()
                            println(errorBody)

                            errorMessage = "HTTP ${e.code()}"
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                            errorMessage = e.message ?: "Unknown error"
                        }
                    }
                }
            }
        ) {
            Text("ENTER")
        }

        if (errorMessage.isNotEmpty()){
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }
    }
}