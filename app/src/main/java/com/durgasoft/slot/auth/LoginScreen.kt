package com.durgasoft.slot.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    auth: AuthManager,
    onLoggedIn: () -> Unit
) {
    val ctx = LocalContext.current
    val activity = ctx as Activity

    val busy = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        busy.value = false
        auth.handleResult(
            activity = activity,
            data = res.data,
            onSuccess = onLoggedIn,
            onError = { msg -> error.value = msg }
        )
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Durgaslot", style = MaterialTheme.typography.headlineMedium)

            Button(
                enabled = !busy.value,
                onClick = {
                    error.value = null
                    busy.value = true
                    launcher.launch(auth.signInIntent())
                }
            ) {
                Text(if (busy.value) "Conectando..." else "Iniciar sesión con Google")
            }

            val e = error.value
            if (!e.isNullOrBlank()) {
                Text(e, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
