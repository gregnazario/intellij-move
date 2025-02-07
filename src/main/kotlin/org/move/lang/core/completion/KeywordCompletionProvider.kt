package org.move.lang.core.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import org.move.lang.core.psi.ext.isErrorElement
import org.move.lang.core.psi.ext.isWhitespace
import org.move.lang.core.psi.ext.rightSiblings

val ALWAYS_NEEDS_SPACE = setOf("module")

class KeywordCompletionProvider(
    private vararg val keywords: String,
) : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        for (keyword in keywords) {
            var element =
                LookupElementBuilder.create(keyword).bold()
            val posParent = parameters.position.parent
            val posRightSiblings = posParent.rightSiblings
                .filter { !it.isWhitespace() && !it.isErrorElement() }
            val posParentNextSibling = posRightSiblings.firstOrNull()?.firstChild

            element = element.withInsertHandler { ctx, _ ->
                val elemSibling = parameters.position.nextSibling
                val suffix = when {
                    elemSibling != null && elemSibling.isWhitespace() -> ""
                    posParentNextSibling == null || !ctx.alreadyHasSpace -> " "
                    else -> ""
                }
                ctx.addSuffix(suffix)
            }
            result.addElement(PrioritizedLookupElement.withPriority(element, KEYWORD_PRIORITY))
        }
    }
}

//private fun addInsertionHandler(
//    keyword: String,
//    builder: LookupElementBuilder,
//    parameters: CompletionParameters,
//): LookupElementBuilder {
//    val nextSibling = parameters.position.parent.nextSibling
//    if (nextSibling.elementType != TokenType.WHITE_SPACE) {
//        return builder.withInsertHandler { ctx, _ -> ctx.addSuffix(" ") }
//    }
//    return builder
////    val suffix = when (keyword) {
////        "script" -> {
////            val nextSibling = parameters.position.parent.nextSibling
////            if (nextSibling.elementType == TokenType.WHITE_SPACE)
////                ""
////            else
////                " "
////        }
////        else -> " "
////    }
////    return builder.withInsertHandler { ctx, _ -> ctx.addSuffix(suffix) }
//}
