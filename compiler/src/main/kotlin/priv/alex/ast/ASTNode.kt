package priv.alex.ast

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenType
import priv.alex.parser.Symbol

class ASTNode(val value: Pair<Symbol, Token?>) {
    private val graph: Graph<ASTNode, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)

    fun addChild(child: ASTNode) {
        graph.addVertex(child)
        graph.addEdge(this, child)
    }

    fun child(): List<ASTNode> {
        return graph.outgoingEdgesOf(this).map { graph.getEdgeTarget(it) }
    }

    fun parent(): ASTNode? {
        return graph.incomingEdgesOf(this).map { graph.getEdgeSource(it) }.firstOrNull()
    }

    fun hasChild(): Boolean {
        if (graph.outgoingEdgesOf(this).isEmpty())
            return false
        return true
    }

    fun brother(predicate: (ASTNode) -> Boolean): List<ASTNode> {
        val parent = this.parent() ?: return emptyList()
        val child = parent.child().filter { it != this }
        return child.filter(predicate)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as ASTNode
        if (other.value != value) return false
        return true
    }

    override fun toString(): String {
        val builder = StringBuilder()
        value.second?.let {
            if (it.type == TokenType.LITERAL) {
                builder.append(it.rawText)
            } else {
                builder.append(TokenType.stringOf(it.type))
            }
        } ?: let { builder.append(value.first.content) }
        return builder.toString()
    }

    override fun hashCode(): Int {
        return graph.toString().hashCode()
    }

}
