package com.example.todolistapp

import com.example.todolistapp.ui.theme.TodoAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.screens.TodoDetailScreen
import com.example.todolistapp.screens.TodoListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "todo_list"
                    ) {
                        composable("todo_list") {
                            TodoListScreen(
                                onTodoClick = { todoId ->
                                    navController.navigate("todo_detail/$todoId")
                                }
                            )
                        }
                        composable("todo_detail/{todoId}") { backStackEntry ->
                            val todoId = backStackEntry.arguments?.getString("todoId")?.toIntOrNull()
                            TodoDetailScreen(
                                todoId = todoId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}