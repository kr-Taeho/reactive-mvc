package com.example.reactive.models.dto.request

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import lombok.ToString
import org.springframework.data.redis.core.RedisHash


data class TodoAddRequest(
    var title: String,
    var content: String?,
    var done: Boolean,
)