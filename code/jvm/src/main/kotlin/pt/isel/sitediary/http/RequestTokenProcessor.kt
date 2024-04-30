package pt.isel.sitediary.http

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import jakarta.servlet.http.Cookie
import pt.isel.sitediary.service.UserService

@Component
class RequestTokenProcessor(
    val usersService: UserService
) {
    private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
    fun processAuthorizationCookieValue(tokenCookie: Cookie?): AuthenticatedUser? {
        if (tokenCookie == null) {
            return null
        }
        val tokenValue = tokenCookie.value
        if (tokenValue == "") {
            return null
        }
        return usersService.getUserByToken(tokenValue)?.let {
            logger.info("User found $it")
            AuthenticatedUser(
                it,
                tokenValue
            )
        }
    }
    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return usersService.getUserByToken(parts[1])?.let {
            logger.info("User found $it")
            AuthenticatedUser(
                it,
                parts[1]
            )
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}