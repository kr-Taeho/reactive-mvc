package com.example.reactive.configs.security.v1.contextbased

import com.example.reactive.configs.security.JwtTokenProvider
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
@AllArgsConstructor
class AuthenticationManager @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials.toString()
        println("AuthenticationManager Token $token")
        return Mono.just(jwtTokenProvider.validateToken(token))
            .filter { valid -> valid }
            .switchIfEmpty(Mono.empty()) // valid is false
            .map { _ -> // valid is true
                jwtTokenProvider.getAuthentication(token)
            }
    }
}