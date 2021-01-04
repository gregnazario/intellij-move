package org.move.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import org.move.dove.project.model.doveProjectService
import org.move.dove.project.model.enabledCrossFileResolution
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.*
import org.move.lang.core.resolve.ref.MoveQualPathReferenceImpl

class MoveUnresolvedReferenceInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        object : MoveVisitor() {
            override fun visitModuleRef(moduleRef: MoveModuleRef) {
                if (moduleRef.ancestorStrict<MoveImportStatement>() != null) return
                if (moduleRef is MoveFullyQualifiedModuleRef) return
                if (!moduleRef.project.doveProjectService.isConfigured()) return

                if (moduleRef.isUnresolved) {
                    holder.registerProblem(
                        moduleRef,
                        "Unresolved module reference: `${moduleRef.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                }
            }

            override fun visitQualPath(qualPath: MoveQualPath) {
                val refElement = qualPath.parent
                if (refElement !is MoveQualPathReferenceElement) return
                if (refElement.isPrimitive) return

                val resolved = refElement.reference.resolve() != null
                if (!resolved && !refElement.project.enabledCrossFileResolution) {
                    // try to resolve to import only
                    val ref = refElement.reference
                    if (ref !is MoveQualPathReferenceImpl<*> || ref.resolveToItemImport() != null) return
                }

                if (refElement.isUnresolved && qualPath.isIdentifierOnly) {
                    val highlightedElement = refElement.referenceNameElement
                    holder.registerProblem(
                        highlightedElement,
                        "Unresolved reference: `${refElement.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                }
            }

            override fun visitStructPatField(o: MoveStructPatField) {
                val resolvedStructDef = o.structPat.referredStructDef ?: return
                if (!resolvedStructDef.fieldNames.any { it == o.referenceName }) {
                    val highlightedElement = o.referenceNameElement
                    holder.registerProblem(
                        highlightedElement,
                        "Unresolved field: `${o.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                }
            }

            override fun visitStructLiteralField(o: MoveStructLiteralField) {
                if (o.isUnresolved) {
                    val highlightedElement = o.referenceNameElement
                    val errorMessage =
                        if (o.isShorthand)
                            "Unresolved reference: `${o.referenceName}`"
                        else
                            "Unresolved field: `${o.referenceName}`"
                    holder.registerProblem(
                        highlightedElement,
                        errorMessage,
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                }
            }
        }
}
