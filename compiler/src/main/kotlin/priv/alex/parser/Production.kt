package priv.alex.parser

data class Production(val head: ProductionHead, val body: ProductionBody) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as Production
        if (other.head != head || other.body != body) return false
        return true
    }

    override fun hashCode(): Int {
        var result = head.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

    override fun toString(): String {
        return "$head->$body"
    }
}