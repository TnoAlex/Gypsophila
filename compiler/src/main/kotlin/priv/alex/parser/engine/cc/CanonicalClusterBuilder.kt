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

    private val firstMap = HashMap<Symbol, HashSet<Terminator>>()
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
        val initCc = closure(CanonicalClusterItem(acceptProduction, hashSetOf(EOF())))
        initCc.add(CanonicalClusterItem(acceptProduction, hashSetOf(EOF())))
        addNode(initCc)
    }

    fun build(): Graph<CanonicalCluster, CanonicalClusterEdge> {
        log.info("Try to build canonical cluster")
        val ccQueue = ArrayDeque<CanonicalCluster>()
        ccQueue.add(ccGraph.vertexSet().first())
        val vertexSet = HashSet<CanonicalCluster>(64)
        vertexSet.add(ccGraph.vertexSet().first())
        while (ccQueue.isNotEmpty()) {
            val newCcs = advance(ccQueue.removeFirst())
            newCcs.forEach {
                if (!ccQueue.contains(it) && !vertexSet.contains(it)) {
                    ccQueue.add(it)
                    vertexSet.add(it)
                }
            }
        }
        log.info("Done")
        return ccGraph
    }

    private fun closure(initCc: CanonicalClusterItem): HashSet<CanonicalClusterItem> {
        val newProductions = HashSet<Production>(16)
        val initProduction = initCc.production
        val res = HashMap<Production, HashSet<Symbol>>(16)
        if (initProduction.body.current() is NonTerminator) {
            newProductions.addAll(productions.filter { p -> p.head.content == initProduction.body.current() })
            lookBackSymbols(initCc, newProductions).forEach { (k, v) ->
                res[k]?.let { res[k]?.addAll(v) } ?: res.put(k, v)
            }
            val allP = HashSet<Production>(32)
            while (newProductions.isNotEmpty()) {
                val next = HashSet<Production>(32)
                newProductions.forEach { p ->
                    val t = HashSet<Production>(8)
                    t.addAll(productions.filter { fp -> fp.head.content == p.body.current() })
                    if (t.isNotEmpty()) {
                        val tt = res.keys.first { it == p }
                        lookBackSymbols(CanonicalClusterItem(tt, res[tt]!!), t).forEach { (k, v) ->
                            res[k]?.let { res[k]?.addAll(v) } ?: res.put(k, v)
                        }
                        next.addAll(t.filter { s -> !allP.contains(s) })
                        allP.addAll(next)
                    }
                }
                newProductions.clear()
                newProductions.addAll(next)
            }
        }
        res.forEach { (k, _) ->
            val sameLeft = res.filter { it.key.head == k.head }
            if (sameLeft.size <= 1)
                return@forEach
            else {
                sameLeft.forEach {
                    lookBackSymbols(
                        CanonicalClusterItem(it.key, it.value),
                        HashSet(sameLeft.filter { s -> s.key != it.key }.keys)
                    ).forEach { (k, v) ->
                        res[k]?.let { res[k]?.addAll(v) } ?: res.put(k, v)
                    }
                }
            }
        }
        return res.map { CanonicalClusterItem(it.key, it.value) }.toHashSet()
    }

    private fun lookBackSymbols(
        initCc: CanonicalClusterItem,
        closure: HashSet<Production>
    ): HashMap<Production, HashSet<Symbol>> {
        val newCc = HashMap<Production, HashSet<Symbol>>(closure.size + 1)
        if (initCc.production.body.currentNext() == null) {
            closure.forEach { newCc[it] = initCc.sc }
        }
        if (initCc.production.body.currentNext() is Terminator) {
            closure.forEach { newCc[it] = hashSetOf(initCc.production.body.currentNext()!!) }
        }
        if (initCc.production.body.currentNext() is NonTerminator) {
            val content = initCc.production.body.content
            val pos = initCc.production.body.projectPos
            val sc = HashSet<Symbol>()
            var flag = false
            for (i in pos until content.size) {
                if (content[i] is Terminator) {
                    sc.add(content[i])
                    flag = true
                    break
                } else {
                    if (!firstMap[content[i]]!!.contains(EmptySymbol())) {
                        sc.addAll(firstMap[content[i]]!!)
                        flag = true
                        break
                    } else {
                        sc.addAll(firstMap[content[i]]!!.filter { it != EmptySymbol() })
                    }
                }
            }
            if (!flag) {
                sc.addAll(initCc.sc)
            }
            closure.forEach { newCc[it] = sc }
        }
        return newCc
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
            v.forEach {
                val ccItem = closure(it)
                if (ccItem.isNotEmpty())
                    newCc.addAll(ccItem)
                newCc.add(it)
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
        val res = HashMap<NonTerminator, HashSet<Terminator>>(32)
        val empty = HashSet<Symbol>(8)
        productionList.forEach {
            if (it.body.current() == NonTerminator("<EMPTY>") || it.body.content.first() == EmptySymbol())
                empty.add(it.head.content)
        }
        res[NonTerminator("<EMPTY>")] = hashSetOf(EmptySymbol())
        val list = productionList.filter { it.head.content != NonTerminator("<EMPTY>") }
        var change = true
        while (change) {
            change = false
            list.forEach {
                if (it.body.current() is Terminator) {
                    if (res[it.head.content] == null) {
                        res[it.head.content] = hashSetOf(it.body.current() as Terminator)
                        change = true
                    } else {
                        if (!res[it.head.content]!!.contains((it.body.current()))) {
                            res[it.head.content]?.add(it.body.current() as Terminator)
                            change = true
                        }
                    }

                } else {
                    if (empty.contains(it.body.current())) {
                        val tSet = HashSet<Symbol>(8)
                        for (i in it.body.content) {
                            if (!empty.contains(i)) {
                                tSet.add(i)
                                break
                            } else
                                tSet.add(i)
                        }
                        if (tSet.size == it.body.content.size) {
                            if (it.body.content.all { s -> res[s] != null }) {
                                it.body.content.forEach { s ->
                                    if (res[it.head.content] == null) {
                                        res[it.head.content] = HashSet(res[s]!!)
                                        change = true
                                    } else {
                                        if (!res[it.head.content]!!.containsAll(res[s]!!)) {
                                            res[it.head.content]?.addAll(res[s]!!)
                                            change = true
                                        }
                                    }

                                }
                            }
                        } else {
                            if (tSet.all { s -> res[s] != null }) {
                                tSet.forEach { s ->
                                    if (res[it.head.content] == null) {
                                        res[it.head.content] = res[s]!!.filter { f -> f != EmptySymbol() }.toHashSet()
                                        change = true
                                    } else {
                                        val t = res[s]!!.filter { f -> f != EmptySymbol() }.toHashSet()
                                        if (!res[it.head.content]!!.containsAll(t)) {
                                            res[it.head.content]?.addAll(t)
                                            change = true
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (res[it.body.current()] != null) {
                            if (res[it.head.content] == null) {
                                res[it.head.content] = HashSet(res[it.body.current()]!!)
                                change = true
                            } else {
                                if (!res[it.head.content]!!.containsAll(res[it.body.current()]!!)) {
                                    res[it.head.content]?.addAll(res[it.body.current()]!!)
                                    change = true
                                }
                            }
                        }
                    }
                }
            }
        }
        res.forEach { (k, v) ->
            firstMap[k] = v
        }
    }
}