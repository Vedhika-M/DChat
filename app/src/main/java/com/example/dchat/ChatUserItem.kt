package com.example.dchat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatUserItem(
    receiver: User,
    onClick: (User) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(receiver)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(30.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = receiver.username,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 6.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.7f)
    )
}