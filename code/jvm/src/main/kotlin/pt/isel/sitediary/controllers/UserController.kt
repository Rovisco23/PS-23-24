package pt.isel.sitediary.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
}