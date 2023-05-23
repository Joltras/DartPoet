package net.theevilreaper.dartpoet

import com.google.common.truth.Truth.*
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.clazz.DartClassSpec
import net.theevilreaper.dartpoet.code.buildCodeBlock
import net.theevilreaper.dartpoet.enum.EnumPropertySpec
import net.theevilreaper.dartpoet.function.DartFunctionSpec
import net.theevilreaper.dartpoet.function.constructor.ConstructorSpec
import net.theevilreaper.dartpoet.import.DartImport
import net.theevilreaper.dartpoet.import.ImportCastType
import net.theevilreaper.dartpoet.import.LibraryImport
import net.theevilreaper.dartpoet.import.PartImport
import net.theevilreaper.dartpoet.parameter.DartParameterSpec
import net.theevilreaper.dartpoet.property.DartPropertySpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class DartFileTest {

    @Test
    fun `test indent set`() {
        assertThrows(
            IllegalArgumentException::class.java,
            { DartFile.builder("Test").indent("") },
            "The indent can't be empty"
        )
        assertThrows(
            IllegalArgumentException::class.java,
            { DartFile.builder("Test").indent { "" } },
            "The indent can't be empty"
        )
    }

    @Test
    fun `write test model with freezed`() {
        val versionFreezedClass = DartClassSpec.builder("VersionModel")
            .withMixin("_${'$'}VersionModel")
            .annotation { AnnotationSpec.builder("freezed").build() }
            .constructor {
                ConstructorSpec.builder("VersionModel")
                    .asFactory(true)
                    .modifier(DartModifier.CONST)
                    .parameter {
                        DartParameterSpec.builder("version", "String")
                            .named(true)
                            .annotations {
                                listOf(
                                    AnnotationSpec.builder("JsonKey")
                                        .content("name: %C", "version").build(),
                                    AnnotationSpec.builder("Default")
                                        .content("%C", "1.0.0").build()
                                )
                            }
                            .build()
                    }
                    .build()
            }
            .constructor {
                ConstructorSpec.named("VersionModel", "fromJson")
                    .lambda(true)
                    .asFactory(true)
                    .parameter(
                        DartParameterSpec.builder("json", "Map<String, dynamic>").build()
                    )
                    .addCode("%L", "_${"$"}VersionModelFromJson(json);")
                    .build()
            }
        val versionFile = DartFile.builder("version.dart")
            .imports {
                listOf(
                    DartImport("freezed_annotation/freezed_annotation.dart"),
                    PartImport("version.freezed.dart"),
                    PartImport("version.g.dart")
                )
            }
            .type(
                versionFreezedClass
            )
            .build()
        assertThat(versionFile.toString()).isEqualTo(
            """
            import 'package:freezed_annotation/freezed_annotation.dart';

            part 'version.freezed.dart';
            part 'version.g.dart';
            
            @freezed
            class VersionModel with _${'$'}VersionModel {
            
              const factory VersionModel({
                @JsonKey(name: 'version')@Default('1.0.0') String version
              }) = _VersionModel;
            
              factory VersionModel.fromJson(Map<String, dynamic> json) =>
                  _${'$'}VersionModelFromJson(json);
            
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test library write`() {
        val libClass = DartFile.builder("testLib")
            .type(
                DartClassSpec.anonymousClassBuilder()
                    .endWithNewLine(true)
                    .function(
                        DartFunctionSpec.builder("JsonMap")
                            .typedef(true)
                            .returns("Map<String, dynamic>")
                            .build()

                    )
                    .build()
            )
            .imports {
                listOf(
                    DartImport("dart:html"),
                    LibraryImport("testLib"),
                    DartImport("dart:math", ImportCastType.AS, "math"),
                )
            }
            .build()
        assertThat(libClass.toString()).isEqualTo(
            """
            library testLib;
            
            import 'dart:html';
            import 'dart:math' as math;
            
            typedef JsonMap = Map<String, dynamic>;
            
            """.trimIndent()
        )
    }

    @Test
    fun `test enum class write`() {
        val enumClass = DartFile.builder("navigation_entry")
            .type(
                DartClassSpec.enumClass("NavigationEntry")
                    .properties(
                        DartPropertySpec.builder("name", "String")
                            .modifier { DartModifier.FINAL }.build(),
                        DartPropertySpec.builder("route", "String")
                            .modifier { DartModifier.FINAL }.build()

                    )
                    .enumProperties(
                        EnumPropertySpec.builder("dashboard")
                            .parameter("%C", "Dashboard")
                            .parameter("%C", "/dashboard")
                            .build(),
                        EnumPropertySpec.builder("build")
                            .parameter("%C", "Build")
                            .parameter("%C", "/build")
                            .build()
                    )
                    .constructor(
                        ConstructorSpec.builder("NavigationEntry")
                            .modifier(DartModifier.CONST)
                            .parameters(
                                DartParameterSpec.builder("name").build(),
                                DartParameterSpec.builder("route").build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()
        assertThat(enumClass.toString()).isEqualTo(
            """
            enum NavigationEntry {
            
              dashboard('Dashboard', '/dashboard'),
              build('Build', '/build');
            
              final String name;
              final String route;
            
              const NavigationEntry(this.name, this.route);
            
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test api handler write`() {
        val className = "DefectApi"
        val apiClient = "ApiClient"

        val handlerApiClass = DartClassSpec.builder(className)
            .property(DartPropertySpec.builder(apiClient.replaceFirstChar { it.lowercase() }, apiClient)
                .modifier { DartModifier.FINAL }
                .build()
            )
            .constructor(
                ConstructorSpec.builder(className)
                    .parameter(
                        DartParameterSpec.builder(apiClient.replaceFirstChar { it.lowercase() }, apiClient).build()
                    )
                    .addCode(buildCodeBlock {
                        add(
                            "%L = %L",
                            apiClient.replaceFirstChar { it.lowercase() },
                            apiClient.replaceFirstChar { it.lowercase() })
                    })
                    .build()
            )
            .function(
                DartFunctionSpec.builder("getByID")
                    .async(true)
                    .returns("DefectDTO")
                    .parameter(DartParameterSpec.builder("id", "int").build())
                    .addCode(buildCodeBlock {
                        addStatement("final queryParams = %L;", "<String, dynamic>{}")
                        addStatement("final baseUri = Uri.parse(apiClient.baseUrl);")
                        addStatement("final uri = baseUri.replace(queryParameters: queryParameters, path: '\${baseUri.path}/defect/\$id/');")
                        addStatement("return await apiClient.dio.getUri<JsonMap>(")
                        indent()
                        addStatement("uri,")
                        unindent()
                        addStatement(").then((response) {")
                        indent()
                        addStatement("return DefectDTO.from(response.data!);")
                        unindent()
                        addStatement("});")

                    })
                    .build()
            )
            .build()

        val file = DartFile.builder("${className}Handler")
            .import(LibraryImport("testLibrary", true))
            .type(handlerApiClass)
            .build()
        assertThat(file.toString()).isEqualTo(
            """
            part of testLibrary;
            
            class DefectApi {
            
              final ApiClient apiClient;
            
              DefectApi(ApiClient apiClient): apiClient = apiClient;
           
              Future<DefectDTO> getByID(int id) async {
                final queryParams = <String, dynamic>{};
                final baseUri = Uri.parse(apiClient.baseUrl);
                final uri = baseUri.replace(queryParameters: queryParameters, path: '${"$"}{baseUri.path}/defect/${"$"}id/');
                return await apiClient.dio.getUri<JsonMap>(
                  uri,
                ).then((response) {
                  return DefectDTO.from(response.data!);
                });
  
              }
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test model class write`() {
        val name = "HousePart"
        val serializer = "standardSerializers"
        val modelClass = DartClassSpec.abstractClass(name)
            .withImplements("Built<$name, ${name}Builder>")
            .function(
                DartFunctionSpec.builder("serializer")
                    .returns("Serializer<$name>")
                    .lambda(true)
                    .getter(true)
                    .modifier(DartModifier.STATIC)
                    .addCode("%L", "_\$${name}Serializer;")
                    .build()
            )
            .function(
                DartFunctionSpec.builder("fromJson")
                    .lambda(true)
                    .returns(name)
                    .modifier(DartModifier.STATIC)
                    .parameter(DartParameterSpec.builder("json", "dynamic").build())
                    .addCode(buildCodeBlock {
                        add("%L.deserialize(json);", serializer)
                    })
                    .build()
            )
            .function(
                DartFunctionSpec.builder("toJson")
                    .lambda(true)
                    .returns("dynamic")
                    .addCode(buildCodeBlock {
                        add("%L.serialize(this);", "standardSerializers")
                    })
                    .build()
            )
            .build()
        assertThat(modelClass.toString()).isEqualTo(
            """
            abstract class $name implements Built<$name, ${name}Builder> {
            
              static Serializer<$name> get serializer => _$${name}Serializer;
            
              static $name fromJson(dynamic json) => $serializer.deserialize(json);
            
              dynamic toJson() => $serializer.serialize(this);
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test class with comment`() {
        val clazz = DartFile.builder("test")
            .fileComment("Hallo")
            .fileComment("This is a [%L]", "Test")
            .type(
                DartClassSpec.builder("Test")
            )
            .build()
        assertThat(clazz.toString()).isEqualTo(
            """
            /// Hallo
            /// This is a [Test]
            class Test {}
            """.trimIndent()
        )
    }
}
