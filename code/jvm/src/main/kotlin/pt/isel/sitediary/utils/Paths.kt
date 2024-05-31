package pt.isel.sitediary.utils

object Paths {

    const val PREFIX = "/api"

    object User {
        const val SIGN_UP = "$PREFIX/signup"
        const val LOGIN = "$PREFIX/login"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_USER_ID = "$PREFIX/users/{id}"
        const val GET_USER_USERNAME = "$PREFIX/users"
        const val SESSION = "$PREFIX/session"
        const val PROFILE_PICTURE = "$PREFIX/profile-picture"
        const val PENDING = "$PREFIX/pending/{id}"
    }

    object Work {
        const val GET_BY_ID = "$PREFIX/work/{id}"
        const val GET_ALL_WORKS = "$PREFIX/work"
        const val GET_OPENING_TERM = "$PREFIX/opening-term/{id}"
        const val GET_INVITE_LIST = "$PREFIX/invite"
        const val GET_INVITE = "$PREFIX/invite/{id}"
        const val FINISH_WORK = "$PREFIX/finish-work"
        const val GET_IMAGE = "$PREFIX/work-image/{id}"
    }

    object Log {
        const val GET_BY_ID = "$PREFIX/logs/{id}"
        const val GET_ALL_LOGS = "$PREFIX/logs"
        const val GET_LOG_FILES = "$PREFIX/logs-files"
        const val EDIT_LOG = "$PREFIX/logs/{id}"
    }

}