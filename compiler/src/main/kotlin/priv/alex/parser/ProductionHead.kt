package priv.alex.parser

/**
 * Production head
 *
 * @property content Production head content
 * @constructor Create Production head
 */
data class ProductionHead(val content: NonTerminator) : Cloneable {
    override fun toString(): String {
        return content.toString()
    }

    public override fun clone(): ProductionHead {
        return ProductionHead(content)
    }
}
