package org.move.ide.inspections

import org.move.utils.tests.annotation.InspectionsTestCase

class PhantomTypeParameterInspectionTest: InspectionsTestCase(PhantomTypeParameterInspection::class) {
    fun `test no inspection if type parameter is used`() = checkErrors("""
    module 0x1::M {
        struct S<T> {
            val: T
        }
    }    
    """)

    fun `test no inspection if type parameter unused but already phantom`() = checkErrors("""
    module 0x1::M {
        struct S<phantom T> {
            val: u8
        }
    }    
    """)

    fun `test show error with quickfix if not used in any field`() = checkFixByText("Declare phantom", """
    module 0x1::M {
        struct S<<error descr="Unused type parameter. Consider declaring it as phantom">/*caret*/T</error>> { val: u8 }
    }    
    """, """
    module 0x1::M {
        struct S<phantom /*caret*/T> { val: u8 }
    }    
    """)

//    fun `test show error with quickfix if used only in phantom positions`() = checkFixByText("Declare phantom", """
//    module 0x1::M {
//        struct R<phantom RR> {}
//        struct S<<warning descr="Unused type parameter. Consider declaring it as phantom">/*caret*/T</warning>> {
//            val: R<T>
//        }
//    }
//    """, """
//    module 0x1::M {
//        struct R<phantom RR> {}
//        struct S<phantom T> {
//            val: R<T>
//        }
//    }
//    """)
}
