package com.example.reactive.repositories

import com.example.reactive.models.entity.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : CoroutineCrudRepository<User, Long>{

    suspend fun findUserByLoginId(loginId: String): User?

    @Modifying
    @Query("UPDATE users SET name = :#{#user.name} WHERE cid = :#{#user.cid}")
    suspend fun updateUser(@Param("user") user: User): User

    @Query("SELECT * FROM users WHERE cid = :cid")
    suspend fun select(cid: Long): User?

    @Query("UPDATE users SET login_pw = :encPw WHERE cid = :cid")
    suspend fun updatePassword(cid: Long, encPw: String)
}