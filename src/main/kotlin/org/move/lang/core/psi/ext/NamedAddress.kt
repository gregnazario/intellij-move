package org.move.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import org.move.lang.core.psi.MoveElementImpl
import org.move.lang.core.psi.MoveNamedAddress
import org.move.openapiext.parentTable
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlTableHeader

val TomlTableHeader.isAddressesHeader: Boolean get() = text in setOf("[addresses]")

fun TomlKeySegment.isNamedAddressDef(): Boolean {
    val key = this.parent as? TomlKey ?: return false
    val tomlTable = (key.parent as? TomlKeyValue)?.parentTable ?: return false
    return tomlTable.header.isAddressesHeader
}

abstract class MoveNamedAddressMixin(node: ASTNode) : MoveElementImpl(node),
                                                      MoveNamedAddress {
    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }
}
