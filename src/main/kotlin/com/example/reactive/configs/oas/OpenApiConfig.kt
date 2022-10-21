package com.example.reactive.configs.oas

import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun userApi(@Value("\${springdoc.version}") appVersion:String ): GroupedOpenApi{
        val paths = arrayOf("/user/**", "/users/**" )
        return GroupedOpenApi.builder()
            .group("user")
            .addOpenApiCustomiser { openApi -> openApi.info(Info().title("User API").version(appVersion)) }
            .pathsToMatch(*paths)
            .build()
    }


    @Bean
    fun todoApi(@Value("\${springdoc.version}") appVersion:String ): GroupedOpenApi{
        val paths = arrayOf("/todo/**", "/todos/**" )
        return GroupedOpenApi.builder()
            .group("todo")
            .addOpenApiCustomiser { openApi -> openApi.info(Info().title("Todo API").version(appVersion)) }
            .pathsToMatch(*paths)
            .build()
    }


}