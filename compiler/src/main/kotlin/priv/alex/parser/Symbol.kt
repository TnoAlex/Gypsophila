package priv.alex.parser

abstract class Symbol(val content: String) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as Symbol
        if (content != other.content) return false
        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String {
        return content
    }
}
