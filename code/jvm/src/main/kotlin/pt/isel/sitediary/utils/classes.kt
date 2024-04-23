package pt.isel.sitediary.utils

import java.security.MessageDigest
import java.util.*
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import java.security.SecureRandom

data class Work(
    val id: UUID,
    val name: String,
    val description: String,
    val address: Address,
    val members: List<Member>
)

data class Address(
    val location: Location,
    val street: String,
    val postalCode: String
)

data class Location(
    val district: String,
    val county: String,
    val parish: String,
)

data class Member(
    val id: Int,
    val name: String,
    val role: String,
    val phone: String
)

data class User(
    val id: Int,
    // profile_picture
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
    val location: Location
)

data class AuthenticatedUser(
    val user: User,
    val token: String
)

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)

data class TokenExternalInfo(
        val tokenValue: String,
        val userId: Int,
        val username: String,
        val tokenExpiration: Instant
)

interface TokenEncoder {
        fun createValidationInformation(token: String): TokenValidationInfo
}

data class TokenValidationInfo(
        val validationInfo: String
)

class Sha256TokenEncoder : TokenEncoder {

        override fun createValidationInformation(token: String): TokenValidationInfo =
                TokenValidationInfo(hash(token))

        private fun hash(input: String): String {
                val messageDigest = MessageDigest.getInstance("SHA256")
                return Base64.getUrlEncoder().encodeToString(
                        messageDigest.digest(
                                Charsets.UTF_8.encode(input).array()
                        )
                )
        }
}

data class UsersDomainConfig(
        val tokenSizeInBytes: Int,
        val tokenTtl: Duration, // Ttl = Time to live
        val tokenRollingTtl: Duration,
        val maxTokensPerUser: Int
) {
        init {
                require(tokenSizeInBytes > 0)
                require(tokenTtl.isPositive())
                require(tokenRollingTtl.isPositive())
                require(maxTokensPerUser > 0)
        }
}

@Component
class UsersDomain(
        //private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig
) {

        fun generateTokenValue(): String =
                ByteArray(config.tokenSizeInBytes).let { byteArray ->
                        SecureRandom.getInstanceStrong().nextBytes(byteArray)
                        Base64.getUrlEncoder().encodeToString(byteArray)
                }

        fun canBeToken(token: String): Boolean = try {
                Base64.getUrlDecoder()
                        .decode(token).size == config.tokenSizeInBytes
        } catch (ex: IllegalArgumentException) {
                false
        }

        fun isTokenTimeValid(
                clock: Clock,
                token: Token
        ): Boolean {
                val now = clock.now()
                return token.createdAt <= now &&
                        (now - token.createdAt) <= config.tokenTtl &&
                        (now - token.lastUsedAt) <= config.tokenRollingTtl
        }

        fun getTokenExpiration(token: Token): Instant {
                val absoluteExpiration = token.createdAt + config.tokenTtl
                val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
                return if (absoluteExpiration < rollingExpiration) {
                        absoluteExpiration
                } else {
                        rollingExpiration
                }
        }

        fun createTokenValidationInformation(token: String): TokenValidationInfo =
                tokenEncoder.createValidationInformation(token)

        fun isSafePassword(password: String) =
                password.length > 8 && password.any { !it.isLetterOrDigit() } &&
                        password.any { it.isDigit() } && password.any { it.isUpperCase() }

        val maxNumberOfTokensPerUser = config.maxTokensPerUser
}