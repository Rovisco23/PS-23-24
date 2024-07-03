package pt.isel.sitediary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isel.sitediary.domain.Idle
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.Profile
import pt.isel.sitediary.domain.WorkListAndProfile
import pt.isel.sitediary.domain.getOrNull
import pt.isel.sitediary.domain.idle
import pt.isel.sitediary.domain.loaded
import pt.isel.sitediary.domain.loading
import pt.isel.sitediary.infrastructure.LoggedUserRepository
import pt.isel.sitediary.services.UserService
import pt.isel.sitediary.services.WorkService
import pt.isel.sitediary.ui.common.GetMainActivityValuesException

class MainViewModel(
    private val workService: WorkService,
    private val userService: UserService,
    private val repo: LoggedUserRepository
) : ViewModel() {

    companion object {
        fun factory(
            workService: WorkService,
            userService: UserService,
            repo: LoggedUserRepository
        ) = viewModelFactory {
            initializer { MainViewModel(workService, userService, repo) }
        }
    }

    private val _mainValuesFlow: MutableStateFlow<LoadState<WorkListAndProfile>> =
        MutableStateFlow(idle())

    val mainValues: Flow<LoadState<WorkListAndProfile>>
        get() = _mainValuesFlow.asStateFlow()

    fun getAllValues() {
        if (_mainValuesFlow.value is Idle) {
            _mainValuesFlow.value = loading()
            viewModelScope.launch {
                try {
                    val loggedUser =
                        repo.getUserInfo() ?: throw GetMainActivityValuesException("Login Required")
                    val workList = workService.getAllWork(loggedUser.token)
                    val user = userService.getProfile(loggedUser.userId, loggedUser.token)
                    val profilePicture =
                        userService.getProfilePicture(loggedUser.userId, loggedUser.token)
                    val rsp = WorkListAndProfile(workList, Profile(user, profilePicture))
                    _mainValuesFlow.value = loaded(Result.success(rsp))
                } catch (e: GetMainActivityValuesException) {
                    val msg = e.message ?: "Something went wrong"
                    _mainValuesFlow.value =
                        loaded(Result.failure(GetMainActivityValuesException(msg, e)))
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val oldValues = _mainValuesFlow.value.getOrNull()?.profile
                    ?: throw GetMainActivityValuesException("Something went wrong")
                _mainValuesFlow.value = loading()
                val loggedUser =
                    repo.getUserInfo() ?: throw GetMainActivityValuesException("Login Required")
                val workList = workService.getAllWork(loggedUser.token)
                val rsp = WorkListAndProfile(workList, oldValues)
                _mainValuesFlow.value = loaded(Result.success(rsp))
            } catch (e: GetMainActivityValuesException) {
                val msg = e.message ?: "Something went wrong"
                _mainValuesFlow.value =
                    loaded(Result.failure(GetMainActivityValuesException(msg, e)))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.clearUserInfo()
        }
    }

    fun resetToIdle() {
        if (_mainValuesFlow.value !is Idle) {
            _mainValuesFlow.value = idle()
        }
    }
}