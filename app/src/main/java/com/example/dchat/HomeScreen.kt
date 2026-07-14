package com.example.dchat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

  /*  val socket= ChatWebsocket()
    socket.connect()
    webSocket.send*/

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getAllUsers()
            registeredUsers = response
            filteredUsers = response
        }catch (e: Exception) {
            print(e.localizedMessage)
        }
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

        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(
                filteredUsers
            ){receiver ->
                ChatUserItem(
                    receiver = receiver,
                    onClick = {
                        SessionManager.receiverId = receiver.id
                        navController.navigate("chatscreen")
                    }
                )
            }
        }
    }
}