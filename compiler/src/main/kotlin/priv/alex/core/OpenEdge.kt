package priv.alex.core

import org.jgrapht.graph.DefaultEdge

/**
 * Open edge
 *
 * @constructor Create Open edge
 */
open class OpenEdge : DefaultEdge() {
    public override fun getSource(): Any {
        return super.getSource()
    }

    public override fun getTarget(): Any {
        return super.getTarget()
    }
}