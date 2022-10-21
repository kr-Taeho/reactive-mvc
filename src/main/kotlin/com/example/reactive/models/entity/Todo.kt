package com.example.reactive.models.entity

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*


@Data
@Table("todos")
data class Todo(
    @Id
    @Column("cid")
    val cid: Long,

    @Column("user_cid")
    val userCid: Long,

    @Column("title")
    val title: String,
    @Column("content")
    val content: String?,
    @Column("created_at")
    val createdAt: Date,
    @Column("updated_at")
    val updatedAt: Date,
    @Column("done")
    val done: Boolean,
)
