package priv.alex.gui

import org.jgrapht.graph.DefaultEdge

class AdapterEdge(private val text: String) : DefaultEdge() {
    override fun toString(): String {
        return text
    }
}