package com.example.reactive.configs.security.v1

import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import kotlin.math.log

@Slf4j
@Component
class SecurityProblemSupport : ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {
    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> =
        Mono.fromRunnable {
            println("UNAUTHORIZED - ${exchange.request.path}")
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        }

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> =
        Mono.fromRunnable {
            println("FORBIDDEN - ${exchange.request.path}")
            exchange.response.statusCode = HttpStatus.FORBIDDEN
        }

}