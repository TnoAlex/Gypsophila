package priv.alex.gui

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.util.mxConstants
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.ast.ASTNode
import priv.alex.core.OpenEdge
import java.awt.Dimension
import javax.swing.JFrame

class ASTAdapter(graph: Graph<ASTNode, OpenEdge>) : JFrame() {
    init {
        val listenableGraph = DefaultListenableGraph(
            GraphTypeBuilder.directed<ASTNode, AdapterEdge>().allowingMultipleEdges(true)
                .allowingSelfLoops(true)
                .weighted(false)
                .buildGraph()!!
        )
        val adapter = JGraphXAdapter(listenableGraph)
        val style = adapter.stylesheet.defaultEdgeStyle
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ELBOW
        style[mxConstants.STYLE_EDGE] = mxConstants.STYLE_LOOP
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_SIDETOSIDE
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_TOPTOBOTTOM
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ORTHOGONAL
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_SEGMENT
        style[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ENTITY_RELATION

        preferredSize = Dimension(1200, 920)
        val component = mxGraphComponent(adapter)
        component.isConnectable = false
        component.graph.isAllowDanglingEdges = false
        contentPane.add(component)

        for (v in graph.vertexSet()) {
            listenableGraph.addVertex(v)
        }
        for (e in graph.edgeSet()) {
            listenableGraph.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e), AdapterEdge(""))
        }

        val layout = mxHierarchicalLayout(adapter)
        layout.execute(adapter.defaultParent)
    }
}