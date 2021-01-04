package org.move.ide.notifications

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import org.move.dove.project.model.DoveProjectService
import org.move.dove.project.model.doveProjectService
import org.move.lang.isDoveToml
import org.move.lang.isMoveFile
import org.move.openapiext.common.isDispatchThread
import org.move.openapiext.common.isUnitTestMode

class NoDoveProjectFoundNotificationProvider(project: Project) : MoveNotificationProvider(project) {
    init {
        project.messageBus.connect().apply {
            subscribe(DoveProjectService.DOVE_PROJECT_TOPIC,
                DoveProjectService.DoveProjectListener {
                    updateAllNotifications()
                })
        }
    }

    override val VirtualFile.disablingKey: String
        get() = NOTIFICATION_STATUS_KEY + path

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project,
    ): MoveEditorNotificationPanel? {
        if (isUnitTestMode && !isDispatchThread) return null
        if (!(file.isMoveFile || file.isDoveToml) || isNotificationDisabled(file)) return null

        if (!project.doveProjectService.isConfigured()) {
            return createNoDoveProjectFoundPanel(file)
        }

        return null
    }

    private fun createNoDoveProjectFoundPanel(file: VirtualFile): MoveEditorNotificationPanel =
        MoveEditorNotificationPanel(NO_DOVE_PROJECT_FOUND)
            .apply {
                text = "No Dove project found, cross-file functionality will be disabled"
                createActionLabel("Do not show again") {
                    disableNotification(file)
                    updateAllNotifications()
                }
            }

    override fun getKey(): Key<EditorNotificationPanel> = PROVIDER_KEY

    companion object {
        private const val NOTIFICATION_STATUS_KEY = "org.rust.hideNoDoveProjectNotifications"

        const val NO_DOVE_PROJECT_FOUND = "NoDoveProjectFound"

        private val PROVIDER_KEY: Key<EditorNotificationPanel> = Key.create("No Dove project")
    }
}
