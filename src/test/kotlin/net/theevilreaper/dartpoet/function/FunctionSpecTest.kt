package net.theevilreaper.dartpoet.function

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.type.asTypeName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FunctionSpecTest {

    companion object {

        @JvmStatic
        private fun invalidParameters() = Stream.of(
            Arguments.of(
                "The name of a function can't be empty",
                { FunctionSpec.builder(" ").build() }
            ),
            Arguments.of(
                "An abstract method can't have a body",
                { FunctionSpec.builder("getName").modifier(DartModifier.ABSTRACT).addCode("%L", "value").build() }
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("invalidParameters")
    fun `test function creation which raise an exception due invalid data`(reason: String, parameter: () -> Unit) {
        val exception = assertThrows<IllegalArgumentException> { parameter() }
        Assertions.assertEquals(reason, exception.message)
    }

    @Test
    fun `test spec to builder conversation`() {
        val functionSpec = FunctionSpec.builder("getAmount")
            .returns(Int::class.asTypeName().copy(nullable = true))
            .async(false)
            .addCode("return %L", "10")
            .build()
        val specAsBuilder = functionSpec.toBuilder()
        assertEquals(functionSpec.name, specAsBuilder.name)
        assertEquals(functionSpec.returnType, specAsBuilder.returnType)
        assertFalse { specAsBuilder.async }
        assertTrue { specAsBuilder.returnType!!.isNullable }
        assertTrue { specAsBuilder.body.isNotEmpty() }
    }
}
