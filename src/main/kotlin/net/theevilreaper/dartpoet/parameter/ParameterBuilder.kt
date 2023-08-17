package net.theevilreaper.dartpoet.parameter

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.code.CodeBlock
import net.theevilreaper.dartpoet.meta.SpecData
import net.theevilreaper.dartpoet.meta.SpecMethods

class ParameterBuilder internal constructor(
    val name: String,
    var type: String? = null,
): SpecMethods<ParameterBuilder> {

    internal val specData: SpecData = SpecData()
    internal var named: Boolean = false
    internal var required: Boolean = false
    internal var nullable: Boolean = false
    internal var initializer: CodeBlock? = null

    fun type(type: String) = apply {
        this.type = type
    }

    fun initializer(format: String, vararg args: Any) = apply {
        initializer(CodeBlock.of(format, *args))
    }

    fun initializer(block: CodeBlock) = apply {
        this.initializer = block
    }

    /**
     * Set's an indicator if the parameter should be named.
     * @param named the indicator to set
     * @return the builder instance
     */
    fun named(named: Boolean) = apply {
        this.named = named
    }

    /**
     * Set's an indicator if the parameter should be nullable.
     * @param nullable the indicator to set
     * @return the builder instance
     */
    fun nullable(nullable: Boolean) = apply {
        this.nullable = nullable
    }

    /**
     * Set's an indicator if the parameter is required for a function.
     * @param required the indicator to set
     * @return the builder instance
     */
    fun required(required: Boolean) = apply {
        this.required = required
    }

    /**
     * Add a [Annotation] to the builder.
     * @param annotation the annotation to add
     * @return the builder instance
     */
    override fun annotation(annotation: () -> AnnotationSpec) = apply {
        this.specData.annotations += annotation()
    }

    /**
     * Add a [Annotation] to the builder.
     * @param annotation the annotation to add
     * @return the builder instance
     */
    override fun annotation(annotation: AnnotationSpec) = apply {
        this.specData.annotations += annotation
    }

    /**
     * Add an aray of [Annotation] to the builder.
     * @param annotations the annotation to add
     * @return the builder instance
     */
    override fun annotations(vararg annotations: AnnotationSpec) = apply {
        this.specData.annotations(*annotations)
    }

    /**
     * Add a [DartModifier] to the builder.
     * @param modifier the modifier to add
     * @return the builder instance
     */
    override fun modifier(modifier: DartModifier) = apply {
        this.specData.modifiers += modifier
    }

    /**
     * Add a [DartModifier] to the builder over a lambda to the builder.
     * @param modifier the modifier to add
     * @return the builder instance
     */
    override fun modifier(modifier: () -> DartModifier) = apply {
        this.specData.modifiers += modifier()
    }

    /**
     * Add a [Array] of [DartModifier] to the builder.
     * @param modifiers the modifiers to add
     * @return the builder instance
     */
    override fun modifiers(vararg modifiers: DartModifier) = apply {
        this.specData.modifiers += modifiers
    }

    /**
     * Returns a new reference from the [ParameterSpec] with the given values from the builder instance.
     * @return the created [ParameterSpec] object reference
     */
    fun build(): ParameterSpec = ParameterSpec(this)
}