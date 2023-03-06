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
    val acceptProduction: Production


    private fun addNode(set: Set<CanonicalClusterItem>): CanonicalCluster {
        val cluster = CanonicalCluster(set, currentCc)
        return if (!ccGraph.vertexSet().contains(cluster)) {
            ccGraph.addVertex(cluster)
            currentCc++
            cluster
        } else {
            ccGraph.vertexSet().first { it.item == set }
        }
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
        acceptProduction = t.first().clone()
        t.addAll(productionList)
        t.forEach { productions.add(it.clone()) }
        val startProduction = HashSet<Production>(8)
        startProduction.add(acceptProduction)
        startProduction.addAll(closure(listOf(acceptProduction)))
        val current = follow(startProduction)
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

    private fun closure(list: List<Production>): ArrayList<Production> {
        val newProductions = HashSet<Production>(16)
        list.forEach {
            if (it.body.current() is NonTerminator) {
                newProductions.addAll(productions.filter { p -> p.head.content == it.body.current() && p != it })
                var size = 0
                while (size != newProductions.size) {
                    val t = ArrayList<Production>(32)
                    size = newProductions.size
                    newProductions.forEach { p ->
                        t.addAll(productions.filter { fp -> fp.head.content == p.body.current() && p != fp })
                    }
                    newProductions.addAll(t)
                }
            }
        }
        if (newProductions.isEmpty())
            return ArrayList()
        return ArrayList(newProductions)
    }

    private fun advance(baseNode: CanonicalCluster): HashSet<CanonicalCluster> {
        val preAdvance = HashMap<Symbol, ArrayList<CanonicalClusterItem>>()
        val res = HashSet<CanonicalCluster>(32)
        val cc = baseNode.clone()
        cc.forEach {
            if (it.production.body.endProject)
                return@forEach
            val symbol = it.production.body.advance()
            preAdvance[symbol]?.add(it) ?: preAdvance.put(symbol, arrayListOf(it))
        }
        preAdvance.forEach { (k, v) ->
            val newCc = HashSet<CanonicalClusterItem>(32)
            val newProductions = HashSet<Production>(32)
            newProductions.addAll(closure(v.map { it.production }))
            newCc.addAll(v)
            if (newProductions.isNotEmpty()) {
                newCc.addAll(follow(newProductions, newCc.toList()))
            }
            val c = addNode(newCc)
            res.add(c)
            val edge = ccGraph.getEdge(baseNode, c)
            if (edge == null || edge.symbol != k)
                ccGraph.addEdge(baseNode, c, CanonicalClusterEdge(k))
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
    private fun follow(
        list: HashSet<Production>,
        initCC: List<CanonicalClusterItem>? = null
    ): Set<CanonicalClusterItem> {
        val res = HashMap<Production, HashSet<Symbol>>(list.size)
        val lists = HashSet<Production>(list)
        val incomplete = HashSet<Production>(8)
        initCC?.let {
            it.forEach { c ->
                res[c.production] = c.sc
                lists.add(c.production)
            }
        }

        lists.forEach {
            if ((initCC != null) && initCC.map { i -> i.production }.contains(it)) {
                return@forEach
            }
            val left = it.head
            val right = lists.filter { p -> p.body.current() == left.content }
            if (right.isEmpty()) {
                res[it] = hashSetOf(EOF())
            } else {
                right.forEach { r ->
                    if (r.body.currentNext() == null) {
                        if (res[r] == null)
                            incomplete.add(it)
                        else {
                            res[it] ?: res.put(it, res[r]!!)
                            res[it]?.addAll(res[r]!!)
                        }

                    } else if (r.body.currentNext() is NonTerminator) {
                        val first = HashSet<Symbol>(16)
                        productions.filter { pro -> pro.head.content == r.body.currentNext() }.forEach { p ->
                            first.addAll(firstMap[p]!!)
                        }
                        res[it] ?: res.put(it, first)
                        res[it]?.addAll(first)
                    } else {
                        res[it] ?: res.put(it, hashSetOf(r.body.currentNext()!!))
                        res[it]?.add(r.body.currentNext()!!)
                    }
                }
            }
        }
        if (incomplete.isNotEmpty()) {
            val cc = follow(incomplete, res.map { CanonicalClusterItem(it.key, it.value) })
            cc.forEach {
                res[it.production] = it.sc
            }
        }
        return res.map { CanonicalClusterItem(it.key, it.value) }.toSet()
    }


}