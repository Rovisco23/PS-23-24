package pt.isel.sitediary.utils

object Paths {

    const val PREFIX = "/api"

    object User {
        const val SIGN_UP = "$PREFIX/signup"
        const val LOGIN = "$PREFIX/login"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_USER_ID = "$PREFIX/users/{id}"
        const val GET_USER_USERNAME = "$PREFIX/users"
    }

    object Work {
        const val GET_BY_ID = "$PREFIX/work/{id}"
        const val GET_ALL_WORKS = "$PREFIX/work"
    }

}