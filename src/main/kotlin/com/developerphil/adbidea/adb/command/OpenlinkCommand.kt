package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet

class OpenlinkCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String, params: Any): Boolean {
        AdbUtil.startActionView(project, device, facet, packageName, params)
        return false
    }

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        return false
    }
}
