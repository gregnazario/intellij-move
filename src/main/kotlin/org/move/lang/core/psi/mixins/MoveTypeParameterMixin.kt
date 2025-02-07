package org.move.lang.core.psi.mixins

import com.intellij.lang.ASTNode
import org.move.lang.core.psi.MvType
import org.move.lang.core.psi.MvTypeParameter
import org.move.lang.core.psi.impl.MvNameIdentifierOwnerImpl
import org.move.lang.core.types.infer.InferenceContext
import org.move.lang.core.types.ty.Ty
import org.move.lang.core.types.ty.TyTypeParameter

fun MvTypeParameter.ty(): TyTypeParameter = TyTypeParameter(this)

abstract class MvTypeParameterMixin(node: ASTNode) : MvNameIdentifierOwnerImpl(node),
                                                       MvTypeParameter {
//    override fun resolvedType(): Ty {
//        return TyTypeParameter(this)
//    }
}
