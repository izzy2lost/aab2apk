package ui.windows

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SplashWindow(
    appName: String = "BundleTool",
    version: String = "v1.0.0",
    onFinish: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        // Simulate initialization delay
        kotlinx.coroutines.delay(2000)
        onFinish()
    }

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App icon
            Image(
                painter = painterResource("files/launcher.png"),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            // App title
            Text(
                text = appName,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Companion.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Version
            Text(
                text = version,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Companion.Medium,
                style = MaterialTheme.typography.titleMedium
            )
            // Loader
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}