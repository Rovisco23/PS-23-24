package pt.isel.sitediary.ui.work

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
import pt.isel.sitediary.SiteDiaryApplication
import pt.isel.sitediary.domain.Loaded
import pt.isel.sitediary.domain.idle
import pt.isel.sitediary.ui.common.ErrorAlert
import pt.isel.sitediary.ui.log.CreateLogActivity
import pt.isel.sitediary.ui.login.LoginActivity
import pt.isel.sitediary.ui.viewmodel.WorkViewModel
import java.util.UUID

class WorkDetailsActivity : ComponentActivity() {

    private val app by lazy { application as SiteDiaryApplication }
    private val viewModel by viewModels<WorkViewModel>(
        factoryProducer = {
            WorkViewModel.factory(
                app.workService,
                app.logService,
                app.userService,
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
            val intent = Intent(ctx, WorkDetailsActivity::class.java)
            intent.putExtra("workId", workId.toString())
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(ContentValues.TAG, "WorkDetailsActivity.onCreate() called")
        val workId = intent.getStringExtra("workId")
        if (workId == null) finish()
        else {
            viewModel.getWorkDetails(workId)
            setContent {
                val work by viewModel.work.collectAsState(initial = idle())
                WorkDetailsView(
                    workDetails = work,
                    onBackRequested = { finish() },
                    onProfileBackRequested = {
                        viewModel.clearSelectedMember()
                    },
                    onCreationRequested = {
                        viewModel.resetToIdle()
                        CreateLogActivity.navigateTo(this, UUID.fromString(workId))
                    },
                    onLogSelected = { logId ->
                        Log.v(ContentValues.TAG, "Log selected: $logId")
                        viewModel.getLog(logId)
                    },
                    onLogBackRequested = {
                        viewModel.clearSelectedLog()
                    },
                    onMemberSelected = { memberId ->
                        Log.v(ContentValues.TAG, "Member selected: $memberId")
                        viewModel.getMemberProfile(memberId)
                    }
                )
                work.let {
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

    override fun onStart() {
        super.onStart()
        Log.v(ContentValues.TAG, "WorkDetailsActivity.onStart() called")
        val workId = intent.getStringExtra("workId")
        if (workId == null) finish()
        else viewModel.getWorkDetails(workId)
    }
}
