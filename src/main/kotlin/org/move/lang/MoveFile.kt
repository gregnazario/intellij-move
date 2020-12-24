package org.move.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import org.move.dove.DoveConstants

class MoveFile(fileViewProvider: FileViewProvider) : PsiFileBase(fileViewProvider, MoveLanguage) {
    override fun getFileType(): FileType = MoveFileType

//    override fun getStub(): MoveFileStub? = super.getStub() as MoveFileStub?
}

val VirtualFile.isMoveFile: Boolean get() = fileType == MoveFileType

val VirtualFile.isDoveToml: Boolean get() = name == DoveConstants.MANIFEST_FILE
