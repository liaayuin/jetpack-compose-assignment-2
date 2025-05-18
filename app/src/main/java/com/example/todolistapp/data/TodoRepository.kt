package com.example.todolistapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todolistapp.data.local.TodoDao
import com.example.todolistapp.data.local.TodoEntity
import com.example.todolistapp.data.remote.TodoApi
import com.example.todolistapp.data.remote.TodoDto
import com.example.todolistapp.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoApi: TodoApi,
    private val todoDao: TodoDao
) {
    fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toTodo() }
        }
    }

    fun getTodoById(id: Int): Flow<Todo?> {
        return todoDao.getTodoById(id).map { it?.toTodo() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshTodos(): NetworkResult<Unit> {
        return try {
            val todos = todoApi.getTodos()
            todoDao.clearAll()
            todoDao.insertAll(todos.map { it.toEntity() })
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch todos")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun TodoDto.toEntity() = TodoEntity(
        id = id,
        userId = userId,
        title = title,
        completed = completed,
        description = "Mock description for ${title} (as of ${LocalDateTime.now()})"
    )

    private fun TodoEntity.toTodo() = Todo(
        id = id,
        userId = userId,
        title = title,
        completed = completed,
        description = description
    )
}