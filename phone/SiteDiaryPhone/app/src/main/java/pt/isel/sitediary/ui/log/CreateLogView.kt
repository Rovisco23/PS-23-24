package pt.isel.sitediary.ui.log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.isel.sitediary.domain.Idle
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.LogInputModel
import pt.isel.sitediary.ui.common.LoadingScreen
import pt.isel.sitediary.ui.common.nav.DefaultTopBar
import pt.isel.sitediary.ui.common.nav.TopBarGoBack
import pt.isel.sitediary.ui.theme.SiteDiaryPhoneTheme

@Composable
fun CreateLogView(
    creation: LoadState<Unit>,
    onCreateClicked: (LogInputModel) -> Unit = {},
    onBackRequested: () -> Unit = {}
) {
    SiteDiaryPhoneTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                DefaultTopBar()
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TopBarGoBack(onBackRequested)
                creation.let {
                    if (it is Idle) {
                        CreateLogScreen(
                            onCreateClicked = onCreateClicked,
                            innerPadding = innerPadding
                        )
                    } else {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateLogViewPreview() {
    CreateLogView(
        creation = Idle,
        onCreateClicked = {}
    )
}