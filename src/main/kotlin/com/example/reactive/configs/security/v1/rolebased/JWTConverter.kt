package com.example.reactive.configs.security.v1.rolebased

import com.example.reactive.configs.security.PBKDF2Encoder
import com.example.reactive.models.dto.request.AuthRequest
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.ResolvableType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.AbstractJackson2Decoder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

//@Component
class JWTConverter(private val jacksonDecoder: AbstractJackson2Decoder, private val passwordEncoder: PBKDF2Encoder) :
    ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> = mono {
        val loginRequest = getUsernameAndPassword(exchange!!) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        println("JWTConverter $loginRequest")
        return@mono UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
    }

    private suspend fun getUsernameAndPassword(exchange: ServerWebExchange): AuthRequest? {
        val dataBuffer = exchange.request.body
        val type = ResolvableType.forClass(AuthRequest::class.java)
        return jacksonDecoder
            .decodeToMono(dataBuffer, type, MediaType.APPLICATION_JSON, mapOf())
            .onErrorResume { Mono.empty<AuthRequest>() }
            .cast(AuthRequest::class.java)
            .awaitFirstOrNull()
    }
}