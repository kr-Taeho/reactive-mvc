package com.example.reactive.configs.security.v1.contextbased

import com.example.reactive.configs.security.JwtTokenProvider
import com.example.reactive.configs.security.UrlPaths
import com.example.reactive.configs.security.v1.SecurityProblemSupport
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


//@AllArgsConstructor
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
class ContextBasedSecurityConfig @Autowired constructor(
    private val problemSupport: SecurityProblemSupport,
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository
)
{

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        println("ContextRepository And Manager Based Security")
        return http
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange {
                it.pathMatchers(*UrlPaths.ALL_PATHS).permitAll()
                    // 특정 권한을 가진 사용자 (GrantedAuthority를 가지고 권한을 확인한다.)
                .pathMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .pathMatchers("/todo/**").hasAnyAuthority("ADMIN", "USER")
                .anyExchange().authenticated()
            }
            .build()
    }
}