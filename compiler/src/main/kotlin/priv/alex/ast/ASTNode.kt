package priv.alex.ast

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class ASTNode(var value: String) {
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
}
