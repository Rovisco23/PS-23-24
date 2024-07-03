package pt.isel.sitediary.ui.log

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pt.isel.sitediary.domain.LogInputModel

@Composable
fun CreateLogScreen(onCreateClicked: (LogInputModel) -> Unit, innerPadding: PaddingValues) {
    Column(modifier = Modifier.padding(innerPadding)) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        TextField(
            singleLine = true,
            label = { Text("Title") },
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .width(232.dp)
                .border(2.dp, Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            singleLine = true,
            label = { Text("Observação") },
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .width(232.dp)
                .border(2.dp, Color.Black),
        )
        Button(
            colors = ButtonDefaults.buttonColors(Color(255, 122, 0)),
            enabled = checkCameraHardware(LocalContext.current),
            modifier = Modifier.width(180.dp),
            onClick = {
                //getCameraInstance()
            }
        ) {
            Text(text = "Câmara")
        }
        Button(
            colors = ButtonDefaults.buttonColors(Color(255, 122, 0)),
            enabled = title.isNotBlank() && description.isNotBlank(),
            modifier = Modifier.width(180.dp),
            onClick = {
                onCreateClicked(
                    LogInputModel(
                        title = title,
                        description = description
                    )
                )
            }
        ) {
            Text(text = "Registar Ocorrência")
        }
    }
}

private fun checkCameraHardware(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
}
/*
/** A safe way to get an instance of the Camera object. */
private fun getCameraInstance(): CameraDevice? {
    return try {
        val session = CameraCaptureSession.StateCallback // attempt to get a Camera instance

    } catch (e: Exception) {
        // Camera is not available (in use or does not exist)
        null // returns null if camera is unavailable
    }
}*/