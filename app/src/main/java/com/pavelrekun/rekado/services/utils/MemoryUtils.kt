package com.pavelrekun.rekado.services.utils

import com.pavelrekun.rekado.R
import com.pavelrekun.rekado.RekadoApplication
import com.pavelrekun.rekado.services.Events
import com.pavelrekun.rekado.services.extensions.parseConfig
import com.pavelrekun.rekado.services.extensions.toFile
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.InputStream

object MemoryUtils {

    private val resources = RekadoApplication.context.resources

    fun parseBundledConfig() {
        if (PreferencesUtils.checkConfigExists()) {
            val currentConfig = PreferencesUtils.getCurrentConfig()
            val bundledConfig = resources.openRawResource(R.raw.config).parseConfig()

            if (bundledConfig.timestamp > currentConfig.timestamp) {
                PreferencesUtils.saveConfig(bundledConfig)
                copyBundledPayloads()
            }
        } else {
            val config = resources.openRawResource(R.raw.config).parseConfig()
            PreferencesUtils.saveConfig(config)
            copyBundledPayloads()
        }
    }

    fun copyPayload(inputStream: InputStream, file: String) {
        inputStream.toFile("${getLocation().absolutePath}/$file")
    }

    fun getLocation(): File {
        return RekadoApplication.context.getExternalFilesDir(null)
                ?: RekadoApplication.context.filesDir
    }

    private fun copyBundledPayloads() {
        copyPayload(resources.openRawResource(R.raw.fusee), "fusee.bin")
        copyPayload(resources.openRawResource(R.raw.hekate), "hekate.bin")
        copyPayload(resources.openRawResource(R.raw.reinx), "reinx.bin")

        EventBus.getDefault().post(Events.UpdatePayloadsListEvent())
    }


}