package priv.alex.ast

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class AST(val buildBy: String) {
    val graph: Graph<ASTNode, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)

    fun addChild(baseNode: ASTNode?, child: ASTNode) {
        graph.addVertex(child)
        baseNode?.let { graph.addEdge(baseNode, child) }
    }

    fun link(baseNode: ASTNode, child: ASTNode) {
        graph.addEdge(baseNode, child)
    }

    fun child(node: ASTNode): List<ASTNode> {
        return graph.outgoingEdgesOf(node).map { graph.getEdgeTarget(it) }
    }

    fun parent(node: ASTNode): ASTNode? {
        return graph.incomingEdgesOf(node).map { graph.getEdgeSource(it) }.firstOrNull()
    }

    fun hasChild(node: ASTNode): Boolean {
        if (graph.outgoingEdgesOf(node).isEmpty())
            return false
        return true
    }

    fun brother(node: ASTNode, predicate: (ASTNode) -> Boolean): List<ASTNode> {
        val parent = this.parent(node) ?: return emptyList()
        val child = child(parent).filter { it != node }
        return child.filter(predicate)
    }
}
