package pt.isel.sitediary.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.tags.Tag
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import pt.isel.sitediary.model.*
import pt.isel.sitediary.service.UserService
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Paths
import pt.isel.sitediary.utils.handleResponse

@RestController
@Tag(name = "User", description = "Operations related the User.")
class UserController(private val service: UserService) {

    @PostMapping(Paths.User.SIGN_UP)
    @Operation(summary = "Sign Up", description = "Used to create a user for the application.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "Successful signup"
            ),
            ApiResponse(
                responseCode = "400", description = "Invalid parameters",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun createUser(@RequestBody user: SignUpInputModel, response: HttpServletResponse) {//: ResponseEntity<*> {
        service.createUser(user)
        response.addHeader(HttpHeaders.LOCATION, "/login")
        response.status = 201
    }

    @PostMapping(Paths.User.LOGIN)
    @Operation(summary = "Login", description = "Endpoint to login user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful login",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = TokenOutputModel::class))
                ]
            ),
            ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun login(@RequestBody u: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val res = service.login(
            u.user,
            u.password
        )
        return handleResponse(res) {
            val tokenValue = TokenOutputModel(
                userId = it.userId,
                username = it.username,
                token = it.tokenValue
            )
            val cookieToken = ResponseCookie
                .from("token", tokenValue.token)
                .path("/")
                .maxAge(it.tokenExpiration.epochSeconds)
                .httpOnly(true)
                .secure(false)
                .build()

            val cookieId = ResponseCookie
                .from("id", tokenValue.userId.toString())
                .path("/")
                .maxAge(it.tokenExpiration.epochSeconds)
                .httpOnly(true)
                .secure(false)
                .build()

            response.addHeader(HttpHeaders.SET_COOKIE, cookieToken.toString())
            response.addHeader(HttpHeaders.SET_COOKIE, cookieId.toString())
            ResponseEntity.status(200).body(tokenValue)
        }
    }

    @GetMapping(Paths.User.SESSION)
    fun checkSession(@RequestBody u: SessionInputModel): ResponseEntity<*> {
        val res = service.checkSession(u.userId, u.token)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }


    @PostMapping(Paths.User.LOGOUT)
    @Operation(summary = "Logout", description = "Used to logout user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful logout",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = TokenModel::class))
                ]
            ),
            ApiResponse(
                responseCode = "401", description = "User not logged in",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun logout(@RequestBody token: TokenModel, response: HttpServletResponse): ResponseEntity<*> {
        val res = service.logout(token.token)
        return handleResponse(res) {
            val cookieToken = Cookie("token", null)
            cookieToken.path = "/"
            cookieToken.maxAge = 0
            cookieToken.isHttpOnly = true
            cookieToken.secure = false

            val cookieId = Cookie("id", null)
            cookieId.path = "/"
            cookieId.maxAge = 0
            cookieId.isHttpOnly = true
            cookieId.secure = false

            response.addCookie(cookieId)
            response.addCookie(cookieToken)
            ResponseEntity.status(200).body(TokenModel(it))
        }
    }

    @GetMapping(Paths.User.GET_USER_ID)
    @Operation(summary = "Get profile", description = "Get user profile from user id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful profile retrieval",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = GetUserModel::class))
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "User does not exist",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun getUserById(@PathVariable id: Int): ResponseEntity<*> {
        val res = service.getUserById(id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.User.GET_USER_USERNAME)
    @Operation(summary = "Get profile", description = "Get user profile using username")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful profile retrieval",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = GetUserModel::class))
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "User does not exist",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun getUserByUsername(@RequestParam username: String): ResponseEntity<*> {
        val res = service.getUserByUsername(username)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @PutMapping(Paths.User.GET_USER_ID)
    @Operation(summary = "Edit profile", description = "Edit profile of user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "202", description = "Edition of profile accepted",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = GetUserModel::class))
                ]
            ),
            ApiResponse(
                responseCode = "400", description = "Invalid parameters",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "User does not exist",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun editProfile(@RequestBody u: EditProfileInputModel, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.editProfile(userId = user.user.id, editUser = u)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

}


fun main() {
    val secret = "your_secret_key"
    val algorithm = Algorithm.HMAC256(secret)
    val map = mapOf("id" to 1, "username" to "JMota", "role" to "admin", "token" to "pCRJ4OWSJarFvFV943aebPScJ5u5Y27KWuBbbLbx1mg=")
    val jwt = JWT.create().withPayload(map).sign(algorithm)
    println("JWT = $jwt")

    // FRONT-END
    val verifier = JWT.require(algorithm).build()
    val decodedJWT = verifier.verify(jwt)

    val id = decodedJWT.getClaim("id").asInt()
    val username = decodedJWT.getClaim("username").asString()
    val role = decodedJWT.getClaim("role").asString()
    val token = decodedJWT.getClaim("token").asString()

    println("Decoded Info:")
    println("id = $id")
    println("username = $username")
    println("role = $role")
    println("token = $token")
}