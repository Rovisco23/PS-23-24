package pt.isel.sitediary.ui.work.create

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.sitediary.domain.LogEntry
import pt.isel.sitediary.domain.LogInputModel
import pt.isel.sitediary.ui.common.nav.TopBarGoBack
import pt.isel.sitediary.ui.work.log.formatDate
import java.io.File

@Composable
fun CreateLogScreen(
    onCreateClicked: (String) -> Unit,
    onBackRequested: () -> Unit,
    onRemoveRequested: (String) -> HashMap<String, File>,
    onUploadRequested: () -> HashMap<String, File>,
    innerPadding: PaddingValues
) {
    var content by remember { mutableStateOf("") }
    var files by remember { mutableStateOf(mutableMapOf<String, File>()) }
    Column(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        TopBarGoBack(onBackRequested)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Observações",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp, top = 4.dp),
                thickness = 2.dp,
                color = Color(17, 17, 61)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ficheiros",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = {
                    val newFiles = onUploadRequested()
                    files = newFiles
                }) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload File Icon"
                    )
                }
            }
            FileList(
                files = files,
                onRemoveRequested = {
                    val newFiles = onRemoveRequested(it)
                    files = newFiles
                }
            )
            Button(
                colors = ButtonDefaults.buttonColors(Color(255, 122, 0)),
                enabled = content.isNotBlank(),
                modifier = Modifier.width(180.dp),
                onClick = {
                    onCreateClicked(content)
                }
            ) {
                Text(text = "Registar Ocorrência")
            }
        }
    }
}

@Composable
fun FileList(
    files: MutableMap<String, File>,
    onRemoveRequested: (String) -> Unit
) {
    files.forEach {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                    contentDescription = "File Icon"
                )
                Text(
                    text = it.key,
                    fontSize = 20.sp
                )
            }
            IconButton(onClick = { onRemoveRequested(it.key) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete File Icon"
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewCreateLogScreen() {
    CreateLogScreen(
        onCreateClicked = {},
        onBackRequested = { /*TODO*/ },
        onRemoveRequested = { hashMapOf() },
        onUploadRequested = { hashMapOf() },
        innerPadding = PaddingValues()
    )
}