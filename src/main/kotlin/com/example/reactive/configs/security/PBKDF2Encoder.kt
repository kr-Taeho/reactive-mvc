package com.example.reactive.configs.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm
import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


@Component
class PBKDF2Encoder(
    @Value("\${spring.password.encoder.secret}") private val secret: String,
    @Value("\${spring.password.encoder.iteration}") private val iteration: Int,
    @Value("\${spring.password.encoder.keylength}") private val keylength: Int
) : PasswordEncoder {

    override fun encode(cs: CharSequence): String = try {
        val result = SecretKeyFactory
            .getInstance(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512.toString())
            .generateSecret(PBEKeySpec(cs.toString().toCharArray(), secret.toByteArray(), iteration, keylength))
            .encoded
        Base64.getEncoder().encodeToString(result)
    } catch (ex: NoSuchAlgorithmException) {
        throw RuntimeException(ex)
    } catch (ex: InvalidKeySpecException) {
        throw RuntimeException(ex)
    }

    override fun matches(cs: CharSequence, string: String): Boolean = encode(cs) == string
}