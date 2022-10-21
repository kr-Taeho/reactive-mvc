package com.example.reactive.configs.security.v1.rolebased

import com.example.reactive.repositories.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomerReactiveUserDetailsService(private val customerRepository: UserRepository) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> = mono {
        val customer = customerRepository.findUserByLoginId(username) ?: throw BadCredentialsException("Invalid Credentials")
        return@mono User(customer.loginId, customer.loginPw, AuthorityUtils.commaSeparatedStringToAuthorityList(customer.role))
    }
}