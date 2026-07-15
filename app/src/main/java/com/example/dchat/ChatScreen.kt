package com.example.dchat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreen() {
    val chatWebsocket = remember {
        ChatWebsocket()
    }

    val connectionState by chatWebsocket.connectionState.collectAsState()

    val messages = remember {
        mutableStateListOf<UserMessageResponse>()
    }

    val listState = rememberLazyListState()

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chatWebsocket.disConnect()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.lake_bg),
            contentDescription = "lake view background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00695C))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = SessionManager.receiverUsername,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (connectionState) {
                    ConnectionStatus.CONNECTED -> "Connected"
                    ConnectionStatus.CONNECTING -> "Connecting..."
                    ConnectionStatus.DISCONNECTED -> "Unable to connect"
                    ConnectionStatus.FAILED -> "Connection Failed"
                },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(messages) { message ->
                val isOther = message.receiverId == SessionManager.receiverId

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isOther)
                        Arrangement.End
                    else
                        Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isOther)
                            Color(0xFF00695C)
                        else
                            Color(0xFFF1F3F4),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            ),
                            color = if (isOther)
                                Color.Black
                            else
                                Color.DarkGray
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Type a message...")
                },
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                maxLines = 6
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (currentMessage.isNotBlank()) {
                        chatWebsocket.send(
                            receiverId = SessionManager.receiverId,
                            message = currentMessage
                        )
                        currentMessage = ""
                    }
                },
                containerColor = Color(0xFF00695C),
                modifier = Modifier.size(56.dp)
            ) {
                Text("Send")
            }
        }
    }
}