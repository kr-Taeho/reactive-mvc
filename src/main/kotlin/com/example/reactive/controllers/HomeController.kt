package com.example.reactive.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping("/")
    suspend fun index(): ResponseEntity<String> = ResponseEntity("Hello Kopring Webflux!", HttpStatus.OK)

    @GetMapping("/admin/test")
    suspend fun admin(): ResponseEntity<String> = ResponseEntity("ADMIN API URL", HttpStatus.OK)
}