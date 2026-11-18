package com.lucasoliveira.bliss.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class LayerDependencyTest {

    @Test
    fun `domain does not depend on data`() {
        moduleFiles(DOMAIN_MODULE).assertFalse { it.importsFrom(DATA_PACKAGE) }
    }

    @Test
    fun `domain does not depend on presentation`() {
        moduleFiles(DOMAIN_MODULE).assertFalse { it.importsFrom(PRESENTATION_PACKAGE) }
    }

    @Test
    fun `domain does not depend on android framework`() {
        moduleFiles(DOMAIN_MODULE).assertFalse { it.importsFrom(ANDROID_PACKAGE) }
    }

    @Test
    fun `presentation does not depend on data`() {
        moduleFiles(PRESENTATION_MODULE).assertFalse { it.importsFrom(DATA_PACKAGE) }
    }

    @Test
    fun `data does not depend on presentation`() {
        moduleFiles(DATA_MODULE).assertFalse { it.importsFrom(PRESENTATION_PACKAGE) }
    }

    private fun moduleFiles(moduleName: String): List<KoFileDeclaration> =
        Konsist.scopeFromModule(moduleName).files

    private fun KoFileDeclaration.importsFrom(packagePrefix: String): Boolean =
        hasImport { it.name.startsWith(packagePrefix) }

    private companion object {
        const val DOMAIN_MODULE = "domain"
        const val DATA_MODULE = "data"
        const val PRESENTATION_MODULE = "presentation"
        const val DATA_PACKAGE = "com.lucasoliveira.bliss.data."
        const val PRESENTATION_PACKAGE = "com.lucasoliveira.bliss.presentation."
        const val ANDROID_PACKAGE = "android."
    }
}
