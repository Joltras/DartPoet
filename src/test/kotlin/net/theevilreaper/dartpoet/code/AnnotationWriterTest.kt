package net.theevilreaper.dartpoet.code

import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AnnotationWriterTest {

    @Test
    fun `test annotation write without content`() {
        val annotationSpec = AnnotationSpec.builder("jsonIgnore").build()
        assertEquals("@jsonIgnore", annotationSpec.toString())
    }

    @Test
    fun `test annotation write with content`() {
        val annotationSpec = AnnotationSpec.builder("JsonKey")
            .content("name = %L", "Test").build()

        assertEquals("@JsonKey(name: \"Test\")", annotationSpec.toString())
    }
}