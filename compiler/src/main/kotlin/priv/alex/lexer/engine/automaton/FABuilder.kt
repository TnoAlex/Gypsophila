package priv.alex.lexer.engine.automaton

import org.jgrapht.Graph
import org.jgrapht.graph.builder.GraphTypeBuilder

/**
 * Fa builder
 * The super class of DFABuilder and NFABuilder
 * @constructor Create Fa builder
 */
internal abstract class FABuilder {

    protected val graph =
        GraphTypeBuilder.directed<Int, RegexEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private var id = 0
    protected var currentNode = 0
        private set

    protected fun clearGraph() {
        graph.removeAllVertices(graph.vertexSet().toMutableList())
        id = 0
        currentNode = 0
    }

    protected fun addNode() {
        graph.addVertex(id)
        currentNode = id
        id += 1
    }

    abstract fun build(): Graph<Int, RegexEdge>

    protected fun reachable() {
        val unreachable = HashSet<Int>()
        for (v in graph.vertexSet()) {
            if (graph.inDegreeOf(v) == 0 && v != 0) {
                unreachable.add(v)
                val edge = graph.edgesOf(v).toMutableList()
                while (edge.isNotEmpty()) {
                    val e = edge.first()
                    if (graph.inDegreeOf(e.target) == 1) {
                        unreachable.add(e.target)
                        edge.addAll(graph.outgoingEdgesOf(e.target))
                    }
                    edge.removeAt(0)
                }
            }
        }
        unreachable.forEach {
            graph.removeVertex(it)
        }
    }
}
