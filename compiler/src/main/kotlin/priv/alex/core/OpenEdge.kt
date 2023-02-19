package priv.alex.core

import org.jgrapht.graph.DefaultEdge

abstract class OpenEdge : DefaultEdge() {
    public override fun getSource(): Any {
        return super.getSource()
    }

    public override fun getTarget(): Any {
        return super.getTarget()
    }
}