package org.move.lang.core.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.move.ide.annotator.BUILTIN_FUNCTIONS
import org.move.ide.annotator.BUILTIN_TYPE_IDENTIFIERS
import org.move.ide.annotator.PRIMITIVE_TYPE_IDENTIFIERS
import org.move.lang.core.psi.ext.structDef
import org.move.lang.core.resolve.ref.MoveQualPathReferenceImpl
import org.move.lang.core.resolve.ref.MoveReference
import org.move.lang.core.resolve.ref.Namespace
import org.move.lang.core.resolve.ref.NamedAddressReference

interface PsiReferenceElement: PsiElement {
    val identifier: PsiElement?
    @JvmDefault
    val referenceNameElement: PsiElement?
        get() = identifier
    @JvmDefault
    val referenceName: String?
        get() = identifier?.text

    override fun getReference(): PsiReference?
    @JvmDefault
    val isUnresolved: Boolean
        get() = reference?.resolve() == null
}

interface MoveReferenceElement : PsiReferenceElement, MoveElement {

    override fun getReference(): MoveReference?
}

interface MoveSchemaReferenceElement : MoveReferenceElement

//abstract class MoveReferenceElementImpl(node: ASTNode) : MoveElementImpl(node),
//                                                         MoveReferenceElement {
//    override val referenceNameElement: PsiElement
//        get() =
//        get() = requireNotNull(findFirstChildByType(MoveElementTypes.IDENTIFIER)) {
//            "Reference elements should all have IDENTIFIER as a direct child: $node doesn't for some reason"
//        }

//    abstract override fun getReference(): MoveReference
//}

interface MoveQualPathReferenceElement : MoveReferenceElement {
    val isPrimitive: Boolean

    val qualPath: MoveQualPath

    @JvmDefault
    override val isUnresolved: Boolean
        get() = !isPrimitive && reference?.resolve() == null
}

//abstract class MoveQualPathReferenceElementImpl : MoveQualPathReferenceElement {
//    override fun getReference(): MoveReference = qualPath.reference
//}

interface MoveQualTypeReferenceElement : MoveQualPathReferenceElement {
    override val isPrimitive: Boolean
        get() = referenceName in PRIMITIVE_TYPE_IDENTIFIERS
                || referenceName in BUILTIN_TYPE_IDENTIFIERS
}

val MoveQualTypeReferenceElement.referredStructSignature: MoveStructSignature?
    get() {
        return reference?.resolve() as? MoveStructSignature
    }

val MoveQualTypeReferenceElement.referredStructDef: MoveStructDef?
    get() {
        return referredStructSignature?.structDef
    }

abstract class MoveQualTypeReferenceElementImpl(node: ASTNode) : MoveElementImpl(node),
                                                                 MoveQualTypeReferenceElement {
    override val identifier: PsiElement get() = qualPath.identifier

    override fun getReference(): MoveReference = MoveQualPathReferenceImpl(this, Namespace.TYPE)
}

interface MoveQualNameReferenceElement : MoveQualPathReferenceElement {
    override val isPrimitive: Boolean
        get() = referenceName in BUILTIN_FUNCTIONS
}

abstract class MoveQualNameReferenceElementImpl(node: ASTNode) : MoveElementImpl(node),
                                                                 MoveQualNameReferenceElement {

    override val identifier: PsiElement get() = qualPath.identifier

    override fun getReference(): MoveReference = MoveQualPathReferenceImpl(this, Namespace.NAME)
}


interface MoveQualSchemaReferenceElement : MoveQualPathReferenceElement {
    @JvmDefault
    override val isUnresolved: Boolean
        get() =
            reference?.resolve() == null
}

abstract class MoveQualSchemaReferenceElementImpl(node: ASTNode) : MoveElementImpl(node),
                                                                   MoveQualSchemaReferenceElement {

    override val identifier: PsiElement get() = qualPath.identifier

    override fun getReference(): MoveReference = MoveQualPathReferenceImpl(this, Namespace.SCHEMA)
}

interface MoveStructFieldReferenceElement : MoveReferenceElement
