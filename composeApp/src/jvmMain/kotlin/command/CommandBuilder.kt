package command

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CommandBuilder(
    private val config: Config
) : ICommandBuilder {

    val logger = LoggerFactory.getLogger("CommandBuilder") as Logger
    data class Config(
        val bundleToolPath: String,
        val aabPath: String,
        val outputDir: String? = null,
        val isUniversal: Boolean = true,
        val keystore: KeystoreConfig? = null,
        val deviceId: String? = null,
        val overwrite: Boolean = false
    )

    data class KeystoreConfig(
        val path: String,
        val password: String,
        val alias: String,
        val keyPassword: String
    )

    override fun build(): Result<String> {
        if (config.bundleToolPath.isBlank()) return Result.failure(IllegalArgumentException("BundleTool path missing"))
        if (config.aabPath.isBlank()) return Result.failure(IllegalArgumentException("AAB file path missing"))

        val cmd = buildString {
            append("java -jar \"${config.bundleToolPath}\" build-apks ")
            append("--bundle=\"${config.aabPath}\" ")
            append("--output=\"${if(config.outputDir.isNullOrEmpty()) getDefaultOutput() else config.outputDir}\" ")
            if (config.isUniversal) append("--mode=universal ")
            if (config.overwrite) append("--overwrite ")
            config.keystore?.let {
                append("--ks=${it.path} ")
                append("--ks-pass=pass:${it.password} ")
                append("--ks-key-alias=${it.alias} ")
                append("--key-pass=pass:${it.keyPassword} ")
            }
            config.deviceId?.let { append("--device-id=$it ") }
            append("--overwrite ")
        }
        logger.debug(cmd.trim())
        return Result.success(cmd.trim())
    }

    private fun getDefaultOutput(): String {
        val base = config.aabPath.substringBeforeLast(".")
        return "$base.apks"
    }

    companion object {
        fun create(block: Builder.() -> Unit): CommandBuilder {
            val builder = Builder()
            builder.block()
            return CommandBuilder(builder.build())
        }
    }

    class Builder {
        private var bundleToolPath: String = ""
        private var aabPath: String = ""
        private var outputDir: String? = null
        private var isUniversal: Boolean = true
        private var keystore: KeystoreConfig? = null
        private var deviceId: String? = null
        private var overwrite: Boolean = false

        fun bundleTool(path: String) = apply { bundleToolPath = path }
        fun aab(path: String) = apply { aabPath = path }
        fun outputDir(dir: String?) = apply { outputDir = dir }
        fun universal(enable: Boolean) = apply { isUniversal = enable }
        fun overwrite(enable: Boolean) = apply { overwrite = enable }
        fun keystore(block: KeystoreConfig.() -> Unit) = apply {
            keystore = KeystoreConfig("", "", "", "").apply(block)
        }

        fun device(id: String?) = apply { deviceId = id }

        fun build() = Config(bundleToolPath, aabPath, outputDir, isUniversal, keystore, deviceId, overwrite)
    }
}