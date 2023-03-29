package priv.alex.parser

/**
 * Production
 *
 * @property head Production head
 * @property body Production body
 * @constructor Create empty Production
 */
data class Production(val head: ProductionHead, val body: ProductionBody) : Cloneable {

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

    public override fun clone(): Production {
        return Production(head.clone(), body.clone())
    }

    override fun toString(): String {
        return "$head->$body"
    }

    fun initProduction(): Production {
        return Production(head, body.initState())
    }

}