package net.theevilreaper.dartpoet.import

import net.theevilreaper.dartpoet.util.SEMICOLON

class LibraryImport(
    private val path: String
): Import {
    init {
        check(path.trim().isNotEmpty()) { "The path of an LibraryImport can't be empty" }
    }

    private val partImport: String = buildString {
        append("part·of·")
        append(path)
        append(SEMICOLON)
    }

    override fun toString(): String = partImport

    override fun compareTo(other: Import): Int = partImport.compareTo(other.toString())
}