package priv.alex.ast

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import priv.alex.core.OpenEdge

/**
 * Ast
 *
 * @property buildBy Token File name
 * @constructor Create Ast
 */
class AST(val buildBy: String) {
    val graph: Graph<ASTNode, OpenEdge> = DefaultDirectedGraph(OpenEdge::class.java)

    /**
     * Add child
     *
     * @param baseNode
     * @param child
     */
    fun addChild(baseNode: ASTNode?, child: ASTNode) {
        graph.addVertex(child)
        baseNode?.let { graph.addEdge(baseNode, child) }
    }


    /**
     * Child
     *
     * @param node
     * @return The child of the node
     */
    fun child(node: ASTNode): List<ASTNode> {
        return graph.outgoingEdgesOf(node).map { graph.getEdgeTarget(it) }
    }

    /**
     * Parent
     *
     * @param node
     * @return the parent of the node
     */
    fun parent(node: ASTNode): ASTNode? {
        return graph.incomingEdgesOf(node).map { graph.getEdgeSource(it) }.firstOrNull()
    }

    /**
     * Has child
     *
     * @param node
     * @return if the node has child
     */
    fun hasChild(node: ASTNode): Boolean {
        return graph.outgoingEdgesOf(node).isNotEmpty()
    }

    /**
     * Brother
     *
     * @param node
     * @param predicate
     * @receiver
     * @return  eligible brothers of node
     */
    fun brother(node: ASTNode, predicate: (ASTNode) -> Boolean): List<ASTNode> {
        val parent = this.parent(node) ?: return emptyList()
        val child = child(parent).filter { it != node }
        return child.filter(predicate)
    }
}
