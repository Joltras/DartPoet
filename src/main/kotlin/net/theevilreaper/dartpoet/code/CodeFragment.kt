package net.theevilreaper.dartpoet.code

class CodeFragment(
    private var formatParts: List<String>,
    private var args: List<Any?>
) {

    fun isEmpty() = formatParts.isEmpty()

    fun toBuilder(): CodeFragmentBuilder {
        val builder = CodeFragmentBuilder()
        builder.formatParts += formatParts
        builder.args.addAll(args)
        return builder
    }
}