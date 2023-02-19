package priv.alex.gui

import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.util.mxConstants
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.lexer.engine.automaton.RegexEdge
import java.awt.Dimension
import javax.swing.JFrame



class RegexGraphAdapter(graph: Graph<Int, RegexEdge>) :JFrame() {

    init {
        val listenableGraph = DefaultListenableGraph(
            GraphTypeBuilder.directed<Int, AdapterEdge>().allowingMultipleEdges(true).allowingSelfLoops(true)
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

        preferredSize = Dimension(960,620)
        val component =  mxGraphComponent(adapter)
        component.isConnectable = false
        component.graph.isAllowDanglingEdges = false
        contentPane.add(component)

        for (v in graph.vertexSet()){
            listenableGraph.addVertex(v)
        }
        for (e in graph.edgeSet()){
            if(e.epsilon){
                listenableGraph.addEdge(e.source,e.target,AdapterEdge("Ep"))
            }
            else if(e.cSet == null && e.cChar == null){
                listenableGraph.addEdge(e.source,e.target,AdapterEdge("Any"))
            }
            else if(e.cSet !=null && e.cChar == null){
                if (e.invert){
                    listenableGraph.addEdge(e.source,e.target,AdapterEdge("^["+e.cSet!!.last()+","+e.cSet!!.first()+"]"))
                }else{
                    listenableGraph.addEdge(e.source,e.target,AdapterEdge("["+e.cSet!!.last()+","+e.cSet!!.first()+"]"))
                }

            }
            else {
                listenableGraph.addEdge(e.source,e.target,AdapterEdge(e.cChar!!.toString()))
            }

        }

        val layout = mxFastOrganicLayout(adapter)
        layout.execute(adapter.defaultParent)
    }
}