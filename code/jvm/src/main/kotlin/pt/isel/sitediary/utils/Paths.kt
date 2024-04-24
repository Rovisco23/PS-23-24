package pt.isel.sitediary.utils

object Paths {

    const val PREFIX = "/api"

    object User {
        const val SIGN_UP = "$PREFIX/signup"
        const val LOGIN = "$PREFIX/login"
        const val GET_USER_ID = "$PREFIX/users/{id}"
        const val GET_USER_USERNAME = "$PREFIX/users"
    }

}