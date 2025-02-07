package org.move.utils.tests

import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.psi.PsiElement
import org.intellij.lang.annotations.Language
import org.move.ide.docs.MvDocumentationProvider
import org.move.lang.core.psi.MvElement
import org.move.openapiext.findVirtualFile
import org.move.utils.tests.base.findElementAndOffsetInEditor

abstract class MvDocumentationProviderProjectTestCase: MvProjectTestCase() {
    protected fun doTestByFileTree(
        @Language("Move") code: String,
        @Language("Html") expected: String?,
        findElement: () -> Pair<PsiElement, Int> = { myFixture.findElementAndOffsetInEditor() },
        block: MvDocumentationProvider.(PsiElement, PsiElement?) -> String?
    ) {
        val testProject = testProjectFromFileTree(code)
        myFixture.configureFromFileWithCaret(testProject)

        val (originalElement, offset) = myFixture.findElementAndOffsetInEditor<MvElement>()
        val element = DocumentationManager.getInstance(project)
            .findTargetElement(myFixture.editor, offset, myFixture.file, originalElement)!!

        val actual = MvDocumentationProvider().block(element, originalElement)?.trim()
        if (expected == null) {
            check(actual == null) { "Expected null, got `$actual`" }
        } else {
            check(actual != null) { "Expected not null result" }
            assertSameLines(actual, expected.trimIndent())
        }
    }
}

abstract class MvDocumentationProviderTestCase : MvTestBase() {
    protected fun doTest(
        @Language("Move") code: String,
        @Language("Html") expected: String?,
        findElement: () -> Pair<PsiElement, Int> = { myFixture.findElementAndOffsetInEditor() },
        block: MvDocumentationProvider.(PsiElement, PsiElement?) -> String?
    ) {
        @Suppress("NAME_SHADOWING")
        doTest(code, expected, findElement, block) { actual, expected ->
            assertSameLines(expected.trimIndent(), actual)
        }
    }

//    protected fun doTest(
//        @Language("Move") code: String,
//        expected: Regex?,
//        findElement: () -> Pair<PsiElement, Int> = { myFixture.findElementAndOffsetInEditor() },
//        block: MvDocumentationProvider.(PsiElement, PsiElement?) -> String?
//    ) {
//        @Suppress("NAME_SHADOWING")
//        doTest(code, expected, findElement, block) { actual, expected ->
//            assertTrue(actual.matches(expected))
//        }
//    }

    protected fun <T> doTest(
        @Language("Move") code: String,
        expected: T?,
        findElement: () -> Pair<PsiElement, Int> = { myFixture.findElementAndOffsetInEditor() },
        block: MvDocumentationProvider.(PsiElement, PsiElement?) -> String?,
        check: (String, T) -> Unit
    ) {
        InlineFile(myFixture, code, "main.move")

        val (originalElement, offset) = findElement()
        val element = DocumentationManager.getInstance(project)
            .findTargetElement(myFixture.editor, offset, myFixture.file, originalElement)!!

        val actual = MvDocumentationProvider().block(element, originalElement)?.trim()
        if (expected == null) {
            check(actual == null) { "Expected null, got `$actual`" }
        } else {
            check(actual != null) { "Expected not null result" }
            check(actual, expected)
        }
    }
}
