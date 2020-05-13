package com.developerphil.adbidea.action

import com.developerphil.adbidea.ui.InputLinkDialog
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class OpenLinkAction : AdbAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) {
        val dialog = InputLinkDialog(project)
        dialog.show()
    }
}