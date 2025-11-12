package utils.files

import java.io.File
import java.io.FileOutputStream

object FileUtilsImpl : IFileUtils {
    override fun renameFile(source: File, destination: File): Boolean {
        if (!source.exists()) return false
        return source.renameTo(destination)
    }

    override fun unzip(zipFile: File, outputDir: String) {
        val buffer = ByteArray(1024)
        val zipInputStream = java.util.zip.ZipInputStream(zipFile.inputStream())
        var entry = zipInputStream.nextEntry
        while (entry != null) {
            val newFile = File(outputDir, entry.name)
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile?.mkdirs()
                FileOutputStream(newFile).use { output ->
                    var len: Int
                    while (zipInputStream.read(buffer).also { len = it } > 0) {
                        output.write(buffer, 0, len)
                    }
                }
            }
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }
        zipInputStream.close()
    }

    override fun deleteFile(file: File): Boolean = file.delete()
}