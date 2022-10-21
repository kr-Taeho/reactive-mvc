package com.example.reactive.models.dto.response

import java.time.LocalDateTime
import java.util.*

data class TodoResponse(
    var title: String,
    var content: String?,
    var createdAt: Date,
    var updatedAt: Date,
    var done: Boolean,
    var writer: String,
)
