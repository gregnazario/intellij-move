package org.move.lang.types

import org.move.utils.tests.types.TypificationTestCase

class ExpressionTypesTest: TypificationTestCase() {
    fun `test add expr`() = testExpr("""
    script {
        fun main() {
            (1u8 + 1u8);
          //^ u8
        }
    }    
    """)

    fun `test sub expr`() = testExpr("""
    script {
        fun main() {
            (1u8 - 1u8);
          //^ u8
        }
    }    
    """)

    fun `test mul expr`() = testExpr("""
    script {
        fun main() {
            (1u8 * 1u8);
          //^ u8
        }
    }    
    """)

    fun `test div expr`() = testExpr("""
    script {
        fun main() {
            (1u8 / 1u8);
          //^ u8
        }
    }    
    """)

    fun `test mod expr`() = testExpr("""
    script {
        fun main() {
            (1u8 % 10);
          //^ u8
        }
    }    
    """)

    fun `test bang expr`() = testExpr("""
    script {
        fun main() {
            !true;
          //^ bool
        }
    }    
    """)

    fun `test less expr`() = testExpr("""
    script {
        fun main() {
            (1 < 1);
          //^ bool
        }
    }    
    """)

    fun `test less equal expr`() = testExpr("""
    script {
        fun main() {
            (1 <= 1);
          //^ bool
        }
    }    
    """)

    fun `test greater expr`() = testExpr("""
    script {
        fun main() {
            (1 > 1);
          //^ bool
        }
    }    
    """)

    fun `test greater equal expr`() = testExpr("""
    script {
        fun main() {
            (1 >= 1);
          //^ bool
        }
    }    
    """)

    fun `test cast expr`() = testExpr("""
    script {
        fun main() {
            (1 as u8);
          //^ u8
        }
    }    
    """)

    fun `test copy expr`() = testExpr("""
    script {
        fun main() {
            copy 1u8;
          //^ u8
        }
    }    
    """)

    fun `test move expr`() = testExpr("""
    script {
        fun main() {
            move 1u8;
          //^ u8
        }
    }    
    """)

    fun `test struct literal expr with unresolved type param`() = testExpr("""
    module 0x1::M {
        struct R<CoinType> {}
        fun main() {
            R {};
          //^ 0x1::M::R<?CoinType>  
        }
    }
    """)

    fun `test borrow expr`() = testExpr("""
    module 0x1::M {
        fun main(s: signer) {
            &s;
          //^ &signer 
        }
    }    
    """)

    fun `test mutable borrow expr`() = testExpr("""
    module 0x1::M {
        fun main(s: signer) {
            &mut s;
          //^ &mut signer 
        }
    }    
    """)

    fun `test deref expr`() = testExpr("""
    module 0x1::M {
        fun main(s: &signer) {
            *s;
          //^ signer 
        }
    }    
    """)

    fun `test dot access to primitive field`() = testExpr("""
    module 0x1::M {
        struct S { addr: address }
        fun main() {
            let s = S { addr: @0x1 };
            ((&s).addr);
          //^ address 
        }
    }    
    """)

    fun `test dot access to field with struct type`() = testExpr("""
    module 0x1::M {
        struct Addr {}
        struct S { addr: Addr }
        fun main() {
            let s = S { addr: Addr {} };
            ((&s).addr);
          //^ 0x1::M::Addr 
        }
    }    
    """)

    fun `test borrow expr of dot access`() = testExpr("""
    module 0x1::M {
        struct Addr {}
        struct S { addr: Addr }
        fun main() {
            let s = S { addr: Addr {} };
            &mut s.addr;
          //^ &mut 0x1::M::Addr 
        }
    }    
    """)

    fun `test add expr with untyped and typed integer`() = testExpr("""
    module 0x1::M {
        fun main() {
            (1 + 1u8);
          //^ u8  
        }
    }    
    """)

    fun `test add expr with untyped and typed integer reversed`() = testExpr("""
    module 0x1::M {
        fun main() {
            (1u8 + 1);
          //^ u8  
        }
    }    
    """)

    fun `test struct field as vector`() = testExpr("""
    module 0x1::M {
        struct NFT {}
        struct Collection { nfts: vector<NFT> }
        fun m(coll: Collection) {
            (coll.nfts);
          //^ vector<0x1::M::NFT>  
        }
    }    
    """)

    fun `test if expr`() = testExpr("""
    module 0x1::M {
        fun m() {
            let a = if (true) 1 else 2;
            a;
          //^ integer  
        }
    }    
    """)

    fun `test if expr without else`() = testExpr("""
    module 0x1::M {
        fun m() {
            let a = if (true) 1;
            a;
          //^ <unknown>  
        }
    }    
    """)

    fun `test if expr with incompatible else`() = testExpr("""
    module 0x1::M {
        fun m() {
            let a = if (true) 1 else true;
            a;
          //^ <unknown>
        }
    }    
    """)

    fun `test return type of unit returning function`() = testExpr("""
    module 0x1::M {
        fun call(): () {}
        fun m() {
            call();
          //^ ()
        }
    }    
    """)

    fun `test if else with references coerced to less specific one`() = testExpr("""
    module 0x1::M {
        struct S {}
        fun m(s: &S, s_mut: &mut S) {
            (if (cond) s_mut else s);
          //^ &0x1::M::S  
        }
    }    
    """)

    fun `test x string`() = testExpr("""
    module 0x1::M {
        fun m() {
            x"1234";
            //^ vector<u8>
        }
    }    
    """)

    fun `test msl num`() = testExpr("""
    module 0x1::M {
        spec module {
            1;
          //^ num  
        }
    }    
    """)

    fun `test msl callable num`() = testExpr("""
    module 0x1::M {
        fun call(): u8 { 1 }
        spec module {
            call();
            //^ num
        }
    }    
    """)

    fun `test msl ref is type`() = testExpr("""
    module 0x1::M {
        struct S {}
        fun ref(): &S {}
        spec module {
            let a = ref();
            a;
          //^ 0x1::M::S
        }
    }    
    """)

    fun `test msl mut ref is type`() = testExpr("""
    module 0x1::M {
        struct S {}
        fun ref_mut(): &mut S {}
        spec module {
            let a = ref_mut();
            a;
          //^ 0x1::M::S
        }
    }    
    """)

    fun `test type of fun param in spec`() = testExpr("""
    module 0x1::M {
        fun call(addr: address) {}
        spec call {
            addr;
            //^ address
        }
    }    
    """)

    fun `test type of u8 fun param in spec`() = testExpr("""
    module 0x1::M {
        fun call(n: u8) {}
        spec call {
            n;
          //^ num  
        }
    }    
    """)

//    fun `test type of result variable in fun spec is return type`() = testExpr("""
//    module 0x1::M {
//        fun call(): address { @0x1 }
//        spec call {
//            result;
//            //^ address
//        }
//    }
//    """)

    fun `test old function type for spec`() = testExpr("""
    module 0x1::M {
        struct S {}
        fun call(a: S) {}
        spec call {
            old(a);
          //^ 0x1::M::S 
        }
    }    
    """)

    fun `test global function type for spec`() = testExpr("""
    module 0x1::M {
        struct S has key {}
        spec module {
            let a = global<S>(@0x1);
            a;
          //^ 0x1::M::S 
        }
    }    
    """)

    fun `test const int in spec`() = testExpr("""
    module 0x1::M {
        const MY_INT: u8 = 1;
        spec module {
            MY_INT;
            //^ num
        }
    }    
    """)

    fun `test schema field type`() = testExpr("""
    module 0x1::M {
        spec schema SS {
            val: num;
            val;
            //^ num
        }
    }    
    """)

    fun `test struct field vector_u8 in spec`() = testExpr("""
    module 0x1::M {
        struct S { vec: vector<u8> } 
        spec module {
            let s = S { vec: b"" };
            s.vec
            //^ vector<num>
        }
    }    
    """)
}
