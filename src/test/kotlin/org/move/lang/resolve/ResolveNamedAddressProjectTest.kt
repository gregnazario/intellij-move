package org.move.lang.resolve

import org.move.lang.core.psi.MvNamedAddress
import org.move.utils.tests.FileTreeBuilder
import org.move.utils.tests.resolve.ResolveProjectTestCase
import org.toml.lang.psi.TomlKeySegment

class ResolveNamedAddressProjectTest : ResolveProjectTestCase() {
    fun `test resolve named address to address`() = checkByFileTree {
        moveToml("""
        [addresses]
        Std = "0x1"
        #X    
        """)
        sources {
            move("main.move", """
            module Std::Module {}
                  //^
            """)
        }
    }

    fun `test resolve named address to toml key defined with placeholder`() = checkByFileTree {
        moveToml("""
        [addresses]
        Std = "_"
        #X    
        """)
        sources {
            move("main.move", """
            module Std::Module {}
                  //^
            """)
        }
    }

    fun `test resolve named address to dev address`() = checkByFileTree {
        moveToml("""
        [dev-addresses]
        Std = "0x1"
        #X    
        """)
        sources {
            move("main.move", """
            module Std::Module {}
                  //^
            """)
        }
    }

    fun `test resolve named address to toml key of the dependency`() = checkByFileTree {
        moveToml("""
        [dependencies]
        Stdlib = { local = "./stdlib", addr_subst = { "Std" = "0x1" } }    
        """)
        dir("stdlib") {
            moveToml("""
            [addresses]
            Std = "_"
            #X    
            """)
        }
        sources {
            move("main.move", """
            module Std::Module {}
                 //^     
            """)
        }
    }

    fun `test named address to renamed dep address`() = checkByFileTree {
        moveToml("""
        [dependencies]
        Stdlib = { local = "./stdlib", addr_subst = { "StdX" = "Std" } }
                                                       #X
        """)
        dir("stdlib") {
            moveToml("""
            [addresses]
            Std = "0x1"
            """)
        }
        sources {
            move("main.move", """
            module StdX::Module {}
                 //^     
            """)
        }
    }

    fun `test resolve address from dependency of dependency`() = checkByFileTree {
        moveToml(
            """
        [dependencies]
        PontStdlib = { local = "./pont-stdlib" }
        """
        )
        sources {
            move("main.move", """
            module 0x1::M {
                use Std::Reflect;
                    //^
            }     
            """)
        }
        dir("pont-stdlib", {
            moveToml("""
            [dependencies]
            MoveStdlib = { local = "./move-stdlib" }    
            """)
            dir("move-stdlib", {
                moveToml("""
                [addresses]
                Std = "0x1"    
                #X  
                """)
            })
        })
    }

    override fun checkByFileTree(fileTree: FileTreeBuilder.() -> Unit) {
        checkByFileTree(MvNamedAddress::class.java,
                        TomlKeySegment::class.java,
                        fileTree)
    }
}
