package com.example.todolistapp.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _todo = MutableStateFlow<Todo?>(null)
    val todo: StateFlow<Todo?> = _todo

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTodo(todoId: Int?) {
        if (todoId == null) {
            _error.value = "Invalid todo ID"
            _todo.value = null
            return
        }
        viewModelScope.launch {
            try {
                repository.getTodoById(todoId).collect { todo ->
                    _todo.value = todo
                    _error.value = if (todo == null) "Todo not found" else null
                }
            } catch (e: Exception) {
                _error.value = "Failed to load todo: ${e.message}"
                _todo.value = null
            }
        }
    }
}