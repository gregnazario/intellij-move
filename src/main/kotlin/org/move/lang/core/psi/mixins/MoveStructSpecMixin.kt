package org.move.lang.core.psi.mixins

import com.intellij.lang.ASTNode
import org.move.lang.core.psi.MoveStructSpec
import org.move.lang.core.psi.impl.MoveReferenceElementImpl
import org.move.lang.core.resolve.ref.MoveReference
import org.move.lang.core.resolve.ref.MoveLocalReferenceImpl
import org.move.lang.core.resolve.ref.Namespace

abstract class MoveStructSpecMixin(node: ASTNode) : MoveReferenceElementImpl(node),
                                                    MoveStructSpec {
    override fun getReference(): MoveReference =
        MoveLocalReferenceImpl(this, Namespace.TYPE)

}
