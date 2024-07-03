package pt.isel.sitediary.ui.main.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.Loaded
import pt.isel.sitediary.domain.WorkListAndProfile
import pt.isel.sitediary.ui.common.LoadingScreen

@Composable
fun ProfileView(
    profile: LoadState<WorkListAndProfile>, onLogoutRequest: () -> Unit, innerPadding: PaddingValues
) {
    profile.let {
        if (it is Loaded && it.value.isSuccess) {
            it.value.getOrNull()?.let { values ->
                ProfileScreen(values.profile, innerPadding, onLogoutRequest)
            }
        } else {
            LoadingScreen()
        }
    }
}
