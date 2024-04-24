package pt.isel.sitediary.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.sitediary.model.LoginInputModel
import pt.isel.sitediary.model.SignUpInputModel
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
            user.parish
        )
        return handleResponse(res){
            ResponseEntity.status(201).body(it)
        }
    }

    @PostMapping(Paths.User.LOGIN)
    fun login(@RequestBody u: LoginInputModel): ResponseEntity<*> {
        val res = service.login(
            u.user,
            u.password
        )
        return handleResponse(res){
            ResponseEntity.status(201).body(it)
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

}