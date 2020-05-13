package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

enum class SvcCommand(val parameter: String, val description: String) {
    WIFI("wifi", "Wi-Fi"),
    MOBILE("data", "Mobile data")
}

class ToggleSvcCommand(
        private val command: SvcCommand,
        private val enable: Boolean) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String, params: Any): Boolean {
        return false
    }

    private val shellCommand = "svc ${command.parameter} ${enable.toState()}"

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            device.executeShellCommand(shellCommand, GenericReceiver(), 30L, TimeUnit.SECONDS)
            info(String.format("<b>%s</b> %s%s on %s", command.description, enable.toState(), "d", device.name))
            return true
        } catch (e: Exception) {
            error("Failure while attempting to ${enable.toState()} ${command.description} on ${device.name}: " + e.message)
        }
        return false
    }

    private fun Boolean.toState() = if (this) "enable" else "disable"
}
