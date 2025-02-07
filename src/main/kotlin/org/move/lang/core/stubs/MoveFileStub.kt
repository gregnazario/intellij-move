package org.move.lang.core.stubs

import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IStubFileElementType
import org.move.lang.MvFile
import org.move.lang.MvLanguage

class MvFileStub(file: MvFile) : PsiFileStubImpl<MvFile>(file) {
    override fun getType() = Type

    object Type : IStubFileElementType<MvFileStub>(MvLanguage) {
        // Bump this number if Stub structure changes
        override fun getStubVersion(): Int {
            return 5002
        }
//        override fun getStubVersion(): Int = 5002

        override fun getBuilder(): StubBuilder = object : DefaultStubBuilder() {
            override fun createStubForFile(file: PsiFile): StubElement<*> {
                return MvFileStub(file as MvFile)
            }

//            override fun skipChildProcessingWhenBuildingStubs(parent: ASTNode, child: ASTNode): Boolean {
//                val elementType = child.elementType
//                return elementType == CODE_BLOCK && parent.elementType == FUNCTION_DEF
//            }
        }

        override fun getExternalId() = "Move.file"
    }
}
