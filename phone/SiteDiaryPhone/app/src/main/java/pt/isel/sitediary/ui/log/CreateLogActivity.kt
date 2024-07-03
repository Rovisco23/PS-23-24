package pt.isel.sitediary.ui.log

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.isel.sitediary.SiteDiaryApplication
import pt.isel.sitediary.domain.Loaded
import pt.isel.sitediary.domain.idle
import pt.isel.sitediary.ui.common.ErrorAlert
import pt.isel.sitediary.ui.login.LoginActivity
import pt.isel.sitediary.ui.viewmodel.LogViewModel
import java.util.UUID

class CreateLogActivity : ComponentActivity() {

    private val app by lazy { application as SiteDiaryApplication }
    private val viewModel by viewModels<LogViewModel>(
        factoryProducer = {
            LogViewModel.factory(
                app.logService,
                app.loggedUserRepository
            )
        }
    )

    companion object {
        fun navigateTo(ctx: Context, workId: UUID?) {
            if (workId == null) {
                return
            }
            ctx.startActivity(createIntent(ctx, workId))
        }

        private fun createIntent(ctx: Context, workId: UUID): Intent {
            val intent = Intent(ctx, CreateLogActivity::class.java)
            intent.putExtra("workId", workId.toString())
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(ContentValues.TAG, "CreateLogActivity.onCreate() called")
        val workId = intent.getStringExtra("workId")
        if (workId == null) finish()
        else {
            lifecycleScope.launch {
                viewModel.creation.collect {
                    if (it is Loaded && it.value.isSuccess) finish()
                }
            }
            setContent {
                val creation by viewModel.creation.collectAsState(initial = idle())
                CreateLogView(
                    creation = creation,
                    onCreateClicked = { input ->
                        viewModel.createLog(input, workId)
                    },
                    onBackRequested = { finish() }
                )
                creation.let {
                    if (it is Loaded && it.value.isFailure) {
                        val msg = it.value.exceptionOrNull()?.message ?: "Something went wrong"
                        ErrorAlert(
                            title = "Ups!",
                            message = msg,
                            buttonText = "Ok",
                            onDismiss = {
                                viewModel.resetToIdle()
                                if (msg == "Login Required") {
                                    viewModel.clearUser()
                                    LoginActivity.navigateTo(this)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}