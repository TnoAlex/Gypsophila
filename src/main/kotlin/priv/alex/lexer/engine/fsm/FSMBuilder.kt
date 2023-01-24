package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph
import org.jgrapht.graph.builder.GraphTypeBuilder

internal abstract class FSMBuilder {

    protected val graph =
        GraphTypeBuilder.directed<Int, RegexEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private var id = 0
    protected var currentNode = 0
        private set

    protected fun clear() {
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

    protected fun reverse(endPoint: Set<Int>) {
        val edge = graph.edgeSet().toMutableList()
        graph.removeAllEdges(edge)
        edge.forEach {
            graph.addEdge(it.target, it.source, RegexEdge(it))
        }
        addNode()
        endPoint.forEach {
            graph.addEdge(currentNode, it, RegexEdge(true))
        }
    }

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

    protected fun clone(): Graph<Int, RegexEdge> {
        val newGraph = GraphTypeBuilder.directed<Int, RegexEdge>().allowingMultipleEdges(true).allowingSelfLoops(true)
            .weighted(false)
            .buildGraph()!!

        graph.vertexSet().forEach {
            newGraph.addVertex(it)
        }
        graph.edgeSet().forEach {
            newGraph.addEdge(it.source, it.target, RegexEdge(it))
        }
        return newGraph
    }

}
