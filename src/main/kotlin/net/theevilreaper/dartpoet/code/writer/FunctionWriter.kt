package net.theevilreaper.dartpoet.code.writer

import net.theevilreaper.dartpoet.DartModifier.*
import net.theevilreaper.dartpoet.code.CodeBlock
import net.theevilreaper.dartpoet.code.CodeWriter
import net.theevilreaper.dartpoet.code.emitParameters
import net.theevilreaper.dartpoet.function.FunctionSpec
import net.theevilreaper.dartpoet.util.SEMICOLON
import net.theevilreaper.dartpoet.util.toImmutableSet

class FunctionWriter {

    fun emit(functionSpec: FunctionSpec, writer: CodeWriter) {
        if (functionSpec.hasDocs) {
            functionSpec.docs.forEach { writer.emitDoc(it) }
        }
        if (functionSpec.isTypeDef) {
            writeTypeDef(functionSpec, writer)
            return
        }

        val writeableModifiers = functionSpec.modifiers.filter { it != PRIVATE && it != PUBLIC }.toImmutableSet()
        if (writeableModifiers.isNotEmpty()) {
            for (modifier in writeableModifiers) {
                writer.emit(modifier.identifier)
                writer.emit("·")
            }
        }

        if (functionSpec.returnType == null) {
            if (functionSpec.isAsync) {
                writer.emitCode("Future<%L>", VOID.identifier)
            } else {
                if (functionSpec.asSetter) {
                    writer.emitCode("set·")
                } else {
                    writer.emit("${VOID.identifier}·")
                }
            }
        } else {
            if (functionSpec.isAsync) {
                writer.emit("Future<")
            }
            writer.emitCode("%T", functionSpec.returnType)

            if (functionSpec.isGetter) {
                writer.emit("·get")
            }

            if (functionSpec.isAsync) {
                writer.emit(">")
            }
            writer.emit("·")
        }
        writer.emit("${if (functionSpec.isPrivate) PRIVATE.identifier else ""}${functionSpec.name}")

        if (functionSpec.typeCast.orEmpty().trim().isNotEmpty()) {
            writer.emitCode("<%L>", functionSpec.typeCast)
        }

        if (functionSpec.isGetter) {
            writer.emit("·=>·")
            writer.emitCode(functionSpec.body.returnsWithoutLinebreak(), ensureTrailingNewline = false)
            return;
        }

        if (functionSpec.parameters.isEmpty()) {
            writer.emit("()")
        } else {
            emitParameters(functionSpec, writer)
        }
        writeBody(functionSpec, writer)
    }

    private fun emitParameters(spec: FunctionSpec, writer: CodeWriter) {
        val hasAdditionalParameters = spec.namedParameters.isEmpty() && spec.parameterWithDefaults.isEmpty()

        spec.normalParameters.emitParameters(writer, openBracket = "(", closingBracket = ")", emitCloseBrackets = hasAdditionalParameters)

        if (!hasAdditionalParameters) {
            writer.emit(", ")
        }

        if (spec.namedParameters.isNotEmpty()) {
            spec.namedParameters.emitParameters(writer, openBracket = "{", closingBracket = "})")
        }

        if (spec.parameterWithDefaults.isNotEmpty()) {
            spec.parameterWithDefaults.emitParameters(writer, openBracket = "[", closingBracket = "])")
        }
    }

    private fun writeBody(spec: FunctionSpec, writer: CodeWriter) {
        if (spec.body.isEmpty()) {
            writer.emit(if (ABSTRACT in spec.modifiers) ";" else " { }")
            return
        }
        if (spec.isAsync) {
            writer.emit("·${ASYNC.identifier}")
        }
        if (spec.isLambda) {
            writer.emit("·=>·")
        } else {
            writer.emit("·{\n")
            writer.indent()
        }
        writer.emitCode(spec.body.returnsWithoutLinebreak(), ensureTrailingNewline = false)
        if (!spec.isLambda) {
            writer.unindent()
            writer.emit("\n}")
        }
    }

    private fun writeTypeDef(spec: FunctionSpec, codeWriter: CodeWriter) {
        codeWriter.emit("${TYPEDEF.identifier}·")
        codeWriter.emit("${spec.name}·")
        codeWriter.emit("=·")
        codeWriter.emit("${spec.returnType}")
        spec.parameters.emitParameters(
            codeWriter,
            emitBrackets = spec.parameters.isNotEmpty(),
            openBracket = "(",
            closingBracket = ")",
            forceNewLines = false
        ) {
            it.write(codeWriter)
        }
        codeWriter.emit(SEMICOLON)
    }

    private val RETURN_EXPRESSION_BODY_PREFIX_SPACE = CodeBlock.of("return ")
    private val RETURN_EXPRESSION_BODY_PREFIX_NBSP = CodeBlock.of("return·")
    private val THROW_EXPRESSION_BODY_PREFIX_SPACE = CodeBlock.of("throw ")
    private val THROW_EXPRESSION_BODY_PREFIX_NBSP = CodeBlock.of("throw·")

    private fun CodeBlock.returnsWithoutLinebreak(): CodeBlock {
        val returnWithSpace = RETURN_EXPRESSION_BODY_PREFIX_SPACE.formatParts[0]
        val returnWithNbsp = RETURN_EXPRESSION_BODY_PREFIX_NBSP.formatParts[0]
        var originCodeBlockBuilder: CodeBlock.Builder? = null
        for ((i, formatPart) in formatParts.withIndex()) {
            if (formatPart.startsWith(returnWithSpace)) {
                val builder = originCodeBlockBuilder ?: toBuilder()
                originCodeBlockBuilder = builder
                builder.formatParts[i] = formatPart.replaceFirst(returnWithSpace, returnWithNbsp)
            }
        }
        return originCodeBlockBuilder?.build() ?: this
    }

    private fun CodeBlock.asExpressionBody(): CodeBlock? {
        val codeBlock = this.trim()
        val asReturnExpressionBody = codeBlock.withoutPrefix(RETURN_EXPRESSION_BODY_PREFIX_SPACE)
            ?: codeBlock.withoutPrefix(RETURN_EXPRESSION_BODY_PREFIX_NBSP)
        if (asReturnExpressionBody != null) {
            return asReturnExpressionBody
        }
        if (codeBlock.withoutPrefix(THROW_EXPRESSION_BODY_PREFIX_SPACE) != null ||
            codeBlock.withoutPrefix(THROW_EXPRESSION_BODY_PREFIX_NBSP) != null
        ) {
            return codeBlock
        }
        return null
    }
}