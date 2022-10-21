package com.example.reactive.configs.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Slf4j
@Component
class JwtTokenProvider @Autowired constructor(
    @Value("\${spring.jwt.secret}") private val secret: String,
    @Value("\${spring.jwt.expiration.access}") private val expirationAccessTime: String?,
    @Value("\${spring.jwt.expiration.refresh}") private val expirationRefreshTime: String?
) {
    private val FIFTEEN_MIN: Long = 1000 * 60 * 15
    private val FOUR_HOURS: Long = 1000 * 60 * 60 * 4

    private val algorithm by lazy { Algorithm.HMAC256(secret) }
    private fun verifier(token: String) = JWT.require(algorithm).build().verify(token)
    
    fun getRoles(token: String): List<String> = verifier(token).getClaim("roles").asList(String::class.java)
    fun getSimpleAuthorities(token: String) = getRoles(token).map { SimpleGrantedAuthority(it) }
    fun getAuthorities(token: String): List<GrantedAuthority> = AuthorityUtils.createAuthorityList(*getRoles(token).toTypedArray())
    fun getLoginId(token: String): String = verifier(token).subject
    fun getExpireDate(token: String): Date = verifier(token).expiresAt

    fun validateToken(token: String): Boolean = try {
        getExpireDate(token).after(Date())
    }catch (e: Exception){
        e.printStackTrace()
        false
    }

    fun getAuthentication(token: String): Authentication {
        return UsernamePasswordAuthenticationToken(getLoginId(token), token, getAuthorities(token))
    }


    fun generateToken(userDetails: UserDetails) =
        generateToken(userDetails.username, userDetails.authorities.map { it.authority })

    fun generateToken(loginId: String, rolesByComma: String) =
        generateToken(loginId, AuthorityUtils.commaSeparatedStringToAuthorityList(rolesByComma).map { it.authority })

    fun generateToken(authentication: Authentication): String {
        val loginId = authentication.name
        val roles = authentication.authorities.map{ it.authority }
        return generateToken(loginId, roles)
    }
    fun generateToken(loginId: String, roles: List<String>): String{
        return generateToken(loginId, expirationAccessTime?.toLong()?.let { it * 1000 } ?: FOUR_HOURS, roles.toTypedArray())
    }

    fun generateToken(loginId: String, expirationInMillis: Long, roles: Array<String>): String {
        val systemTimeMillis = System.currentTimeMillis()
        return JWT.create()
            .withSubject(loginId)
            .withIssuedAt(Date(systemTimeMillis))
            .withExpiresAt(Date(systemTimeMillis + expirationInMillis))
            .withArrayClaim("roles", roles)
            .sign(algorithm)
    }

    fun generateAccessToken(loginId: String,  roles: Array<String>): String = generateToken(loginId, expirationAccessTime?.toLong()?.let { it * 1000 } ?: FOUR_HOURS, roles)
    fun generateRefreshToken(loginId: String,  roles: Array<String>): String = generateToken(loginId, expirationRefreshTime?.toLong()?.let { it * 1000 } ?: FIFTEEN_MIN, roles)
}