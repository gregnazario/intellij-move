package org.move.ide.navigation

import com.intellij.codeInsight.hint.DeclarationRangeHandler
import com.intellij.openapi.util.TextRange
import org.move.lang.core.psi.MvFunction
import org.move.lang.core.psi.MvModuleDef
import org.move.lang.core.psi.ext.endOffset
import org.move.lang.core.psi.ext.startOffset

class ModuleDeclarationRangeHandler : DeclarationRangeHandler<MvModuleDef> {
    override fun getDeclarationRange(container: MvModuleDef): TextRange {
        val startOffset = container.startOffset
        val endOffset = container.moduleBlock?.endOffset ?: container.endOffset
        return TextRange(startOffset, endOffset)
    }
}

class FunctionDeclarationRangeHandler: DeclarationRangeHandler<MvFunction> {
    override fun getDeclarationRange(container: MvFunction): TextRange {
        return TextRange(container.startOffset,
                         container.codeBlock?.endOffset ?: container.endOffset)
    }
}
