package com.example.reactive.models.dto

import com.example.reactive.configs.security.Role
import com.example.reactive.models.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import kotlin.streams.toList

@ToString
@NoArgsConstructor
@AllArgsConstructor
data class AuthUser(
    private val serialVersionUID: Long = 1L,
    private var username: String,
    @JsonProperty
    private var password: String,
    private var enabled: Boolean,
    private var roles: List<Role>
) : UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> =
        this.roles.stream().map { authority -> SimpleGrantedAuthority(authority.name) }.toList()

    @JsonIgnore
    override fun getPassword(): String = password

    override fun getUsername(): String =username

    override fun isAccountNonExpired(): Boolean = false

    override fun isAccountNonLocked(): Boolean = false

    override fun isCredentialsNonExpired(): Boolean = false

    override fun isEnabled(): Boolean = enabled

    fun getRoles(): List<Role> = roles

    companion object {
        fun convert(user: User): AuthUser =
            AuthUser(user.cid, user.loginId, user.loginPw, user.enabled, user.role.split(",").map {  Role.valueOf(it) }.toList())
    }
}