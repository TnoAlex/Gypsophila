package priv.alex.ast

import priv.alex.logger.Logger

@Logger
class ASTBuilder(root: String) {

    private val root: ASTNode

    init {
        this.root = ASTNode(root)
    }

    fun build() {
        TODO()
    }
}