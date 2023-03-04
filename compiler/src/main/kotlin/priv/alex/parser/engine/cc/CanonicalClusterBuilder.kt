package priv.alex.parser.engine.cc

import org.jgrapht.Graph
import org.jgrapht.graph.builder.GraphTypeBuilder
import priv.alex.logger.Logger
import priv.alex.parser.*

@Logger
class CanonicalClusterBuilder(entryPoint: Production, productionList: List<Production>) {

    private val ccGraph =
        GraphTypeBuilder.directed<CanonicalCluster, CanonicalClusterEdge>().allowingMultipleEdges(true)
            .allowingSelfLoops(true).weighted(false)
            .buildGraph()!!

    private val firstMap = HashMap<Production, HashSet<Terminator>>()
    val productions = ArrayList<Production>()
    private var currentCc = 0
    val broadeningSyntax: Production


    private fun addNode(cluster: CanonicalCluster) {
        ccGraph.addVertex(cluster)
        currentCc++
    }

    init {
        first(productionList)
        val appendProductionHead = NonTerminator(entryPoint.head.content.content.replace(">", "*>"))
        val t = ArrayList<Production>()
        t.add(
            Production(
                ProductionHead(appendProductionHead),
                ProductionBody(listOf(entryPoint.head.content))
            )
        )
        broadeningSyntax = t.first().clone()
        t.addAll(productionList)
        t.forEach { productions.add(it.clone()) }
        val current = CanonicalCluster(follow(t), currentCc)
        addNode(current)
    }

    fun build(): Graph<CanonicalCluster, CanonicalClusterEdge> {
        log.info("Try to build canonical cluster")
        val ccQueue = ArrayDeque<CanonicalCluster>()
        ccQueue.add(ccGraph.vertexSet().first())
        while (ccQueue.isNotEmpty()) {
            val newCcs = advance(ccQueue.removeFirst())
            ccQueue.addAll(newCcs)
        }
        log.info("Done")
        return ccGraph
    }

    private fun advance(baseNode: CanonicalCluster): ArrayList<CanonicalCluster> {
        val preAdvance = HashMap<CanonicalClusterItem, Symbol>()
        val res = ArrayList<CanonicalCluster>(8)
        val cc = baseNode.clone()
        cc.forEach {
            if (it.production.body.endProject)
                return@forEach
            preAdvance[it] = it.production.body.advance()
        }
        preAdvance.forEach { (k, v) ->
            val newCc = HashSet<CanonicalClusterItem>(8)
            newCc.add(k)
            val newProductions = ArrayList<Production>()
            if (k.production.body.current() is NonTerminator) {
                productions.filter { it.head.content == k.production.body.current() }
                newCc.addAll(follow(newProductions, k))
            }
            val t = CanonicalCluster(newCc, currentCc)
            if (!ccGraph.vertexSet().contains(t))
                res.add(t)
            addNode(t)
            ccGraph.addEdge(baseNode, t, CanonicalClusterEdge(v))
        }
        return res
    }

    /**
     * 求取first集
     */
    private fun first(productionList: List<Production>) {
        for (i in productionList.indices) {
            if (productionList[i].body.content.first() is Terminator) {
                firstMap[productionList[i]] ?: firstMap.put(
                    productionList[i],
                    hashSetOf(productionList[i].body.content.first() as Terminator)
                )?.add(productionList[i].body.content.first() as Terminator)
            } else {
                var nextSymbol = productionList[i]
                val symbolQueue = ArrayDeque<Production>(8)
                val allSymbol = ArrayList<Production>(32)
                while (nextSymbol.body.content.first() is NonTerminator) {
                    productionList.filter { it.head.content == nextSymbol.body.content.first() && it != productionList[i] }
                        .forEach {
                            if (it.body.content.first() is Terminator) {
                                firstMap[productionList[i]]?.add(it.body.content.first() as Terminator)
                                    ?: firstMap.put(
                                        productionList[i],
                                        hashSetOf(it.body.content.first() as Terminator)
                                    )
                            } else {
                                if (!allSymbol.contains(it)) {
                                    symbolQueue.add(it)
                                    allSymbol.add(it)
                                }
                            }
                    }
                    nextSymbol = symbolQueue.removeFirstOrNull() ?: break
                }
            }
        }
        firstMap.forEach { (k, v) ->
            val firstSet = HashSet<Terminator>(v)
            val sameLeft = firstMap.filter { it.key.head == k.head }
            sameLeft.values.forEach { firstSet.addAll(it) }
            v.addAll(firstSet)
            sameLeft.values.forEach { it.addAll(firstSet) }
        }
        productionList.filter { !firstMap.keys.contains(it) }.forEach {
            val firstSet = HashSet<Terminator>()
            val sameLeft = firstMap.filter { (k, _) -> k.head == it.head }
            if (sameLeft.isEmpty()) {
                log.error("$it -> The production cannot find the first set")
                throw RuntimeException("Unparseable syntax")
            } else {
                sameLeft.values.forEach { v -> firstSet.addAll(v) }
                firstMap[it] = firstSet
            }
        }
    }

    /**
     * 求取follow集
     */
    private fun follow(list: List<Production>, initCC: CanonicalClusterItem? = null): Set<CanonicalClusterItem> {
        val res = HashMap<Production, HashSet<Symbol>>(list.size)
        val incompleteMap = HashMap<Production, Production>()

        if (initCC != null) {
            res[initCC.production] = initCC.sc
        }

        for (i in list.indices) {
            val left = list[i].head
            val rightList = list.filter { it.body.current() == left.content }
            if (initCC != null && initCC.production.body.current() == left.content) {
                (rightList as ArrayList).add(initCC.production)
            }
            if (rightList.isEmpty())
                res[list[i]] = hashSetOf(EOF())
            else {
                rightList.forEach {
                    if (it.body.currentNext() == null) {
                        if (res[it] != null) {
                            res[list[i]] ?: res.put(list[i], res[it]!!)?.addAll(res[it]!!)
                        } else {
                            incompleteMap[list[i]] = it
                        }
                    } else if (it.body.currentNext() is NonTerminator) {
                        val t = HashSet<Symbol>(8)
                        list.filter { l -> l.head.content == it.body.currentNext() }
                            .forEach { p -> t.addAll(firstMap[p]!!) }
                        res[list[i]] ?: res.put(list[i], t)?.addAll(t)
                    } else {
                        res[list[i]] ?: res.put(list[i], hashSetOf(it.body.currentNext()!!))
                            ?.add(it.body.currentNext()!!)
                    }
                }
            }
        }
        val deepDependenceIncomplete = HashMap<Production, Production>()
        if (incompleteMap.isNotEmpty()) {
            incompleteMap.forEach { (k, v) ->
                if (res[v] != null) {
                    res[k] ?: res.put(k, res[v]!!)?.addAll(res[v]!!)
                } else {
                    deepDependenceIncomplete[k] = v
                }
            }
        }
        deepDependenceIncomplete.forEach { (k, v) ->
            if (res[v] == null) {
                log.error("$k -> This production cannot build a follow collection")
                throw RuntimeException("Null generated references")
            }
            res[k] ?: res.put(k, res[v]!!)?.addAll(res[v]!!)
        }
        return res.map { CanonicalClusterItem(it.key, it.value) }.toSet()
    }

}