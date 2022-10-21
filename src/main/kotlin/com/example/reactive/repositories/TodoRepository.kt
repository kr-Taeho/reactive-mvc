package com.example.reactive.repositories

import com.example.reactive.models.entity.Todo
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : CoroutineCrudRepository<Todo, Long> {

    @Modifying
    @Query("UPDATE todos SET done = :done WHERE cid = :cid")
    suspend fun updateTodoDone(cid: Long, done: Boolean)

    @Modifying
    @Query("UPDATE todos SET title = :title, content = :content, done = :done, updated_at = now() WHERE cid = :cid")
    suspend fun updateTodoContent(cid: Long, title: String, content: String?, done: Boolean)
}