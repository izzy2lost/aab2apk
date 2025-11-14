package com.aab2apk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: ComponentActivity() {
    private var pickedAab: Uri? = null
    private var pickedOutputFolder: Uri? = null

    private val pickAabLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickedAab = uri
            Toast.makeText(this, "AAB selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickFolderLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickedOutputFolder = uri
            Toast.makeText(this, "Output folder selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "aab2apk", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = { pickAabLauncher.launch(arrayOf("application/octet-stream","*/*")) }, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Pick .aab file")
                    }
                    Button(onClick = { pickFolderLauncher.launch(null) }, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Select output folder")
                    }
                    Button(onClick = {
                        if (pickedAab == null) {
                            Toast.makeText(this, "Please pick an AAB first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@MainActivity, "Starting conversion (best-effort)...", Toast.LENGTH_SHORT).show()
                            val result = AabToApkManager.convertAab(this@MainActivity, pickedAab!!, pickedOutputFolder)
                            if (result.isSuccess) {
                                Toast.makeText(this@MainActivity, "Success: ${'$'}{result.getOrNull()}", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Failed: ${'$'}{result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Convert AAB to APK (best-effort)")
                    }
                }
            }
        }
    }
}
