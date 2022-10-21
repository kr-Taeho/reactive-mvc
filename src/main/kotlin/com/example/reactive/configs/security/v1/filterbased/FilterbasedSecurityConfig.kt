package com.example.reactive.configs.security.v1.filterbased

import com.example.reactive.configs.security.JwtTokenProvider
import com.example.reactive.configs.security.UrlPaths
import com.example.reactive.configs.security.v1.SecurityProblemSupport
import lombok.AllArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class FilterbasedSecurityConfig @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val problemSupport: SecurityProblemSupport,
)
{
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        logger.info("Filter Based")
        return http
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange {
                it.pathMatchers(*UrlPaths.ALL_PATHS).permitAll()
                    // 특정 권한을 가진 사용자 (GrantedAuthority를 가지고 권한을 확인한다.)
                .pathMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .pathMatchers("/todo/**").hasAnyAuthority("ADMIN", "USER")
                .anyExchange().authenticated()
            }
            .addFilterAt(JwtTokenAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC) // 명시적으로 Filter를 삽입할 수도 있음.
            .build()
    }


    // 인증 확인 필터
    // @Bean // Bean주입을 하면 필터는 자동으로 실행됨
    fun getJwtAuthorizationFilter(jwtTokenProvider: JwtTokenProvider) = JwtTokenAuthenticationFilter(jwtTokenProvider)

}