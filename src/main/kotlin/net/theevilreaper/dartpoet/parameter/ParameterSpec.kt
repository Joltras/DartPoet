package net.theevilreaper.dartpoet.parameter

import net.theevilreaper.dartpoet.code.CodeWriter
import net.theevilreaper.dartpoet.code.buildCodeString
import net.theevilreaper.dartpoet.code.writer.ParameterWriter
import net.theevilreaper.dartpoet.util.toImmutableSet

/**
 * A ParameterSpec represents the structure of a parameter from the language Dart.
 * This class can be constructed over the [ParameterBuilder].
 * @author theEvilReaper
 * @since 1.0.0
 */
class ParameterSpec internal constructor(
    builder: ParameterBuilder
) {

    internal val name = builder.name
    internal val type = builder.type
    internal val isNamed = builder.named
    internal val isNullable = builder.nullable
    internal val isRequired = builder.required
    internal val initializer = builder.initializer
    internal val annotations = builder.specData.annotations.toImmutableSet()

    init {
        check(name.trim().isNotEmpty()) { "The name of a parameter can't be empty" }

        if (type != null) {
            check(type.trim().isNotEmpty()) { "The type can't be empty" }
        }
    }

    /**
     * Converts a given [ParameterSpec] instance into a [ParameterBuilder].
     * This method can be used to change attributes from an existing [ParameterSpec] object.
     * @return the created builder instance
     */
    fun toBuilder(): ParameterBuilder {
        val builder = ParameterBuilder(name, type)
        builder.named = isNamed
        builder.nullable = isNullable
        builder.required = isRequired
        return builder
    }

    /**
     * Triggers the write process to write the [ParameterSpec] into an [Appendable].
     * @param codeWriter the writer instance to apply to the parameter
     */
    internal fun write(codeWriter: CodeWriter) { ParameterWriter().write(this, codeWriter) }

    /**
     * Creates a textual representation from the [ParameterSpec] with the code format from dart.
     * @return the created text representation
      */
    override fun toString() = buildCodeString { write(this) }

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