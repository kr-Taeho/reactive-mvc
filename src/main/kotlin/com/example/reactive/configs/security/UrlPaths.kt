package com.example.reactive.configs.security

object UrlPaths {
    val EXCLUDED_PATHS = arrayOf("/login", "/", "/static/**", "/index.html", "/favicon.ico")
    val SWAGGER_PATHS = arrayOf( // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",  // -- Swagger UI v3 (OpenAPI)
        "/v3/api-docs/**",
        "/swagger-ui/**" // other public endpoints of your API may be appended to this array
    )

    val ALL_PATHS = EXCLUDED_PATHS + SWAGGER_PATHS
}