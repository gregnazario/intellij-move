package org.move.utils.tests.annotation

import org.intellij.lang.annotations.Language
import org.move.utils.tests.MvTestBase

abstract class MvAnnotationTestCase : MvTestBase() {
    protected lateinit var annotationFixture: MvAnnotationTestFixture

    override fun setUp() {
        super.setUp()
        annotationFixture = createAnnotationFixture()
        annotationFixture.setUp()
    }

    override fun tearDown() {
        annotationFixture.tearDown()
        super.tearDown()
    }

    protected abstract fun createAnnotationFixture(): MvAnnotationTestFixture

    fun checkHighlighting(text: String, ignoreExtraHighlighting: Boolean = true) =
        annotationFixture.checkHighlighting(
            text,
            ignoreExtraHighlighting
        )

    fun checkErrors(@Language("Move") text: String) = annotationFixture.checkErrors(text)
    fun checkWarnings(@Language("Move") text: String) = annotationFixture.checkWarnings(text)

    protected fun checkByText(
        @Language("Move") text: String,
        checkWarn: Boolean = true,
        checkInfo: Boolean = false,
        checkWeakWarn: Boolean = false,
        ignoreExtraHighlighting: Boolean = false,
    ) = annotationFixture.checkByText(
        text,
        checkWarn,
        checkInfo,
        checkWeakWarn,
        ignoreExtraHighlighting,
    )
}
