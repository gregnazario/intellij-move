package org.move.lang.core.psi.mixins

import com.intellij.lang.ASTNode
import org.move.ide.MoveIcons
import org.move.lang.core.psi.MoveNativeFunctionDef
import org.move.lang.core.psi.impl.MoveNameIdentifierOwnerImpl
import javax.swing.Icon

abstract class MoveNativeFunctionDefMixin(node: ASTNode) : MoveNameIdentifierOwnerImpl(node),
                                                           MoveNativeFunctionDef {

    override fun getIcon(flags: Int): Icon = MoveIcons.FUNCTION
}