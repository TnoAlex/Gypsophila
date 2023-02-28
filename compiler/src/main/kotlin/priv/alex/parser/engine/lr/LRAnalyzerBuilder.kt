package priv.alex.parser.engine.lr


import org.jgrapht.Graph
import priv.alex.parser.NonTerminator
import priv.alex.parser.Production
import priv.alex.parser.ProjectState
import priv.alex.parser.engine.cc.CanonicalCluster
import priv.alex.parser.engine.cc.CanonicalClusterEdge

class LRAnalyzerBuilder(productions: Set<Production>) {

    private val analyseTable = LRTable()
    private val productionMap = HashMap<Int, Production>()
    private val invertedMap = HashMap<Production, Int>()
    private val acceptProduction: Production

    init {
        productions.mapIndexed { index, value ->
            productionMap[index] = value
            invertedMap.put(value, index)
        }
        acceptProduction = productions.first().clone()
    }

    fun build(cc: Graph<CanonicalCluster, CanonicalClusterEdge>): LRTable {
        cc.vertexSet().forEach {
            val outEdge = cc.outgoingEdgesOf(it)
            outEdge.forEach { e ->
                if (e.symbol is NonTerminator) {
                    analyseTable.addGoto(e.symbol, e.target.ccId)
                } else {
                    analyseTable.addAction(Pair(it.ccId, e.symbol), LRAction(Action.SHIFT, e.target.ccId))
                }
            }
            it.item.forEach { c ->
                if (c.production.body.projectState == ProjectState.REDUCE) {
                    if (c.production.hashCode() == productionMap[0]!!.hashCode())
                        c.sc.forEach { s ->
                            analyseTable.addAction(
                                Pair(it.ccId, s),
                                LRAction(Action.REDUCE, invertedMap[c.production]!!)
                            )
                        }
                }
            }
        }
        return analyseTable
    }
}