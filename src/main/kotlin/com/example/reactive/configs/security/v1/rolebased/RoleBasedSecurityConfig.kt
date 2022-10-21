package com.example.reactive.configs.security.v1.rolebased

import com.example.reactive.configs.security.JwtTokenProvider
import com.example.reactive.configs.security.PBKDF2Encoder
import com.example.reactive.configs.security.UrlPaths
import com.example.reactive.configs.security.v1.SecurityProblemSupport
import com.example.reactive.configs.security.v1.filterbased.JwtTokenAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.codec.json.AbstractJackson2Decoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

//@AllArgsConstructor
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
class RoleBasedSecurityConfig @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider
)
{
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, jwtAuthenticationFilter: AuthenticationWebFilter): SecurityWebFilterChain {
        println("Role Based")
        return http
            .exceptionHandling()
            .and()
            .csrf().disable()
            .authorizeExchange {
                it.pathMatchers(*UrlPaths.ALL_PATHS).permitAll()
                    // 특정 역할을 가진 사용자 (UserDetailsService에서 역할을 확인하게 된다.)
                    .pathMatchers("/admin/**").hasAnyRole("ADMIN")
                    .pathMatchers("/todo/**").hasAnyRole("ADMIN", "USER")
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) // 로그인할때 인증
            .addFilterAt(JWTReactiveAuthorizationFilter(jwtTokenProvider), SecurityWebFiltersOrder.AUTHORIZATION) // 로그인 이후 검증
            .build()
    }


    @Bean
    fun jacksonDecoder(): AbstractJackson2Decoder = Jackson2JsonDecoder()

    @Bean
    fun authenticationWebFilter(reactiveAuthenticationManager: ReactiveAuthenticationManager,
                                jwtConverter: JWTConverter,
                                serverAuthenticationSuccessHandler: ServerAuthenticationSuccessHandler,
                                jwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler
    ): AuthenticationWebFilter {
        val authenticationWebFilter = AuthenticationWebFilter(reactiveAuthenticationManager)
        authenticationWebFilter.setRequiresAuthenticationMatcher { ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/role-login").matches(it) }
        authenticationWebFilter.setServerAuthenticationConverter(jwtConverter)
        authenticationWebFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler)
        authenticationWebFilter.setAuthenticationFailureHandler(jwtServerAuthenticationFailureHandler)
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        return authenticationWebFilter
    }

    @Bean
    fun reactiveAuthenticationManager(reactiveUserDetailsService: CustomerReactiveUserDetailsService,
                                      passwordEncoder: PBKDF2Encoder
    ): ReactiveAuthenticationManager {
        val manager = UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService)
        manager.setPasswordEncoder(passwordEncoder)
        return manager
    }
}