package com.example.todolistapp.viewmodels


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.TodoRepository
import com.example.todolistapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _networkState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val networkState: StateFlow<NetworkResult<Unit>?> = _networkState

    init {
        loadTodos()
        refreshTodos()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshTodos() {
        viewModelScope.launch {
            _networkState.value = NetworkResult.Loading
            _networkState.value = repository.refreshTodos()
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            repository.getAllTodos().collect { todos ->
                _todos.value = todos
            }
        }
    }
}