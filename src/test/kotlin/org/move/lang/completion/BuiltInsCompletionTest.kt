package org.move.lang.completion

import org.intellij.lang.annotations.Language
import org.move.ide.annotator.BUILTIN_FUNCTIONS
import org.move.utils.tests.completion.CompletionTestCase

class BuiltInsCompletionTest : CompletionTestCase() {
    fun `test autocompletion for built-in functions in expr position`() = checkContainsBuiltins("""
        module 0x1::M {
            fun main() {
                /*caret*/
            }
        }    
    """)

    fun `test no builtins in script`() = checkNoCompletion("""
        script {
            fun main() {
                borrow_glo/*caret*/
            }
        }    
    """)

    fun `test parens added`() = doSingleCompletion("""
        module 0x1::M {
            fun main() {
                move_t/*caret*/
            }
        }    
    """, """
        module 0x1::M {
            fun main() {
                move_to(/*caret*/)
            }
        }    
    """)

    fun `test parens existed`() = doSingleCompletion("""
        module 0x1::M {
            fun main() {
                move_t/*caret*/()
            }
        }    
    """, """
        module 0x1::M {
            fun main() {
                move_to(/*caret*/)
            }
        }    
    """)

    fun `test no builtins in type position`() = checkNoCompletion("""
        module 0x1::M {
            fun main(a: borrow/*caret*/) {}
        }    
    """)

    fun `test no builtins in qualified path`() = checkNoCompletion("""
        module 0x1::M {
            fun main() {
                let a = Libra::borrow/*caret*/
            }
        }    
    """)

    fun `test builtin spec functions in specs`() = doFirstCompletion("""
    module 0x1::M {
        spec module {
            ensures globa/*caret*/
        }
    }    
    """, """
    module 0x1::M {
        spec module {
            ensures global</*caret*/>()
        }
    }    
    """)

    fun `test autocomplete assert! in module`() = doSingleCompletion("""
    module 0x1::M {
        fun m() {
            ass/*caret*/
        }
    }    
    """, """
    module 0x1::M {
        fun m() {
            assert!(/*caret*/)
        }
    }    
    """)

    fun `test autocomplete assert! in script`() = doSingleCompletion("""
    script {
        fun m() {
            ass/*caret*/
        }
    }    
    """, """
    script {
        fun m() {
            assert!(/*caret*/)
        }
    }    
    """)

    private fun checkContainsBuiltins(@Language("Move") text: String) {
        val functionNames = BUILTIN_FUNCTIONS
        for (name in functionNames) {
            checkContainsCompletion(name, text)
        }
    }
}
