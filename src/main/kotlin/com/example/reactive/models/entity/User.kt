package com.example.reactive.models.entity

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import lombok.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
data class User(
    @Id
    @Column("cid")
    val cid: Long,

    @Column("login_id")
    val loginId: String,
    @Column("login_pw")
    val loginPw: String,
    @Column("name")
    val name: String,
    @Column("email")
    val email: String?,
    @Column("enabled")
    val enabled: Boolean,
    @Column("role")
    val role: String
)
