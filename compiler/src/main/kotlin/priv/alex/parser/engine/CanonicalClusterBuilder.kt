package priv.alex.parser.engine

import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.logger.Logger

@Logger
class CanonicalClusterBuilder {
    private val cc = GraphTypeBuilder.directed<CanonicalCluster, CanonicalClusterEdge>().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(false)
        .buildGraph()!!
}