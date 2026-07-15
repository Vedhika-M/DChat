package com.example.dchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
){
    var searchValue by remember{
        mutableStateOf("")
    }

    var registeredUsers by remember {
        mutableStateOf<List<User>>(
            emptyList()
        )
    }

    var filteredUsers by remember {
        mutableStateOf<List<User>>(
            emptyList()
        )
    }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getAllUsers()
            registeredUsers = response
            filteredUsers = response
        }catch (e: Exception) {
            print(e.localizedMessage)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.coffeeshop_bg),
            contentDescription = "coffee shop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = searchValue,
            onValueChange = {
                searchValue=it
                if (it.isNotEmpty()) {
                    filteredUsers = registeredUsers.filter {
                        it.username.contains(
                            searchValue,
                            ignoreCase = true
                        )
                    }
                }else{
                    filteredUsers = registeredUsers
                }
            },
            label = {
                Text("Search")
            },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = RectangleShape
        )){
            items(
                filteredUsers
            ){receiver ->
                ChatUserItem(
                    receiver = receiver,
                    onClick = {
                        SessionManager.receiverId = receiver.id
                        SessionManager.receiverUsername = receiver.username
                        navController.navigate("chatscreen")
                    }
                )
            }
        }
    }
}