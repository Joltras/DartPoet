package net.theevilreaper.dartpoet.parameter

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.code.CodeWriter
import net.theevilreaper.dartpoet.code.buildCodeString
import net.theevilreaper.dartpoet.code.writer.ParameterWriter
import net.theevilreaper.dartpoet.keyword.ALLOWED_PARAMETER_KEY_WORDS
import net.theevilreaper.dartpoet.keyword.hasAllowedParameterKeys
import net.theevilreaper.dartpoet.util.toImmutableSet

class ParameterSpec internal constructor(
    builder: ParameterBuilder
) {
    internal val name = builder.name
    internal val type = builder.type
    internal val isNamed = builder.named
    internal val isNullable = builder.nullable
    internal val initializer = builder.initializer
    internal val modifiers = builder.specData.modifiers.toImmutableSet()
    internal val annotations = builder.specData.annotations.toImmutableSet()
    internal val isRequired = modifiers.contains(DartModifier.REQUIRED)

    init {
        check(name.trim().isNotEmpty()) { "The name of a parameter can't be empty" }

        if (hasAllowedParameterKeys(this.modifiers)) {
            val forbiddenModifiers = this.modifiers.filter { it !in ALLOWED_PARAMETER_KEY_WORDS }.toSet()
            throw IllegalArgumentException("Received invalid keywords $forbiddenModifiers. Allowed keywords for parameters are $ALLOWED_PARAMETER_KEY_WORDS")
        }
        if (type != null) {
            check(type.trim().isNotEmpty()) { "The type can't be empty" }
        }
    }

    internal fun write(codeWriter: CodeWriter) {
        ParameterWriter().write(this, codeWriter)
    }

    override fun toString() = buildCodeString { write(this) }

    /**
     * Creates a new [ParameterBuilder] reference from an existing [ParameterSpec] object.
     * @return the created [ParameterBuilder] instance
     */
    fun toBuilder(): ParameterBuilder {
        val builder = ParameterBuilder(this.name, this.type)
        builder.named = isNamed
        builder.nullable = isNullable
        builder.annotations(*this.annotations.toTypedArray())
        builder.initializer = initializer
        return builder
    }

    companion object {

        /**
         * Creates a new instance from the [ParameterBuilder] with the given name.
         * @return the created builder instance
         */
        @JvmStatic
        fun builder(name: String) = ParameterBuilder(name)

        /**
         * Creates a new instance from the [ParameterBuilder] with the given values.
         * @return the created builder instance
         */
        @JvmStatic
        fun builder(name: String, type: String) = ParameterBuilder(name, type)
    }
}