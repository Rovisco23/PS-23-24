package pt.isel.sitediary.repository

import kotlinx.datetime.Instant
import pt.isel.sitediary.utils.Token
import pt.isel.sitediary.utils.TokenValidationInfo

interface TokenRepository {
    fun createToken(token: Token, maxTokens: Int)
    fun updateLastUsedToken(token: Token, now: Instant)
    fun deleteToken(token: TokenValidationInfo) : Boolean
}