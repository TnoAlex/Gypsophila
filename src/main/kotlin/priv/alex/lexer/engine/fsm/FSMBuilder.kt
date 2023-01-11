package priv.alex.lexer.engine.fsm

import org.jgrapht.Graph
import org.jgrapht.graph.builder.GraphTypeBuilder

abstract class FSMBuilder {

    protected val graph =
        GraphTypeBuilder.directed<Int, RegexEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private var id = 0
    protected var currentNode = 0

    protected fun addNode() {
        graph.addVertex(id)
        currentNode = id
        id += 1
    }

    abstract fun build(): Graph<Int, RegexEdge>
}