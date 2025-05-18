package com.example.todolistapp.data.remote


import com.example.todolistapp.data.Todo
import retrofit2.http.GET
import retrofit2.http.Path

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<TodoDto>
    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") todoId: Int): Todo
}