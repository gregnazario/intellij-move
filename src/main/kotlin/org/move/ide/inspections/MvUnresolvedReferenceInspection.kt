package org.move.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.*

class MvUnresolvedReferenceInspection : MvLocalInspectionTool() {
    override val isSyntaxOnly get() = false

    override fun buildMvVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : MvVisitor() {
        override fun visitModuleRef(moduleRef: MvModuleRef) {
            if (moduleRef.isMsl()) return

            // skip this check, as it will be checked in MvPath visitor
            if (moduleRef.ancestorStrict<MvPath>() != null) return

            if (moduleRef.ancestorStrict<MvImportStmt>() != null) return
            if (moduleRef is MvFQModuleRef) return

            if (moduleRef.isUnresolved) {
                holder.registerProblem(
                    moduleRef,
                    "Unresolved module reference: `${moduleRef.referenceName}`",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                )
            }
        }

        override fun visitPath(path: MvPath) {
            if (path.isMsl()) return
            if (path.isMsl() && path.isResult) return
            if (path.isUpdateFieldArg2) return
            if (path.isPrimitiveType()) return
            if (path.isMsl() && path.isSpecPrimitiveType()) return
            if (path.isInsideAssignmentLeft()) return
            if (path.text == "assert") return

            val moduleRef = path.pathIdent.moduleRef
            if (moduleRef != null) {
                if (moduleRef is MvFQModuleRef) return
                if (moduleRef.isUnresolved) {
                    holder.registerProblem(
                        moduleRef,
                        "Unresolved module reference: `${moduleRef.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                    return
                }
            }
            if (path.isUnresolved) {
                val description = when (path.parent) {
                    is MvPathType -> "Unresolved type: `${path.referenceName}`"
                    else -> "Unresolved reference: `${path.referenceName}`"
                }
                val highlightedElement = path.referenceNameElement ?: return
                holder.registerProblem(
                    highlightedElement,
                    description,
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                )
            }
        }

        override fun visitStructPatField(o: MvStructPatField) {
            if (o.isMsl()) return
            val resolvedStructDef = o.structPat.path.maybeStruct ?: return
            if (!resolvedStructDef.fieldNames.any { it == o.referenceName }) {
                holder.registerProblem(
                    o.referenceNameElement,
                    "Unresolved field: `${o.referenceName}`",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                )
            }
        }

        override fun visitStructLitField(litField: MvStructLitField) {
            if (litField.isMsl()) return
            if (litField.isShorthand) {
                val resolvedItems = litField.reference.multiResolve()
                val resolvedStructField = resolvedItems.find { it is MvStructField }
                if (resolvedStructField == null) {
                    holder.registerProblem(
                        litField.referenceNameElement,
                        "Unresolved field: `${litField.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
                val resolvedBinding = resolvedItems.find { it is MvBindingPat }
                if (resolvedBinding == null) {
                    holder.registerProblem(
                        litField.referenceNameElement,
                        "Unresolved reference: `${litField.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            } else {
                if (litField.reference.resolve() == null) {
                    holder.registerProblem(
                        litField.referenceNameElement,
                        "Unresolved field: `${litField.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }

        override fun visitSchemaLitField(field: MvSchemaLitField) {
            if (field.isShorthand) {
                val resolvedItems = field.reference.multiResolve()
                val fieldBinding = resolvedItems.find { it is MvBindingPat && it.owner is MvSchemaFieldStmt }
                if (fieldBinding == null) {
                    holder.registerProblem(
                        field.referenceNameElement,
                        "Unresolved field: `${field.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
                val letBinding = resolvedItems.find { it is MvBindingPat }
                if (letBinding == null) {
                    holder.registerProblem(
                        field.referenceNameElement,
                        "Unresolved reference: `${field.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            } else {
                if (field.reference.resolve() == null) {
                    holder.registerProblem(
                        field.referenceNameElement,
                        "Unresolved field: `${field.referenceName}`",
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }
}
