package pt.isel.sitediary.utils

import org.springframework.http.ResponseEntity

class Errors(val status: Int, val reason: String) {
    companion object {
        fun response(error: Errors) = ResponseEntity
            .status(error.status)
            .header("Content-Type", "application/problem+json")
            .body<Any>(error.reason)

        val invalidTechnicians = Errors(400, "Para iniciar uma Obra é necessário ter pelo menos um " +
                "Fiscal e um Coordenador de Obra.")
        val logNotEditable = Errors(400, "Esta observação não pode ser editada.")
        val workFinished = Errors(400, "Não é possível registar uma observação numa obra terminada.")
        val notTechnician = Errors(401, "Não pode criar uma observação se não for um Técnico.")
        val membersMissing = Errors(400, "É necessário ter pelo menos um Fiscal e um Coordenador de Obra.")

        val workAlreadyFinished = Errors(400, "Work is already finished.")

        val forbidden = Errors(403, "Forbidden")

        val userNotFound = Errors(404, "User does not exist.")

        val emailAlreadyInUse = Errors(400, "That email is already in use.")

        val invalidParameter = Errors(400, "Invalid Parameter.")

        val invalidPhoneNumber = Errors(400, "Invalid Phone number.")

        val invalidRole = Errors(400, "Invalid Role.")

        val invalidLocation = Errors(400, "Invalid Location.")

        val internalError = Errors(500, "Internal Server Error. Please try again later.")

        val invalidLoginParamCombination = Errors(400, "User or password are invalid.")

        val noUserLoggedIn = Errors(401, "No user is logged in.")

        val invalidPassword = Errors(
            400,
            "Invalid Password.\nPassword must have at least 8 digits, one uppercase letter, one number and a symbol."
        )

        val usernameAlreadyInUse = Errors(400, "That username is already in use.")

        val workNotFound = Errors(404, "Work does not exist.")

        val notMember = Errors(401, "You are not a member of this Work")

        val notAdmin = Errors(401, "You are not an Admin of this Work")

        val logNotFound = Errors(404, "Log does not exist.")

        val inviteNotFound = Errors(404, "Invite does not exist.")

        val notInviteOwner = Errors(403, "You are not the owner of this Invite")
    }
}