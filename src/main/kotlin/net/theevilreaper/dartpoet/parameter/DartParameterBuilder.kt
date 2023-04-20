package net.theevilreaper.dartpoet.parameter

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.code.CodeBlock
import net.theevilreaper.dartpoet.meta.SpecData
import net.theevilreaper.dartpoet.meta.SpecMethods

class DartParameterBuilder internal constructor(
    val name: String,
    val type: String,
): SpecMethods<DartParameterBuilder> {

    internal val specData: SpecData = SpecData()
    internal var named: Boolean = false
    internal var required: Boolean = false
    internal var nullable: Boolean = false
    internal var initializer: CodeBlock? = null


    fun initializer(format: String, vararg args: Any) = apply {
        initializer(CodeBlock.of(format, *args))
    }

    fun initializer(block: CodeBlock) = apply {
        this.initializer = block
    }

    fun named(named: Boolean) = apply {
        this.named = named
    }

    fun nullable(nullable: Boolean) = apply {
        this.nullable = nullable
    }

    fun required(required: Boolean) = apply {
        this.required = required
    }

    override fun annotations(annotations: Iterable<AnnotationSpec>) = apply {
        this.specData.annotations += annotations
    }

    override fun annotations(annotations: () -> Iterable<AnnotationSpec>) = apply {
        this.specData.annotations += annotations()
    }

    override fun annotation(annotation: () -> AnnotationSpec) = apply {
        this.specData.annotations += annotation()
    }

    override fun annotation(annotation: AnnotationSpec) = apply {
        this.specData.annotations += annotation
    }

    override fun modifier(modifier: DartModifier) = apply {
        this.specData.modifiers += modifier
    }

    override fun modifier(modifier: () -> DartModifier) = apply {
        this.specData.modifiers += modifier()
    }

    override fun modifiers(vararg modifiers: DartModifier) = apply {
        TODO("Not yet implemented")
    }

    override fun modifiers(modifiers: Iterable<DartModifier>) = apply {
        this.specData.modifiers += modifiers
    }

    override fun modifiers(modifiers: () -> Iterable<DartModifier>) = apply {
        this.specData.modifiers += modifiers()
    }

    fun build(): DartParameterSpec {
        return DartParameterSpec(this)
    }
}