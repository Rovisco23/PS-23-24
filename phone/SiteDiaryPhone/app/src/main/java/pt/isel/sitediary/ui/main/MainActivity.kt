package pt.isel.sitediary.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pt.isel.sitediary.SiteDiaryApplication
import pt.isel.sitediary.domain.Loaded
import pt.isel.sitediary.domain.idle
import pt.isel.sitediary.ui.common.ErrorAlert
import pt.isel.sitediary.ui.login.LoginActivity
import pt.isel.sitediary.ui.viewmodel.MainViewModel
import pt.isel.sitediary.ui.work.WorkDetailsActivity


class MainActivity : ComponentActivity() {

    private val app by lazy { application as SiteDiaryApplication }
    private val viewModel by viewModels<MainViewModel>(
        factoryProducer = {
            MainViewModel.factory(app.workService, app.userService, app.loggedUserRepository)
        }
    )

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, MainActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(ContentValues.TAG, "MainActivity.onCreate() called")
        viewModel.getAllValues()
        setContent {
            val mainValues by viewModel.mainValues.collectAsState(initial = idle())
            MainView(
                mainValues = mainValues,
                onRefresh = {
                    viewModel.refresh()
                },
                onWorkSelected = { workId ->
                    Log.v(ContentValues.TAG, "MainActivity.onWorkSelected() workId: $workId")
                    WorkDetailsActivity.navigateTo(this, workId)
                },
                onLogoutRequest = {
                    viewModel.logout()
                    LoginActivity.navigateTo(this)
                }
            )
            mainValues.let { values ->
                if (values is Loaded && values.value.isFailure) {
                    val msg = values.value.exceptionOrNull()?.message ?: "Something went wrong"
                    ErrorAlert(
                        title = "Ups!",
                        message = msg,
                        buttonText = "Ok",
                        onDismiss = {
                            viewModel.resetToIdle()
                            if (msg == "Login Required") {
                                viewModel.logout()
                                LoginActivity.navigateTo(this)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(ContentValues.TAG, "MainActivity.onStart() called")
        viewModel.getAllValues()
    }

    override fun onStop() {
        super.onStop()
        Log.v(ContentValues.TAG, "MainActivity.onStop() called")
        viewModel.resetToIdle()
    }
}

