package com.example.reactive.services

import com.example.reactive.configs.cache.*
import com.example.reactive.models.dto.request.TodoAddRequest
import com.example.reactive.models.entity.Todo
import com.example.reactive.repositories.TodoRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class TodoService @Autowired constructor(
    val todoRepository: TodoRepository,
    val redisUtils: RedisUtils
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    suspend fun getAllTodos(): List<Todo> = todoRepository.findAll().toList()
    suspend fun getTodoByCid(cid: Long) = todoRepository.findById(cid)
    suspend fun deleteTodoByCid(cid: Long) = todoRepository.deleteById(cid)
    suspend fun addTodo(todo: Todo) {
        todoRepository.save(todo)
    }
    suspend fun addTodo(todo: TodoAddRequest): List<TodoAddRequest>{
        logger.info(todo.title)
        val todos = mutableMapOf<String, TodoAddRequest>()
        (0 until  10).forEach {
            todos[todo.title + it] = TodoAddRequest(todo.title+it, todo.content+it, todo.done)
        }
        redisUtils.hset("todos", redisUtils.mapValueObjectAsJson(todos))
        redisUtils.hdelete("todos", *todos.toList().take(5).map { it.first }.toTypedArray())
        return redisUtils.hgetAll("todos").map { redisUtils.jsonAsObject(it) }
//        return redisUtils.hgetMulti("todos", *todos.map { it.value.title }.toTypedArray()).map { redisUtils.jsonAsObject(it) }
//        redisUtils.set("todo", redisUtils.objectAsJson(todo))
//        return redisUtils.get("todo")?.let { redisUtils.jsonAsObject(it) }
    }


    suspend fun updateTodoDone(cid: Long, done: Boolean) {
        todoRepository.updateTodoDone(cid, done)
    }
    suspend fun updateTodoContent(cid: Long, title: String, content: String?, done: Boolean){
        todoRepository.updateTodoContent(cid, title, content, done)
    }
}