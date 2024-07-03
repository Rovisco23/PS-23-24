package pt.isel.sitediary.ui.common.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import pt.isel.sitediary.R

sealed class BottomNavItem(val route: String, @StringRes val title: Int, val icon: ImageVector) {
    object Main {
        data object Home : BottomNavItem("Obras", R.string.obras, Icons.Default.Home)
        data object UserProfile : BottomNavItem("Perfil", R.string.perfil, Icons.Default.Person)
    }

    object Work {
        data object Details : BottomNavItem("Detalhes", R.string.detalhes, Icons.Default.Info)
        data object Logs : BottomNavItem("Ocorrências", R.string.registo, Icons.Default.ErrorOutline)
        data object Members : BottomNavItem("Membros", R.string.membros, Icons.Default.Groups)
    }
}