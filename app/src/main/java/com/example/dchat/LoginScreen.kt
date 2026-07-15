package com.example.dchat

import retrofit2.HttpException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController
){
    var username by remember {
        mutableStateOf("")
    }
    var passcode by remember {
        mutableStateOf("")
    }

    val scope= rememberCoroutineScope()

    var errorMessage by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.raindrop_bg),
            contentDescription = "rain drop background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LOGIN",
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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (username.isNotEmpty() && passcode.isNotEmpty()){
                    scope.launch{
                        try {
                            val response= RetrofitInstance.api.login(
                                username = username,
                                password = passcode
                            )
                            SessionManager.jwtToken=response.access_token
                            navController.navigate("homescreen")
                        } catch (e: HttpException) {
                            errorMessage = when (e.code()) {
                                400 -> "Invalid username or password."
                                401 -> "Invalid username or password."
                                404 -> "User not found."
                                500 -> "Server error. Please try again."
                                else -> "Oops! Something went wrong..."
                            }
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

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ){
        Text("not signed up yet?")
        Button(
            onClick = {
                navController.navigate("signupscreen")
            }
        ) {
            Text("Sign up")
        }
    }
}