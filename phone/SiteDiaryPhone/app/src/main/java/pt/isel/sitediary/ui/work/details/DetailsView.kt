package pt.isel.sitediary.ui.work.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import pt.isel.sitediary.domain.LoadState
import pt.isel.sitediary.domain.Loaded
import pt.isel.sitediary.domain.Work
import pt.isel.sitediary.ui.common.LoadingScreen
import pt.isel.sitediary.ui.common.nav.TopBarGoBack

@Composable
fun DetailsView(
    work: LoadState<Work>,
    innerPadding: PaddingValues,
    onBackRequested: () -> Unit = {}
) {
    work.let {
        if (it is Loaded && it.value.isSuccess) {
            it.value.getOrNull()?.let { work ->
                val details = work.toDetails()
                DetailsScreen(details, innerPadding, onBackRequested)
            }
        } else {
            LoadingScreen()
        }
    }
}