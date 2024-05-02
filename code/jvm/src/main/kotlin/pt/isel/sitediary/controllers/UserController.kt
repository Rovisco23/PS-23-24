package pt.isel.sitediary.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import pt.isel.sitediary.model.EditProfileInputModel
import pt.isel.sitediary.model.LoginInputModel
import pt.isel.sitediary.model.SignUpInputModel
import pt.isel.sitediary.model.TokenOutputModel
import pt.isel.sitediary.service.UserService
import pt.isel.sitediary.utils.Paths
import pt.isel.sitediary.utils.handleResponse

@RestController
class UserController (private val service: UserService) {

    @PostMapping(Paths.User.SIGN_UP)
    fun createUser(@RequestBody user: SignUpInputModel): ResponseEntity<*> {
        val res = service.createUser(
            user.email,
            user.role,
            user.username,
            user.password,
            user.firstName,
            user.lastName,
            user.phone,
            user.parish,
            user.county
        )
        return handleResponse(res){
            ResponseEntity.status(201).body(it)
        }
    }

    @PostMapping(Paths.User.LOGIN)
    @Operation(summary = "Login", description = "Endpoint to login user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful login"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun login(@RequestBody u: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val res = service.login(
            u.user,
            u.password
        )
        return handleResponse(res){
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

            val cookieId =  ResponseCookie
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



    @PostMapping(Paths.User.LOGOUT)
    fun logout(@RequestBody token: String, response: HttpServletResponse): ResponseEntity<*> {
        val res = service.logout(token)
        return handleResponse(res){
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
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.User.GET_USER_ID)
    fun getUserById(@PathVariable id: Int): ResponseEntity<*> {
        val res = service.getUserById(id)
        return handleResponse(res){
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.User.GET_USER_USERNAME)
    fun getUserByUsername(@RequestParam username: String): ResponseEntity<*> {
        val res = service.getUserByUsername(username)
        return handleResponse(res){
            ResponseEntity.status(200).body(it)
        }
    }

    /*@PutMapping(Paths.User.GET_USER_ID)
    fun editProfile(@RequestBody u: EditProfileInputModel, user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.editProfile(
            userId = user.user.id,
            //user.img,
            username = u.username,
            firstName = u.firstName,
            lastName = u.lastName,
            phone = u.phone,
            parish = u.parish,
            county = u.county
        )
        return handleResponse(res){
            ResponseEntity.status(200).body(it)
        }
    }*/

}