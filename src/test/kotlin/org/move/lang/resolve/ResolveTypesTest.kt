package org.move.lang.resolve

import org.move.utils.tests.resolve.ResolveTestCase

class ResolveTypesTest : ResolveTestCase() {
    fun `test resolve struct as function param type`() = checkByCode(
        """
        module M {
            struct MyStruct {}
                 //X
            
            fun call(s: MyStruct) {}
                      //^
        }
    """
    )

    fun `test resolve struct as return type`() = checkByCode(
        """
        module M {
            struct MyStruct {}
                 //X
            
            fun call(): MyStruct {}
                      //^
        }
    """
    )

    fun `test resolve struct as acquires type`() = checkByCode(
        """
        module M {
            struct MyStruct {}
                 //X
            
            fun call() acquires MyStruct {}
                              //^
        }
    """
    )

    fun `test resolve struct as struct literal`() = checkByCode(
        """
        module M {
            struct MyStruct {}
                 //X
            
            fun call() {
                let a = MyStruct {};
                      //^
            }
        }
    """
    )

    fun `test resolve struct as struct pattern destructuring`() = checkByCode(
        """
        module M {
            struct MyStruct { val: u8 }
                 //X
            
            fun call() {
                let MyStruct { val } = get_struct();
                  //^
            }
        }
    """
    )

    fun `test resolve struct as type param`() = checkByCode(
        """
        module M {
            resource struct MyStruct {}
                          //X
            
            fun call() {
                let a = move_from<MyStruct>();
                                //^
            }
        }
    """
    )

    fun `test resolve struct type param`() = checkByCode(
        """
        module M {
            struct MyStruct<T> {
                          //X
                val: T
                   //^
            }
        }
    """
    )

    fun `test resolve struct type param inside vector`() = checkByCode(
        """
        module M {
            struct MyStruct<T> {
                          //X
                val: vector<T>
                          //^
            }
        }
    """
    )

    fun `test resolve struct type to struct`() = checkByCode(
        """
        module M {
            struct Native {}
                 //X
            fun main(n: Native): u8 {}
                      //^
        }
    """
    )

    fun `test resolve struct type with generics`() = checkByCode(
        """
        module M {
            struct Native<T> {}
                 //X
            fun main(n: Native<u8>): u8 {}
                      //^
        }
    """
    )

    fun `test pass native struct to native fun`() = checkByCode(
        """
        module M {
            native struct Native<T>;
                        //X
            native fun main(n: Native<u8>): u8;
                             //^
        }
    """
    )

//    fun `test resolve type to import`() = checkByCode(
//        """
//        script {
//            use 0x1::Transaction::Sender;
//                                //X
//
//            fun main(s: Sender) {}
//                      //^
//        }
//    """
//    )

    fun `test resolve type from import`() = checkByCode(
        """
        address 0x1 {
            module Transaction {
                struct Sender {}
                     //X
            }
        }
        script {
            use 0x1::Transaction::Sender;
                                //^
        }
    """
    )

    fun `test resolve type from usage`() = checkByCode(
        """
        address 0x1 {
            module Transaction {
                struct Sender {}
                     //X
            }
        }
        script {
            use 0x1::Transaction::Sender;

            fun main(n: Sender) {}
                      //^
        }
    """
    )

    fun `test resolve type to alias`() = checkByCode(
        """
        module M {
            use 0x1::Transaction::Sender as MySender;
                                          //X
            fun main(n: MySender) {}
                      //^
        }
    """
    )

    fun `test resolve return type to alias`() = checkByCode(
        """
        module M {
            use 0x1::Transaction::Sender as MySender;
                                          //X
            fun main(): MySender {}
                      //^
        }
    """
    )

    fun `test function return type to type param`() = checkByCode(
        """
        module M {
            fun main<Token>()
                   //X
                : Token {}
                //^
        }
    """
    )

    fun `test function return type param to type param`() = checkByCode(
        """
        module M {
            struct Coin<Token> {}
            
            fun main<Token>()
                   //X
                    : Coin<Token> {}
                         //^
        }
    """
    )

    fun `test native function return type param to type param`() = checkByCode(
        """
        module M {
            struct Coin<Token> {}
            
            native fun main<Token>()
                          //X
                    : Coin<Token>;
                         //^
        }
    """
    )

    fun `test struct unresolved in name expr`() = checkByCode("""
        address 0x1 {
            module A {
                struct S {}
            }
            module B {
                use 0x1::A;
                fun call() {
                    A::S
                     //^ unresolved                   
                }
            }
        }
    """)

    fun `test resolve type param in native function in spec`() = checkByCode("""
    module 0x1::M {
        spec module {
            /// Native function which is defined in the prover's prelude.
            native fun serialize<MoveValue>(
                                    //X
                v: &MoveValue
                    //^
            ): vector<u8>;
        }
    }    
    """)
}
