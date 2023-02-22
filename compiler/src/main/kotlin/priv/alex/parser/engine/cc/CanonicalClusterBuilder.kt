package priv.alex.parser.engine.cc

import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.logger.Logger
import priv.alex.parser.Production

@Logger
class CanonicalClusterBuilder(entryPoint: Production, production: List<Production>) {

    private val cc = GraphTypeBuilder.directed<CanonicalCluster, CanonicalClusterEdge>().allowingMultipleEdges(true)
        .allowingSelfLoops(true).weighted(false)
        .buildGraph()!!

    private var nodeId = 0
    private var currentPos = 0

    private fun addNode(cluster: List<CanonicalClusterItem>) {
        cc.addVertex(CanonicalCluster(cluster, nodeId))
        currentPos = nodeId
        nodeId += 1
    }

    init {

    }

    fun build() {

    }

    private fun follow(list: List<Production>) {

    }

}