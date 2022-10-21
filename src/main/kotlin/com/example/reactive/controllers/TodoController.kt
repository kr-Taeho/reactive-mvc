package com.example.reactive.controllers

import com.example.reactive.models.dto.request.TodoAddRequest
import com.example.reactive.services.TodoService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class TodoController @Autowired constructor(val todoService: TodoService) {

    @GetMapping("/todo/{cid}")
    suspend fun getTodo(@PathVariable cid: Long): ResponseEntity<Any> {
        val todo = todoService.getTodoByCid(cid) ?: return ResponseEntity("Not Found!", HttpStatus.NOT_FOUND)
        return ResponseEntity(todo, HttpStatus.OK)
    }

    @PostMapping("/todo/add")
    suspend fun addTodo(@RequestBody todo: TodoAddRequest, authentication: Authentication): ResponseEntity<List<TodoAddRequest>>{
        val rst = todoService.addTodo(todo)
        return ResponseEntity(rst, HttpStatus.OK)
    }
}