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

    private fun closure(list: List<Production>): ArrayList<Production> {
        val newProductions = HashSet<Production>(16)
        list.forEach {
            if (it.body.current() is NonTerminator) {
                newProductions.addAll(productions.filter { p -> p.head.content == it.body.current() })
                var size = 0
                while (size != newProductions.size) {
                    val t = ArrayList<Production>(32)
                    size = newProductions.size
                    newProductions.forEach { p ->
                        t.addAll(productions.filter { fp -> fp.head.content == p.body.current() })
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
            if (newCc == c.item)
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
            productionList.filter { it.head.content == k }.forEach {
                firstMap[it] = v
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
                            if (firstMap[p]!!.contains(EmptySymbol())) {
                                if (res[p] == null)
                                    incomplete.add(it)
                                else {
                                    first.addAll(firstMap[p]!!.filter { f -> f != EmptySymbol() })
                                    first.addAll(res[r]!!)
                                }
                            } else {
                                first.addAll(firstMap[p]!!)
                            }
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