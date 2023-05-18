package net.theevilreaper.dartpoet.clazz

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.InheritKeyword
import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.enum.EnumPropertySpec
import net.theevilreaper.dartpoet.function.DartFunctionSpec
import net.theevilreaper.dartpoet.meta.SpecData
import net.theevilreaper.dartpoet.meta.SpecMethods
import net.theevilreaper.dartpoet.function.constructor.ConstructorSpec
import net.theevilreaper.dartpoet.property.DartPropertySpec

//TODO: Add check to prevent illegal modifiers on some class combinations
class DartClassBuilder internal constructor(
    internal val name: String?,
    internal val classType: ClassType,
    vararg modifiers: DartModifier
) : SpecMethods<DartClassBuilder> {
    internal val classMetaData: SpecData = SpecData(*modifiers)
    internal val isAnonymousClass get() = name == null && classType == ClassType.CLASS
    internal val isEnumClass get() = classType == ClassType.ENUM
    internal val isMixinClass get() = classType == ClassType.MIXIN
    internal val isAbstract get() = classType == ClassType.ABSTRACT
    internal val isLibrary get() = classType == ClassType.CLASS
    private val isNormalClass get() = classType == ClassType.CLASS && !isEnumClass && !isMixinClass && !isAbstract && !isLibrary

    internal val constructorStack: MutableList<ConstructorSpec> = mutableListOf()
    internal val propertyStack: MutableList<DartPropertySpec> = mutableListOf()
    internal val functionStack: MutableList<DartFunctionSpec> = mutableListOf()
    internal val enumPropertyStack: MutableList<EnumPropertySpec> = mutableListOf()
    internal val constantStack: MutableSet<DartPropertySpec> = mutableSetOf()

    internal var superClass: String? = null
    internal var inheritKeyWord: InheritKeyword? = null
    internal var endWithNewLine = false

    fun withMixin(className: String) = apply {
        setClassName(className)
        this.inheritKeyWord = InheritKeyword.MIXIN
    }

    fun withExtends(className: String) = apply {
        setClassName(className)
        this.inheritKeyWord = InheritKeyword.EXTENDS
    }

    fun withImplements(className: String) = apply {
        setClassName(className)
        this.inheritKeyWord = InheritKeyword.IMPLEMENTS
    }

    /**
     * Add a constant [DartPropertySpec] to the file.
     * @param constant the property to add
     */
    fun constant(constant: DartPropertySpec) = apply {
        this.constantStack += constant
    }

    /**
     * Add an array of constant [DartPropertySpec] to the file.
     * @param constants the array to add
     */
    fun constants(vararg constants: DartPropertySpec) = apply {
        this.constantStack += constants
    }

    fun enumProperty(enumPropertySpec: EnumPropertySpec) = apply {
        this.enumPropertyStack += enumPropertySpec
    }

    fun enumProperties(vararg properties: EnumPropertySpec) = apply {
        this.enumPropertyStack += properties
    }

    private fun setClassName(className: String) {
        this.superClass = className
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

    fun properties(vararg properties: DartPropertySpec) = apply {
        this.propertyStack += properties
    }

    fun function(function: DartFunctionSpec) = apply {
        this.functionStack += function
    }

    fun function(function: () -> DartFunctionSpec) = apply {
        this.functionStack += function()
    }

    fun constructor(constructor: ConstructorSpec) = apply {
        this.constructorStack += constructor
    }

    fun constructor(constructor: () -> ConstructorSpec) = apply {
        this.constructorStack += constructor()
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
