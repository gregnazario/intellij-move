package org.move.ide.inspections.unresolved_ref

import org.move.dove.project.model.doveProjectService
import org.move.ide.inspections.MoveUnresolvedReferenceInspection
import org.move.utils.tests.EnableCrossFileResolution
import org.move.utils.tests.annotation.InspectionsTestCase
import org.move.utils.tests.findAnnotationInstance

class CrossFileUnresolvedReferenceInspectionTest : InspectionsTestCase(MoveUnresolvedReferenceInspection::class) {
    override fun setUp() {
        super.setUp()

        // used to specify whether cross-module resolution should be enabled or not
        project.doveProjectService.testsCrossFileResolutionEnabled =
            findAnnotationInstance<EnableCrossFileResolution>() != null
    }

    fun `test no unresolved module member`() = checkByText("""
        script {
            use 0x1::MyModule::call;

            fun main() {
                call();
            }
        }
    """)

    @EnableCrossFileResolution
    fun `test unresolved module member cross file enabled`() = checkByText("""
        script {
            use 0x1::MyModule::call;

            fun main() {
                <error descr="Unresolved reference: `call`">call</error>();
            }
        }
    """)

    fun `test no unresolved reference to module`() = checkByText(
        """
        module M {
            fun main() {
                let t = Transaction::create();
            }
        }
    """
    )

    @EnableCrossFileResolution
    fun `test unresolved reference to module cross file enabled`() = checkByText(
        """
        module M {
            fun main() {
                let t = <error descr="Unresolved module reference: `Transaction`">Transaction</error>::create();
            }
        }
    """
    )

    fun `test no unresolved reference for fully qualified module`() = checkByText(
        """
        module M {
            fun main() {
                0x1::Debug::print(1);
            }
        }
    """
    )
}
