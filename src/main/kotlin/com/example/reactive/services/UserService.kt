package com.example.reactive.services

import com.example.reactive.models.entity.User
import com.example.reactive.repositories.UserRepository
import kotlinx.coroutines.flow.toList
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class UserService @Autowired constructor(private val userRepository: UserRepository) {
    suspend fun getUser(loginId: String): User? {
        return userRepository.findUserByLoginId(loginId)
    }
    suspend fun addUser(user: User): User {
        return userRepository.save(user)
    }
    suspend fun getUserByCid(cid: Long): User? {
        return userRepository.select(cid)
    }
    suspend fun getAllUser(): List<User> = userRepository.findAll().toList()

    suspend fun deleteUserByCid(cid: Long) {
        userRepository.deleteById(cid)
    }
    suspend fun updateUser(user: User): User {
        return userRepository.updateUser(user)
    }

    suspend fun passwordChange(cid: Long, encPw: String) {
        userRepository.updatePassword(cid, encPw)
    }


}