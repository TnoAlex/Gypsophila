package priv.alex.parser

data class ProductionHead(val content: NonTerminator) {
    override fun toString(): String {
        return content.toString()
    }
}
