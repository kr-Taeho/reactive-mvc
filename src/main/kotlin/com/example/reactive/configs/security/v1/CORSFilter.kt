package com.example.reactive.configs.security.v1

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
class CORSFilter : WebFluxConfigurer {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun addCorsMappings(registry: CorsRegistry) {
        logger.info("CORSFilter")
        registry.addMapping("/**")
            .allowedOrigins("/**")
            .allowedMethods("*")
            .allowedHeaders("*")
    }
}