package net.theevilreaper.dartpoet.parameter

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.DartModifier.*
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.Exception
import java.util.stream.Stream
import kotlin.test.assertContentEquals

class ParameterSpecTest {

    companion object {

        @JvmStatic
        private fun invalidParameters() = Stream.of(
            Arguments.of(
                "Received invalid keywords [ABSTRACT]. Allowed keywords for parameters are [FINAL, REQUIRED, CONST]",
                { ParameterSpec.builder("test", String::class).modifier { ABSTRACT }.build() }
            ),
            Arguments.of(
                "When a parameter should be const no other modifiers are allowed",
                { ParameterSpec.builder("test", String::class).modifiers(CONST, FINAL).build() }
            ),
            Arguments.of(
                "The required keyword can't be used in combination with const",
                { ParameterSpec.builder("test", String::class).modifiers(REQUIRED, CONST).build() }
            )
        )
    }

    @ParameterizedTest
    @MethodSource("invalidParameters")
    fun `test parameter creation which raise an exception due invalid data`(reason: String, parameter: () -> Unit) {
        val exception = assertThrows<IllegalArgumentException> { parameter() }
        assertEquals(reason, exception.message)
    }

    @Test
    fun `test spec to builder conversation`() {
        val parameterSpec = ParameterSpec.builder("amount", Int::class)
            .nullable(true)
            .initializer("%L", "10")
            .annotation(AnnotationSpec.builder("nullable").build())
            .build()
        val specAsBuilder = parameterSpec.toBuilder()
        assertNotNull(parameterSpec)
        assertEquals(parameterSpec.name, specAsBuilder.name)
        assertEquals(parameterSpec.type, specAsBuilder.typeName)
        assertEquals(parameterSpec.isNullable, specAsBuilder.nullable)
        assertTrue { specAsBuilder.initializer!!.isNotEmpty() }
        assertContentEquals(parameterSpec.annotations, specAsBuilder.specData.annotations)
    }
}