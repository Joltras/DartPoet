package net.theevilreaper.dartpoet.clazz

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.meta.SpecData
import net.theevilreaper.dartpoet.meta.SpecMethods
import net.theevilreaper.dartpoet.function.FunctionType
import net.theevilreaper.dartpoet.property.DartPropertySpec

//TODO: Add check to prevent illegal modifiers on some class combinations
class DartClassBuilder internal constructor(
    internal val name: String?,
    internal val classType: ClassType,
    vararg modifiers: DartModifier
) : SpecMethods<DartClassBuilder> {
    internal val classMetaData: SpecData = SpecData(*modifiers)
    internal val isAnonymousClass get() = name == null && classType == ClassType.CLASS
    internal val isEnumClass get() = classType == ClassType.CLASS && DartModifier.ENUM in classMetaData.modifiers
    internal val isMixinClass get() = classType == ClassType.MIXIN && DartModifier.MIXIN in classMetaData.modifiers
    internal val isAbstract get() = classType == ClassType.CLASS && DartModifier.ABSTRACT in classMetaData.modifiers
    internal val isLibrary get() = classType == ClassType.CLASS && DartModifier.LIBRARY in classMetaData.modifiers
    private val isNormalClass get() = classType == ClassType.CLASS && !isEnumClass && !isMixinClass && !isAbstract && !isLibrary

    private val propertyStack: MutableList<DartPropertySpec> = mutableListOf()
    internal val functionStack: MutableList<FunctionType> = mutableListOf()

    internal var includeMixinClass: String? = null
    internal var endWithNewLine = false

    fun includeMixing(className: String) = apply {
        this.includeMixinClass = className
        this.modifier { DartModifier.WITH }
    }

    fun endWithNewLine(endWithNewLine: Boolean) = apply {
        this.endWithNewLine = endWithNewLine
    }

    fun property(dartPropertySpec: DartPropertySpec) = apply {
        this.propertyStack += dartPropertySpec
    }

    fun property(dartPropertySpec: () -> DartPropertySpec) = apply {
        this.propertyStack += dartPropertySpec()
    }

    fun function(function: FunctionType) = apply {
        this.functionStack += function
    }

    fun function(function: () -> FunctionType) = apply {
        this.functionStack += function()
    }

    override fun annotations(annotations: Iterable<AnnotationSpec>) = apply {
        this.classMetaData.annotations(annotations)
    }

    override fun annotations(annotations: () -> Iterable<AnnotationSpec>) = apply {
        this.classMetaData.annotations(annotations)
    }

    override fun annotation(annotation: () -> AnnotationSpec) = apply {
        this.classMetaData.annotation(annotation)
    }

    override fun annotation(annotation: AnnotationSpec) = apply {
        this.classMetaData.annotation(annotation)
    }

    override fun modifier(modifier: DartModifier) = apply {
        this.classMetaData.modifier(modifier)
    }

    override fun modifier(modifier: () -> DartModifier) = apply {
        this.classMetaData.modifier(modifier)
    }

    override fun modifiers(vararg modifiers: DartModifier) = apply {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun modifiers(modifiers: Iterable<DartModifier>) = apply {
        this.classMetaData.modifiers(modifiers)
    }

    override fun modifiers(modifiers: () -> Iterable<DartModifier>) = apply {
        this.classMetaData.modifiers(modifiers)
    }

    fun build(): DartClassSpec {
        return DartClassSpec(this)
    }
}