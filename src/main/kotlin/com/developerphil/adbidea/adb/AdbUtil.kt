package com.developerphil.adbidea.adb

import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.IDevice
import com.android.ddmlib.ShellCommandUnresponsiveException
import com.android.ddmlib.TimeoutException
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.joor.Reflect
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object AdbUtil {
    @Throws(TimeoutException::class, AdbCommandRejectedException::class, ShellCommandUnresponsiveException::class, IOException::class)
    fun isAppInstalled(device: IDevice, packageName: String): Boolean {
        val receiver = GenericReceiver()
        // "pm list packages com.my.package" will return one line per package installed that corresponds to this package.
        // if this list is empty, we know for sure that the app is not installed
        device.executeShellCommand("pm list packages $packageName", receiver, 15L, TimeUnit.SECONDS)
        //TODO make sure that it is the exact package name and not a subset.
        // e.g. if our app is called com.example but there is another app called com.example.another.app, it will match and return a false positive
        return receiver.adbOutputLines.isNotEmpty()
    }

    // The android debugger class is not available in Intellij 2016.1.
    // Nobody should use that version but it's still the minimum "supported" version since android studio 2.2
    // shares the same base version.
    val isDebuggingAvailable: Boolean
        get() = try {
            Reflect.on("com.android.tools.idea.run.editor.AndroidDebugger").get<Any>()
            true
        } catch (e: Exception) {
            false
        }

    fun isGradleSyncInProgress(project: Project): Boolean {
        return try {
            GradleSyncState.getInstance(project).isSyncInProgress
        } catch (t: Throwable) {
            info("Couldn't determine if a gradle sync is in progress")
            false
        }
    }

    /**
     *  Open Url or AppLink
    */
    fun startActionView(project: Project, device: IDevice, facet: AndroidFacet, packageName: String, params: Any): Boolean {
        try {
            val command = "am start -a android.intent.action.VIEW -d $params"
            device.executeShellCommand(command, GenericReceiver(), 15L, TimeUnit.SECONDS)
            info(command)
            return true
        } catch (e: Exception) {
            error("am start fail... " + e.message)
        }
    }

    private val sEscapePattern = Pattern.compile("([\\()*+?\"\'&#/\\s])")

    fun escape(entryName: String): String {
        return sEscapePattern.matcher(entryName).replaceAll("\\\\$1")
    }

}