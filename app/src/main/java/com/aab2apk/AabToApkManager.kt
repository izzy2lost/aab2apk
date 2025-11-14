package com.aab2apk

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object AabToApkManager {
    private const val TAG = "AabToApkManager"

    /**
     * Attempt to convert an AAB to APK(s).
     *
     * This is a best-effort adapter: real conversion normally requires bundletool (Java jar) or
     * server-side conversion. On-device conversion is not generally supported unless you include
     * an executable bundletool binary compiled for Android and place it in the app's files dir.
     *
     * This method will:
     *  - copy the selected AAB to the app's internal files directory
     *  - check for a self-contained 'bundletool' executable in filesDir and try to run it (if present)
     *  - otherwise, show a Toast explaining how to perform conversion externally (or how to provide bundletool)
     */
    suspend fun convertAab(
        context: Context,
        aabUri: Uri,
        outputDirUri: Uri?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Copy AAB to internal storage
            val aabFile = File(context.filesDir, "input.aab")
            context.contentResolver.openInputStream(aabUri).use { input ->
                if (input == null) return@withContext Result.failure(Exception("Cannot open AAB input"))
                aabFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Ensure a bundletool asset (if packaged) is installed to filesDir and set executable
            val bundletoolFile = File(context.filesDir, "bundletool")
            try {
                if (!bundletoolFile.exists()) {
                    // Attempt to copy bundled asset (if present) into filesDir
                    context.assets.open("bundletool").use { input ->
                        bundletoolFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    bundletoolFile.setExecutable(true)
                }
            } catch (e: Exception) {
                // If asset not present or copy failed, we'll proceed to check for an existing executable.
            }
            if (bundletoolFile.exists() && bundletoolFile.canExecute()) {
                // Build output path
                val outFile = File(context.filesDir, "output.apks")
                val cmd = arrayListOf(bundletoolFile.absolutePath, "build-apks", "--bundle=${aabFile.absolutePath}", "--output=${outFile.absolutePath}", "--mode=universal")
                Log.i(TAG, "Running: ${cmd.joinToString(" ")}")
                val procBuilder = ProcessBuilder(cmd)
                procBuilder.redirectErrorStream(true)
                val proc = procBuilder.start()
                val stdout = StringBuilder()
                proc.inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { stdout.append(it).append("\n") }
                }
                val exit = proc.waitFor()
                if (exit == 0) {
                    // If outputDirUri provided, copy the result there
                    if (outputDirUri != null) {
                        val doc = DocumentFile.fromTreeUri(context, outputDirUri)
                        if (doc != null && doc.canWrite()) {
                            val outDoc = doc.createFile("application/octet-stream", "output.apks")
                            if (outDoc != null) {
                                context.contentResolver.openOutputStream(outDoc.uri).use { os ->
                                    outFile.inputStream().use { fis -> fis.copyTo(os!!) }
                                }
                                return@withContext Result.success("Conversion complete — output.apks saved to destination folder.")
                            }
                        }
                    }
                    return@withContext Result.success("Conversion complete. Output at ${outFile.absolutePath}")
                } else {
                    return@withContext Result.failure(Exception("bundletool failed with exit $exit\n${stdout}"))
                }
            } else {
                // No bundletool available — inform user what to do
                val msg = """bundletool executable not found in app files.
To convert an AAB to APK on-device you must provide a bundletool binary built for Android and place it at: ${context.filesDir}/bundletool
Alternatively, convert the AAB on a desktop using bundletool.jar:
java -jar bundletool-all.jar build-apks --bundle=app.aab --output=app.apks --mode=universal
Then transfer resulting .apks/.apks.zip to the phone."""
                Log.i(TAG, msg)
                return@withContext Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Conversion failed", e)
            return@withContext Result.failure(e)
        }
    }
}
