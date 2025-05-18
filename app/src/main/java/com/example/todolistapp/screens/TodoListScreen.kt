package com.example.todolistapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistapp.data.Todo
import com.example.todolistapp.util.NetworkResult
import com.example.todolistapp.viewmodels.TodoListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onTodoClick: (Int) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos = viewModel.todos.collectAsState().value
    val networkState = viewModel.networkState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Todo List",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3E78B2),
                    titleContentColor = Color.White
                ),
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F4F8))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                when (networkState) {
                    is NetworkResult.Loading -> {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        LaunchedEffect(networkState) {
                            snackbarHostState.showSnackbar(
                                message = networkState.message,
                                actionLabel = "Retry",
                                duration = SnackbarDuration.Short
                            ).let { result ->
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.refreshTodos()
                                }
                            }
                        }
                        Text(
                            text = "Using cached data",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                    is NetworkResult.Success -> {
                        if (todos.isNotEmpty()) {
                            Text(
                                text = "Fetched from ${if (networkState is NetworkResult.Success) "network" else "cache"}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                    null -> Unit
                }

                if (todos.isEmpty() && networkState !is NetworkResult.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Todos Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todos) { todo ->
                            TodoItem(
                                todo = todo,
                                onClick = { onTodoClick(todo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(todo: Todo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E78B2).copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (todo.completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (todo.completed) Icons.Filled.Check else Icons.Filled.AccessTime,                        contentDescription = if (todo.completed) "Completed" else "Pending",
                        tint = if (todo.completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }
            Text(
                text = if (todo.completed) "Done" else "Pending",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                color = if (todo.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
