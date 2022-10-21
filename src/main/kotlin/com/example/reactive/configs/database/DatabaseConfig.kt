package com.example.reactive.configs.database

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import io.r2dbc.pool.PoolingConnectionFactoryProvider.*
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import io.r2dbc.spi.Option
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import java.time.Duration

@Slf4j
@Configuration
@EnableR2dbcRepositories
class DatabaseConfig(
    @Value("\${spring.r2dbc.url}") val url: String,
    @Value("\${spring.r2dbc.ssl}") val ssl: Boolean,
    @Value("\${spring.r2dbc.database}")val database: String,
    @Value("\${spring.r2dbc.username}") val username: String,
    @Value("\${spring.r2dbc.password}") val password: String,
    @Value("\${spring.r2dbc.port}") val port: Int,
    @Value("\${spring.r2dbc.charset}") val charset: String,
    @Value("\${spring.r2dbc.pool.max-size}") val poolMaxSize: Int,
    @Value("\${spring.r2dbc.pool.initial-size}") val initialSize: Int,
    @Value("\${spring.r2dbc.pool.max-idle-time}") val maxIdleTime: Long,
    @Value("\${spring.r2dbc.pool.max-life}") val maxLife: Long,
    @Value("\${spring.r2dbc.pool.max-create-connection-time}") val poolMaxCreateConnectionTime: Long,
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(builder()
            .option(SSL, ssl)
            .option(DRIVER, "pool")
            .option(PROTOCOL, "mariadb")
            .option(HOST, url)
            .option(DATABASE, database)
            .option(PORT, port)
            .option(USER, username)
            .option(PASSWORD, password)
            .option(Option.valueOf("charset"), charset)
            .option(MAX_SIZE, poolMaxSize)
            .option(INITIAL_SIZE, initialSize)
            .option(MAX_IDLE_TIME, Duration.ofSeconds(maxIdleTime))
            .option(MAX_CREATE_CONNECTION_TIME, Duration.ofSeconds(poolMaxCreateConnectionTime))
            .option(MAX_LIFE_TIME, Duration.ofMinutes(maxLife))
            .build())
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        val populator = CompositeDatabasePopulator()
//        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("init/schema.sql")))
//        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("init/data.sql")))
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}