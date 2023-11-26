package net.theevilreaper.dartpoet.code.writer

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.function.FunctionSpec
import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import net.theevilreaper.dartpoet.code.CodeBlock
import net.theevilreaper.dartpoet.code.CodeWriter
import net.theevilreaper.dartpoet.code.buildCodeBlock
import net.theevilreaper.dartpoet.parameter.ParameterSpec
import net.theevilreaper.dartpoet.type.ClassName
import net.theevilreaper.dartpoet.type.ParameterizedTypeName.Companion.parameterizedBy
import net.theevilreaper.dartpoet.type.asClassName

class FunctionWriterTest {

    @Test
    fun `write void method`() {
        val method = FunctionSpec.builder("test")
            .returns(Void::class)
            .build()
        assertThat(method.toString()).isEqualTo("void test() { }")
    }

    @Test
    fun `write simple method without parameters`() {
        val writer = CodeWriter(StringBuilder())
        val method = FunctionSpec.builder("getName")
            .modifier(DartModifier.PUBLIC)
            .returns(String::class)
            .addCode("return %C;", "test")
            .build()
        writer.close()

        assertThat(method.toString()).isEqualTo(
            """
            String getName() {
              return 'test';
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test simple private method`() {
        val writer = CodeWriter(StringBuilder())
        val method = FunctionSpec.builder("name")
            .returns(String::class)
            .modifier(DartModifier.PRIVATE)
            .addCode("return %C;", "Tobi").build()
        writer.close()
        assertThat(method.toString()).isEqualTo(
            """
            String _name() {
              return 'Tobi';
            }
            """.trimIndent()
        )
    }

    @Test
    fun `write simple nullable function`() {
        val method = FunctionSpec.builder("getId")
            .returns(Int::class.asClassName().copy(nullable = true))
            .addCode("return %L;", 10).build()
        assertThat(method.toString()).isEqualTo(
            """
            int? getId() {
              return 10;
            }
            """.trimIndent()
        )
    }

    @Test
    fun `write another nullable method`() {
        val method = FunctionSpec.builder("getValue")
            .returns(Int::class.asClassName().copy(nullable = true))
            .addCode("return 1;")
            .build()
        assertThat(method.toString()).isEqualTo(
            """
            int? getValue() {
              return 1;
            }
            """.trimIndent()
        )
    }

    @Test
    fun `write simple async function`() {
        val method = FunctionSpec.builder("getNameById")
            .returns(String::class)
            .async(true)
            .parameter {
                ParameterSpec.builder("id", Int::class).build()
            }
            .addCode(
                CodeBlock.builder()
                    .add("return 'Thomas';")
                    .build()
            )
            .build()
        assertThat(method.toString()).isEqualTo(
            """
            Future<String> getNameById(int id) async {
              return 'Thomas';
            }
            """.trimIndent()
        )
    }

    @Test
    fun `write method with two parameters`() {
        val method = FunctionSpec.builder("getAllById")
            .returns(List::class.parameterizedBy(ClassName("Model")))
            .parameters {
                listOf(
                    ParameterSpec.builder("id", String::class).build(),
                    ParameterSpec.builder("amount", Int::class).build()
                )
            }
            .build()
        assertThat(method.toString()).isEqualTo(
            """
            List<Model> getAllById(String id, int amount) { }
            """.trimIndent()
        )
    }

    @Test
    fun `test typedef write`() {
        val function = FunctionSpec.builder("ValueUpdate<E>")
            .typedef(true)
            .parameter(
                ParameterSpec.builder("value", ClassName("E"))
                    .nullable(true)
                    .build()
            )
            .returns(ClassName("void Function"))
            .build()
        assertThat(function.toString()).isEqualTo("typedef ValueUpdate<E> = void Function(E? value);")
    }

    @Test
    fun `test cast write`() {
        val function = FunctionSpec.builder("getId")
            .returns(Int::class)
            .typeCast("int")
            .build()
        assertThat(function.toString()).isEqualTo("int getId<int>() { }")
    }

    @Test
    fun `test other getter variant write`() {
        val function = FunctionSpec.builder("value")
            .returns(Int::class)
            .getter(true)
            .addCode("%L", "_value;")
            .build()
        assertThat(function.toString()).isEqualTo("int get value => _value;");
    }

    @Test
    fun `test other setter variant write`() {
        val function = FunctionSpec.builder("value")
            .parameter(
                ParameterSpec.builder("value", Int::class).build()
            )
            .setter(true)
            .addCode(buildCodeBlock {
                add("%L = %L;", "_value", "value")
            })
            .build()
        assertThat(function.toString()).isEqualTo(
            """
            set value(int value) {
              _value = value;
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test lambda method write`() {
        val function = FunctionSpec.builder("isNoble")
            .lambda(true)
            .parameter(ParameterSpec.builder("atomicNumber", Int::class).build())
            .returns(Boolean::class)
            .addCode("_nobleGases[atomicNumber] != null;")
            .build()
        assertThat(function.toString()).isEqualTo("bool isNoble(int atomicNumber) => _nobleGases[atomicNumber] != null;")
    }

    @Test
    fun `test method with documentation`() {
        val function = FunctionSpec.builder("getName")
            .returns(String::class)
            .addCode("return %C;", "Test")
            .doc("Returns the name from an object")
            .doc("For generation tests it returns 'Test'")
            .build()
        assertThat(function.toString()).isEqualTo(
            """
            /// Returns the name from an object
            /// For generation tests it returns 'Test'
            String getName() {
              return 'Test';
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test method write with required parameters`() {
        val method = FunctionSpec
            .builder("apply")
            .modifier { DartModifier.ABSTRACT }
            .parameters {
                listOf(
                    ParameterSpec.builder("format", String::class).modifier { DartModifier.REQUIRED }.build(),
                    ParameterSpec.builder("time", Int::class).modifier { DartModifier.REQUIRED }.build()
                )
            }.build()
        assertThat(method.toString()).isEqualTo(
            """
            abstract void apply({required String format, required int time});
            """.trimIndent()
        )
    }

    @Test
    fun `test method parameter with normal and some with a default value`() {
        val method = FunctionSpec
            .builder("setData")
            .parameters {
                listOf(
                    ParameterSpec.builder("name", String::class).build(),
                    ParameterSpec.builder("city", String::class).build(),
                    ParameterSpec.builder("age", Int::class).initializer("%L", 10).build()
                )
            }
            .build()
        assertThat(method.toString()).isEqualTo(
            """
            void setData(String name, String city, [int age = 10]) { }
            """.trimIndent()
        )
    }
}
