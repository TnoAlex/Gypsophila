package priv.alex.gui

import org.jgrapht.graph.DefaultEdge

class AdapterEdge(val text: String) : DefaultEdge(){
    override fun toString(): String {
        return text
    }
}