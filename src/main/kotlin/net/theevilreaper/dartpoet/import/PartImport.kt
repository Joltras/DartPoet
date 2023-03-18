package net.theevilreaper.dartpoet.import

class PartImport internal constructor(
    private val path: String
) : Import {

    private val partImport: String = buildString {
        append("part ")
        append("'")
        append(path)
        append("';")
    }

    override fun toString(): String = partImport

    override fun compareTo(other: Import): Int = partImport.compareTo(other.toString())
}