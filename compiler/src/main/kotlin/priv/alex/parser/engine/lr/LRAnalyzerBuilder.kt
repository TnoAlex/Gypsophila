package priv.alex.parser.engine.lr


import org.jgrapht.Graph
import priv.alex.logger.Logger
import priv.alex.parser.EOF
import priv.alex.parser.NonTerminator
import priv.alex.parser.Production
import priv.alex.parser.ProjectState
import priv.alex.parser.engine.cc.CanonicalCluster
import priv.alex.parser.engine.cc.CanonicalClusterEdge

/**
 * Lr analyzer builder
 *
 * @property acceptProduction The production that can be accpeted
 * @constructor
 *
 * @param productions
 */
@Logger
class LRAnalyzerBuilder(productions: Set<Production>, private val acceptProduction: Production) {

    private val analyseTable = LRTable()
    val productionMap = HashMap<Int, Production>()
    private val invertedMap = HashMap<Production, Int>()

    init {
        productions.mapIndexed { index, value ->
            productionMap[index] = value
            invertedMap.put(value, index)
        }
    }

    /**
     * Build
     *
     * @param cc CanonicalCluster graph
     * @return LR analyse table
     */
    fun build(cc: Graph<CanonicalCluster, CanonicalClusterEdge>): LRTable {
        log.info("Build Lr analyze table")
        cc.vertexSet().forEach {
            val outEdge = cc.outgoingEdgesOf(it)
            outEdge.forEach { e ->
                if (e.symbol is NonTerminator) {
                    analyseTable.addGoto(Pair(it.ccId, e.symbol), e.target.ccId)
                } else {
                    analyseTable.addAction(Pair(it.ccId, e.symbol), LRAction(Action.SHIFT, e.target.ccId))
                }
            }
            it.item.forEach { c ->
                if (c.production.body.projectState == ProjectState.REDUCE) {
                    if (c.production.body.content == acceptProduction.body.content && c.production.head == acceptProduction.head) {
                        analyseTable.addAction(
                            Pair(it.ccId, EOF()),
                            LRAction(Action.ACCEPT, invertedMap[c.production.initProduction()]!!)
                        )
                    }
                    c.sc.forEach { s ->
                        analyseTable.addAction(
                            Pair(it.ccId, s),
                            LRAction(Action.REDUCE, invertedMap[c.production.initProduction()]!!)
                        )
                    }
                }
            }
        }
        log.info("Done")
        return analyseTable
    }
}