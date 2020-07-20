package org.move.lang.core.completion

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.impl.BaseFixture

class MvCompletionTestFixture(
    fixture: CodeInsightTestFixture,
    private val defaultFileName: String = "main.move"
): BaseFixture() {
}