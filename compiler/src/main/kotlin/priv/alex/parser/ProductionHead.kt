package priv.alex.parser

data class ProductionHead(val content: NonTerminator) : Cloneable {
    override fun toString(): String {
        return content.toString()
    }

    public override fun clone(): ProductionHead {
        return ProductionHead(content)
    }
}
