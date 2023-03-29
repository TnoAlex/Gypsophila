package priv.alex.parser.engine.cc

import priv.alex.core.OpenEdge
import priv.alex.parser.Symbol

/**
 * Canonical cluster edge
 *
 * @property symbol
 * @constructor Create Canonical cluster edge
 */
class CanonicalClusterEdge(val symbol: Symbol) : OpenEdge(){
    override fun getSource() = super.getSource() as CanonicalCluster
    override fun getTarget() = super.getTarget() as CanonicalCluster

    override fun toString(): String {
        return symbol.toString()
    }
}