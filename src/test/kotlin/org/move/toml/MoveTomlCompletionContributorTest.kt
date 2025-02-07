package org.move.toml

import org.move.utils.tests.MoveTomlCompletionTestBase

class MoveTomlCompletionContributorTest: MoveTomlCompletionTestBase() {
    fun `test complete top level`() {
        myFixture.configureByText("Move.toml", "[addr<caret>]")
        val completions = myFixture.completeBasic().map { it.lookupString }
        assertEquals(
            completions,
            listOf("addresses", "dev-addresses")
        )
    }

    fun `test complete package name`() = doSingleCompletion("""
    [package]
    na/*caret*/    
    """, """
    [package]
    name/*caret*/    
    """)
}
