package pt.isel.sitediary.ui.work

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.Work
import pt.isel.sitediary.ui.common.nav.BottomNavItem
import pt.isel.sitediary.ui.common.nav.BottomNavigationBar
import pt.isel.sitediary.ui.common.nav.DefaultTopBar
import pt.isel.sitediary.ui.theme.SiteDiaryPhoneTheme
import pt.isel.sitediary.ui.work.details.DetailsView
import pt.isel.sitediary.ui.work.logs.LogsView
import pt.isel.sitediary.ui.work.members.MembersView

@Composable
fun WorkDetailsView(
    workDetails: LoadState<Work>,
    onBackRequested: () -> Unit = {},
    onProfileBackRequested: () -> Unit = {},
    onCreationRequested: () -> Unit = {},
    onLogSelected: (Int) -> Unit = {},
    onLogBackRequested: () -> Unit = {},
    onMemberSelected: (Int) -> Unit = {}
) {
    SiteDiaryPhoneTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                DefaultTopBar()
            },
            bottomBar = {
                BottomNavigationBar(
                    navController,
                    listOf(
                        BottomNavItem.Work.Details,
                        BottomNavItem.Work.Logs,
                        BottomNavItem.Work.Members
                    )
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Work.Logs.route
            ) {
                composable(BottomNavItem.Work.Details.route) {
                    DetailsView(workDetails, innerPadding, onBackRequested)
                }
                composable(BottomNavItem.Work.Logs.route) {
                    LogsView(
                        workDetails,
                        innerPadding,
                        onLogSelected,
                        onBackRequested,
                        onCreationRequested,
                        onLogBackRequested
                    )
                }
                composable(BottomNavItem.Work.Members.route) {
                    MembersView(
                        workDetails,
                        innerPadding,
                        onMemberSelected,
                        onBackRequested,
                        onProfileBackRequested
                    )
                }
            }
        }
    }
}
