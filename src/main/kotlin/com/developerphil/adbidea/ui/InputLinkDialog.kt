package com.developerphil.adbidea.ui;


import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.adb.AdbFacade
import com.developerphil.adbidea.adb.AdbUtil
import com.intellij.openapi.project.Project;
import javax.swing.JComponent
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.util.prefs.Preferences
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JTextField

class InputLinkDialog(myProject: Project) : DialogWrapper(true) {

    private val myProject: Project

    lateinit var myPanel: JPanel
    lateinit var jTextField: JTextField
    lateinit var editorPanel: JEditorPane

    private val pref = Preferences.userRoot().node(this.javaClass.name)

    companion object {
        const val OPEN_LINK_KEY = "OpenLink"
        const val INPUT_LINK_KEY = "InputLink"
    }

    init {
        title = "open the news details page"
        isOKActionEnabled = true
        this.myProject = myProject
        init()
        initView()
    }

    private fun initView() {
        jTextField.toolTipText = "Deep Link or App Link"
        if (pref.get(INPUT_LINK_KEY, null) != null) {
            jTextField.text = pref.get(INPUT_LINK_KEY, null)
        }
        initEditorPanel()
    }

    private fun initEditorPanel() {
        val tempValue = pref.get(OPEN_LINK_KEY, null)
        if (tempValue == null || "" == tempValue) {
            val localDir = File(myProject.basePath + "note.txt")
            try {
                editorPanel.setPage(localDir.toURI().toString())
                editorPanel.enableInputMethods(true)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            editorPanel.text = tempValue
        }
    }

    override fun createCenterPanel(): JComponent = myPanel

    override fun doOKAction() {
        if (isOKActionEnabled) {
            if ("" == jTextField.text) {
                Messages.showErrorDialog("The input must not be empty", "Error")
            } else {
                pref.put(OPEN_LINK_KEY, editorPanel.text)
                pref.put(INPUT_LINK_KEY, jTextField.text)
                val linkStr = jTextField.text?.let { AdbUtil.escape(it) }
                if (linkStr != null) {
                    AdbFacade.openLink(myProject, linkStr)
                }
                this.close(0)
            }
        }
    }
}
