package org.move.dove.project.model

import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.util.io.exists
import com.intellij.util.messages.Topic
import org.move.dove.DoveConstants
import org.move.openapiext.common.isUnitTestMode
import org.move.openapiext.rootDir

class DoveProjectService(val project: Project) {
    init {
        with(project.messageBus.connect()) {
            if (!isUnitTestMode) {
                subscribe(VirtualFileManager.VFS_CHANGES,
                    object : BulkFileListener {
                        override fun after(events: MutableList<out VFileEvent>) {
                            if (events.any {
                                    it.pathEndsWith(DoveConstants.MANIFEST_FILE) || it.pathEndsWith(".move")
                                }) {
                                emitDoveProjectUpdatedEvent()
                            }
                        }
                    })
            }

        }
    }

    fun manifestFileExists(): Boolean {
        val rootDir = project.rootDir
        val manifestFile = rootDir.resolve(DoveConstants.MANIFEST_FILE)
        return manifestFile.exists()
    }

    fun emitDoveProjectUpdatedEvent() {
        invokeAndWaitIfNeeded {
            runWriteAction {
                project.messageBus.syncPublisher(DOVE_PROJECT_TOPIC)
                    .doveProjectUpdated(this)
            }
        }
    }

    companion object {
        val DOVE_PROJECT_TOPIC = Topic(
            "dove project changes",
            DoveProjectListener::class.java
        )
    }

    fun interface DoveProjectListener {
        fun doveProjectUpdated(service: DoveProjectService)
    }
}

val Project.doveProjectService get() = service<DoveProjectService>()

private fun VFileEvent.pathEndsWith(suffix: String): Boolean =
    path.endsWith(suffix) ||
            this is VFilePropertyChangeEvent && oldPath.endsWith(suffix)
