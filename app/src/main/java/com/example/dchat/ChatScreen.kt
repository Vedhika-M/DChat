package com.example.dchat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatScreen(
    navController: NavController
) {
    val chatWebsocket = remember {
        ChatWebsocket()
    }

    val connectionState by chatWebsocket.connectionState.collectAsState()

    val messages = remember {
        mutableStateListOf<UserMessageResponse>()
    }

    var currentMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val history = RetrofitInstance.api.getMessages(SessionManager.receiverId)
        messages.addAll(history)

        chatWebsocket.connect(
            token = SessionManager.jwtToken
        ) { message ->
            messages.add(message)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chatWebsocket.disConnect()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = when (connectionState) {
                ConnectionStatus.CONNECTED -> "Connected"
                ConnectionStatus.CONNECTING -> "Connecting..."
                ConnectionStatus.DISCONNECTED -> "Disconnected"
                ConnectionStatus.FAILED -> "Connection Failed"
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages) { message ->
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Row {
            TextField(
                value = currentMessage,
                onValueChange = {
                    currentMessage=it
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                enabled = connectionState == ConnectionStatus.CONNECTED,
                onClick = {
                    if (currentMessage.isNotBlank()) {
                        chatWebsocket.send(
                            receiverId = SessionManager.receiverId,
                            message = currentMessage
                        )
                        currentMessage = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}