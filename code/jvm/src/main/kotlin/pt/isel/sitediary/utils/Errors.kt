package pt.isel.sitediary.utils

import org.springframework.http.ResponseEntity

class Errors(val status: Int, val reason: String) {
    companion object {
        fun response(error: Errors) = ResponseEntity
            .status(error.status)
            .header("Content-Type", "application/problem+json")
            .body<Any>(error.reason)

        val userNotFound = Errors(404, "User does not exist.")

        val emailAlreadyInUse = Errors(400, "That email is already in use.")

        val cellOccupied = Errors(400, "That cell already has a piece.")

        val wrongTurn = Errors(400, "It's not your turn.")

        val invalidParameter = Errors(400, "Invalid Parameter.")

        val invalidPhoneNumber = Errors(400, "Invalid Phone number.")

        val invalidRole = Errors(400, "Invalid Role.")

        val invalidLocation = Errors(400, "Invalid Location.")

        val internalError = Errors(500, "Internal Server Error. Please try again later.")

        val invalidLoginParamCombination = Errors(400, "User or password are invalid.")

        val invalidPassword = Errors(
            400,
            "Invalid Password.\nPassword must have at least 8 digits, one uppercase letter, one number and a symbol."
        )

        val usernameAlreadyInUse = Errors(400, "That username is already in use.")
    }
}