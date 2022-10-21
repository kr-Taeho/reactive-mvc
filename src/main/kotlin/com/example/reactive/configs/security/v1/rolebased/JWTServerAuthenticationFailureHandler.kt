package com.example.reactive.configs.security.v1.rolebased

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class JWTServerAuthenticationFailureHandler : ServerAuthenticationFailureHandler {
    override fun onAuthenticationFailure(webFilterExchange: WebFilterExchange?, exception: AuthenticationException?): Mono<Void> = mono {
        val exchange = webFilterExchange?.exchange ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        println("UNAUTHORIZED - ${exchange.request.path}")
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        exchange.response.setComplete().awaitFirstOrNull()
    }
}