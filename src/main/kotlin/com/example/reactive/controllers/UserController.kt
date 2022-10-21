package com.example.reactive.controllers

import com.example.reactive.configs.security.PBKDF2Encoder
import com.example.reactive.configs.security.JwtTokenProvider
import com.example.reactive.models.dto.request.AuthRequest
import com.example.reactive.models.dto.request.PasswordChangeRequest
import com.example.reactive.models.entity.User
import com.example.reactive.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.datetime.DateFormatter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.util.*

@RestController
class UserController @Autowired constructor(
    private val userService: UserService,
    private val passwordEncoder: PBKDF2Encoder,
    private val jwtTokenProvider: JwtTokenProvider
    ) {
    @InitBinder
    fun initBinder(binder: WebDataBinder){
        binder.addCustomFormatter(DateFormatter("yyyy-MM-dd"))
    }

    @ExceptionHandler
    fun exceptionHandle(ex: IOException): ResponseEntity<String>{
        return ResponseEntity("Custom Exception Handled!", HttpStatus.BAD_GATEWAY)
    }

    @GetMapping("/user/by/{loginId}")
    suspend fun getUserByLoginId(@PathVariable("loginId") loginId: String): ResponseEntity<Any>{
        val result = userService.getUser(loginId) ?: return ResponseEntity("not found", HttpStatus.NOT_FOUND)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/user/{cid}")
    suspend fun getUserByCid(@PathVariable("cid") cid: Long): ResponseEntity<Any>{
        val result = userService.getUserByCid(cid) ?: return ResponseEntity("not found", HttpStatus.NOT_FOUND)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/user/add")
    suspend fun addUser(@RequestBody user: User): ResponseEntity<String> {
        userService.addUser(user)
        return ResponseEntity("added", HttpStatus.OK)
    }

    @GetMapping("/users")
    suspend fun getAllUser(): ResponseEntity<Any>{
        val users = userService.getAllUser()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @DeleteMapping("/user/delete/{cid}")
    suspend fun deleteUser(@PathVariable cid: Long): ResponseEntity<String>{
        userService.deleteUserByCid(cid)
        return ResponseEntity("deleted!", HttpStatus.OK)
    }

    @PutMapping("/user/update")
    suspend fun updateUser(@RequestBody user: User): ResponseEntity<String>{
        userService.updateUser(user)
        return ResponseEntity("updated!", HttpStatus.OK)
    }

    @PostMapping("/login")
    suspend fun signIn(@RequestBody authRequest: AuthRequest): ResponseEntity<Any>{
        val user = userService.getUser(authRequest.username) ?: return ResponseEntity("Not found!", HttpStatus.NOT_FOUND)
        return if(passwordEncoder.matches(authRequest.password, user.loginPw)){
            ResponseEntity(jwtTokenProvider.generateToken(user.loginId, user.role), HttpStatus.OK)
        } else ResponseEntity("Login Failed!", HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/user/pwc")
    suspend fun chanePassword(@RequestBody pwChangeRequest: PasswordChangeRequest): ResponseEntity<Any>{
        val user = userService.getUser(pwChangeRequest.username) ?: return ResponseEntity("Not found!", HttpStatus.NOT_FOUND)
        userService.passwordChange(user.cid, passwordEncoder.encode(pwChangeRequest.newPassword))
        return ResponseEntity("password changed!", HttpStatus.OK)
//        return if(passwordEncoder.matches(pwChangeRequest.oldPassword, user.loginPw)){
//            userService.passwordChange(user.cid, passwordEncoder.encode(pwChangeRequest.newPassword))
//            ResponseEntity("password changed!", HttpStatus.OK)
//        } else ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}