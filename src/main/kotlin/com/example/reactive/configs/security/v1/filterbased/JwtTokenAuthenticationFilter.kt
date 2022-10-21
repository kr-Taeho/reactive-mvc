package com.example.reactive.configs.security.v1.filterbased

import com.example.reactive.configs.security.JwtTokenProvider
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@RequiredArgsConstructor
//@Component //Component를 달면 자동으로 Filter가 실행됨.
class JwtTokenAuthenticationFilter constructor(
    private val jwtTokenProvider: JwtTokenProvider
) : WebFilter {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val HEADER_PREFIX = "Bearer ";


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = resolveToken(exchange.request) ?: return chain.filter(exchange)
        logger.info("Filtered Token : $token")
        if (StringUtils.hasText(token) && this.jwtTokenProvider.validateToken(token)) {
            val authentication: Authentication = this.jwtTokenProvider.getAuthentication(token)
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        }
        return chain.filter(exchange)
    }

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return null
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            bearerToken.substring(7)
        } else null
    }

}