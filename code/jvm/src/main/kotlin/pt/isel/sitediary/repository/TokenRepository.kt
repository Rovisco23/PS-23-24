package pt.isel.sitediary.repository

import kotlinx.datetime.Instant
import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenValidationInfo
import pt.isel.sitediary.model.SessionModel

interface TokenRepository {
    fun createToken(token: Token, maxTokens: Int)
    fun updateLastUsedToken(token: Token, now: Instant)
    fun checkSession(userId: Int, token: String): SessionModel?
    fun deleteToken(token: TokenValidationInfo) : Boolean
}