package net.theevilreaper.dartpoet.code

import net.theevilreaper.dartpoet.annotation.AnnotationSpec
import net.theevilreaper.dartpoet.extension.ExtensionSpec
import net.theevilreaper.dartpoet.function.FunctionSpec
import net.theevilreaper.dartpoet.function.constructor.ConstructorSpec
import net.theevilreaper.dartpoet.directive.Directive
import net.theevilreaper.dartpoet.parameter.ParameterSpec
import net.theevilreaper.dartpoet.property.PropertySpec
import net.theevilreaper.dartpoet.util.CURLY_CLOSE
import net.theevilreaper.dartpoet.util.CURLY_OPEN
import net.theevilreaper.dartpoet.util.EMPTY_STRING
import net.theevilreaper.dartpoet.util.NEW_LINE
import net.theevilreaper.dartpoet.util.SPACE

internal val NO_ARG_PLACEHOLDERS = arrayOf('%', '⇥', '⇤', '«', '»').toCharArray()
internal val NO_ARG_PLACEHOLDERS_STRING = setOf("⇥", "⇤", "«", "»")
internal val SPECIAL_CHARACTERS = " \n·".toCharArray()
internal val UNSAFE_LINE_START = Regex("\\s*[-+].*")

fun String.withOpenBrackets(): String {

    for (i in length - 1 downTo 0) {
        if (this[i] == CURLY_OPEN) {
            return "$this$NEW_LINE"
        } else if (this[i] == CURLY_CLOSE) {
            break
        }
    }
    return "$this $CURLY_OPEN"
}

val Char.isSingleCharNoArgPlaceholder get() = this in NO_ARG_PLACEHOLDERS
val Char.isMultiCharNoArgPlaceholder get() = this == '%'


internal val String.isPlaceholder
    get() = (length == 1 && first().isSingleCharNoArgPlaceholder) ||
            (length == 2 && first().isMultiCharNoArgPlaceholder)
fun String.nextPotentialPlaceholderPosition(startIndex: Int) =
    indexOfAny(NO_ARG_PLACEHOLDERS, startIndex)

internal fun Set<FunctionSpec>.emitFunctions(
    codeWriter: CodeWriter,
    emitBlock: (FunctionSpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (isNotEmpty()) {
        val newLines = size > 1
        forEachIndexed { index, functionSpec ->
            if (index > 0) {
                emit(NEW_LINE)
            }
            emitBlock(functionSpec)
            if (newLines && index < size - 1) {
                emit(NEW_LINE)
            }
        }
    }
}

internal fun Set<AnnotationSpec>.emitAnnotations(
    codeWriter: CodeWriter,
    inLineAnnotations: Boolean = true,
    endWithNewLine: Boolean = true,
    emitBlock: (AnnotationSpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (isNotEmpty()) {
        forEachIndexed { index, annotation ->
            if (index > 0) {
                codeWriter.emit(if (inLineAnnotations) EMPTY_STRING else NEW_LINE)
            }
            emitBlock(annotation)
        }

        if (endWithNewLine) {
            emit(NEW_LINE)
        } else {
            emit(SPACE)
        }
    }
}

internal fun Set<ConstructorSpec>.emitConstructors(
    codeWriter: CodeWriter,
    forceNewLines: Boolean = false,
    leadingNewLine: Boolean = false,
    emitBlock: (ConstructorSpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (isNotEmpty()) {
        if (leadingNewLine) {
            codeWriter.emit(NEW_LINE)
        }
        forEachIndexed { index, constructorSpec ->
            val emitNewLines = size >= 1 || forceNewLines

            if (index > 0 && emitNewLines) {
                codeWriter.emit(NEW_LINE)
            }

            emitBlock(constructorSpec)

            if (emitNewLines) {
                codeWriter.emit(NEW_LINE)
            }
        }
    }
}

internal fun List<ParameterSpec>.emitParameters(
    codeWriter: CodeWriter,
    forceNewLines: Boolean = false,
    emitBrackets: Boolean = true,
    emitSpace: Boolean = true,
    emitBlock: (ParameterSpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (emitBrackets) {
        emit("(")
    }
    if (isNotEmpty()) {
        val emitComma = size > 1
        forEachIndexed { index, parameter ->
            if (index > 0 && forceNewLines)  {
                emit(NEW_LINE)
            }

            emitBlock(parameter)
            if (emitComma) {
                if (index < size - 1) {
                    emit(",")
                }
                if (emitSpace && index < size - 1) {
                    emit(SPACE)
                }
            }
        }
    }
    if (emitBrackets) {
        emit(")")
    }
}

internal fun List<ExtensionSpec>.emitExtensions(
    codeWriter: CodeWriter,
    forceNewLines: Boolean = false,
    emitBlock: (ExtensionSpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (isNotEmpty()) {
        val emitNewLines = size > 1 || forceNewLines

        forEachIndexed { index, parameter ->
            if (index > 0 && emitNewLines)  {
                emit(NEW_LINE)
            }
            emitBlock(parameter)
        }

        if (emitNewLines) {
            codeWriter.emit(NEW_LINE)
        }
    }
}

internal fun <T: Directive> List<T>.writeImports(
    writer: CodeBlock.Builder,
    newLineAtEnd: Boolean = false,
    emitBlock: (T) -> String = { it.toString() }
) {
    if (isNotEmpty()) {
        forEach { import ->
            writer.addStatement(emitBlock(import))
        }
    }

    if (newLineAtEnd) {
        writer.add(NEW_LINE)
    }
}

/**
 * Represents an extension function for a [Set] which contains [PropertySpec] to write them all into a
 * [CodeWriter] reference.
 * @param codeWriter the writer instance to apply the code parts
 * @param forceNewLines indicates if each generation should end with an empty lines
 * @param emitBlock the lambda block which holds the emit logic for the properties
 */
fun Set<PropertySpec>.emitProperties(
    codeWriter: CodeWriter,
    forceNewLines: Boolean = false,
    emitBlock: (PropertySpec) -> Unit = { it.write(codeWriter) }
) = with(codeWriter) {
    if (isNotEmpty()) {
        val emitNewLines = size > 1 || forceNewLines

        forEachIndexed { index, property ->
            if (index > 0) {
                emit(if (emitNewLines) NEW_LINE else EMPTY_STRING)
            }
            emitBlock(property)
        }
        emit(NEW_LINE)
    }
}
