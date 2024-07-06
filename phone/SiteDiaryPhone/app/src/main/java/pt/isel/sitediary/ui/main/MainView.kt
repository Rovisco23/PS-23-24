package pt.isel.sitediary.ui.main


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.WorkListAndProfile
import pt.isel.sitediary.ui.common.nav.BottomNavItem.Main.Logs
import pt.isel.sitediary.ui.common.nav.BottomNavItem.Main.Home
import pt.isel.sitediary.ui.common.nav.BottomNavItem.Main.UserProfile
import pt.isel.sitediary.ui.common.nav.BottomNavigationBar
import pt.isel.sitediary.ui.common.nav.DefaultTopBar
import pt.isel.sitediary.ui.main.profile.ProfileView
import pt.isel.sitediary.ui.main.workList.WorkListView
import pt.isel.sitediary.ui.theme.SiteDiaryPhoneTheme
import java.util.UUID

@Composable
fun MainView(
    mainValues: LoadState<WorkListAndProfile>,
    onRefresh: () -> Unit = { },
    onWorkSelected: (workId: UUID) -> Unit = { },
    onLogoutRequest: () -> Unit = { }
) {
    SiteDiaryPhoneTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { DefaultTopBar() },
            bottomBar = { BottomNavigationBar(navController, listOf(Home, Logs, UserProfile)) }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = Home.route) {
                composable(Home.route) {
                    WorkListView(mainValues, onRefresh, onWorkSelected, innerPadding)
                }
                composable(Logs.route) {
                    WorkListView(mainValues, onRefresh, onWorkSelected, innerPadding)
                }
                composable(UserProfile.route) {
                    ProfileView(mainValues, onLogoutRequest, innerPadding)
                }
            }
        }
    }
}
