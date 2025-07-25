package com.gi.cryptochat.view.chatroom

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatRoomListView(
    onChatRoomSelected: (String) -> Unit,
    chatRoomListViewModel: ChatRoomListViewModel = viewModel()
) {
    val chatRooms by chatRoomListViewModel.chatRooms.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF2575FC))
    )

    fun resetDialogState() {
        showDialog = false
        newRoomName = ""
        errorText = ""
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chat Rooms",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                Modifier.background(brush = gradientBrush),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                containerColor = Color.Transparent,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)

            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create Chat Room",
                )
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chatRooms) { room ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onChatRoomSelected(room.name) }
                        .clip(RoundedCornerShape(16.dp))
                        .drawWithCache {
                            onDrawBehind { drawRect(gradientBrush) }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                )
                {
                    Text(
                        room.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Text(
                        "Created by ${room.creator}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
    if (showDialog) {
        key(errorText) {
            AlertDialog(
                onDismissRequest = { resetDialogState() },
                title = { Text("Create Chat Room") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newRoomName,
                            onValueChange = { newRoomName = it },
                            label = { Text("Room Name") }
                        )
                        if (errorText.isNotEmpty()) {
                            Log.d("UI@@", "Showing error inside dialog: $errorText") // New log
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorText,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        chatRoomListViewModel.createChatRoom(
                            newRoomName,
                            onSuccess = { resetDialogState() },
                            onError = { error -> errorText = error }
                        )
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { resetDialogState() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}