package net.theevilreaper.dartpoet.property

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.code.CodeBlock

/**
 * The builder is used to set all values that describe a property in Dart.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
class DartPropertyBuilder internal constructor(
    var name: String,
    var type: String,
) {
    internal var nullable = false
    internal val modifiers: MutableSet<DartModifier> = mutableSetOf()
    internal val annotations: MutableList<AnnotationSpec> = mutableListOf()
    internal var initBlock: CodeBlock.Builder = CodeBlock.builder()

    /**
     * Apply a given format which contains the parts for the init block of the [DartPropertySpec].
     * @param format the given format
     * @param args the arguments for the format
     */
    fun initWith(format: String, vararg args: Any?): DartPropertyBuilder = apply {
        this.initBlock.add(format, *args)
    }

    /**
     * Set the initializer block directly as [CodeBlock.Builder] to the property.
     * @param codeFragment the [CodeBlock.Builder] to set
     */
    fun initWith(codeFragment: CodeBlock.Builder): DartPropertyBuilder = apply {
        this.initBlock = codeFragment
    }

    /**
     * Set if the property should be nullable or not. A property which is nullable in Dart contains a '?' after the type.
     * @param nullable if the property should be nullable or not
     */
    fun nullable(nullable: Boolean): DartPropertyBuilder {
        this.nullable = nullable
        return this
    }

    /**
     * Add a [Iterable] of [AnnotationSpec] to the property.
     * @param annotations the annotations to add
     */
    fun annotations(annotations: Iterable<AnnotationSpec>): DartPropertyBuilder = apply {
        this.annotations += annotations
    }

    /**
     * Add a [Iterable] of [AnnotationSpec] to the property.
     * @param annotations the annotations to add
     */
    fun annotations(annotations: () -> Iterable<AnnotationSpec>): DartPropertyBuilder = apply {
        this.annotations += annotations()
    }

    /**
     * Add a single [AnnotationSpec] to the property.
     * @param annotation the annotation to add
     */
    fun annotation(annotation: () -> AnnotationSpec): DartPropertyBuilder = apply {
        this.annotations += annotation()
    }

    /**
     * Add a single [AnnotationSpec] to the property.
     * @param annotation the annotation to add
     */
    fun annotation(annotation: AnnotationSpec): DartPropertyBuilder = apply {
        this.annotations += annotation
    }

    /**
     * Add a new [DartModifier] to the property.
     * @param modifier the modifier to add
     */
    fun modifier(modifier: DartModifier): DartPropertyBuilder = apply {
        this.modifiers += modifier
    }

    /**
     * Add a new [DartModifier] to the property.
     * @param modifier the modifier to add
     */
    fun modifier(modifier: () -> DartModifier): DartPropertyBuilder = apply {
        this.modifiers += modifier()
    }

    /**
     * Add a [Iterable] of [DartModifier] to the property.
     * @param modifiers the modifiers to add
     */
    fun modifiers(modifiers: Iterable<DartModifier>): DartPropertyBuilder = apply {
        this.modifiers += modifiers
    }

    /**
     * Add a [Iterable] of [DartModifier] to the property.
     * @param modifiers the modifiers to add
     */
    fun modifiers(modifiers: () -> Iterable<DartModifier>): DartPropertyBuilder = apply {
        this.modifiers += modifiers()
    }

    /**
     * Creates a new reference from the [DartPropertySpec] with the given builder reference.
     * @return the created [DartPropertySpec] instance
     */
    fun build(): DartPropertySpec {
        return DartPropertySpec(this)
    }
}
