package priv.alex.lexer.engine.automaton

import org.jgrapht.Graph


internal class DFABuilder(private val nfa: NFA) : FABuilder() {

    private val nodeMap = HashMap<HashSet<Int>, Int>()
    val endPoint = HashSet<Int>()

    init {
        addNode()
    }

    override fun build(): Graph<Int, RegexEdge> {
        deterministic()
        nodeMap.filter { it.key.containsAny(setOf(nfa.endPoint)) }.forEach { (_, v) -> endPoint.add(v) }
        return graph
    }

    private fun <E> Set<E>.containsAny(collection: Collection<E>): Boolean {
        collection.forEach {
            if (contains(it))
                return true
        }
        return false
    }

    private fun deterministic() {
        val stateSet = HashMap<Set<Int>, Boolean>()
        val rawStatus: MutableList<HashSet<Int>> = mutableListOf(nfa.epsilonClosure(setOf(nfa.startPoint)))

        var moveStatus = HashMap<Alphabet, MutableSet<Int>>()
        stateSet[rawStatus.first()] = false
        nodeMap[rawStatus.first()] = currentNode
        var baseNode = 0

        while (stateSet.any { !it.value } || moveStatus.isNotEmpty()) {
            if (moveStatus.isEmpty()) {
                moveStatus = nfa.moveTo(rawStatus.first())
                baseNode = nodeMap[rawStatus.first()]!!
                stateSet[rawStatus.first()] = true
                rawStatus.removeAt(0)
            }

            var usingKey: Alphabet? = null
            run block@{
                moveStatus.forEach { (k, v) ->
                    usingKey = k
                    val closure = nfa.epsilonClosure(v)
                    if (stateSet.all { it.key != closure }) {
                        stateSet[closure] = false
                        addNode()
                        graph.addEdge(baseNode, currentNode, Alphabet.toEdge(k))
                        rawStatus.add(closure)
                        nodeMap[rawStatus.last()] = currentNode
                    } else {
                        val sourceNode = nodeMap[closure]
                        graph.addEdge(baseNode, sourceNode, Alphabet.toEdge(k))
                    }
                    return@block
                }
            }
            moveStatus.remove(usingKey)
        }

        graph.vertexSet().forEach {v->
            val edge = graph.outgoingEdgesOf(v)
            val margeEdge = HashSet<RegexEdge>()
            edge.forEach edgeLoop@{e->
                if (margeEdge.contains(e))
                    return@edgeLoop
                edge.filter { e.sameCondition(it) && e.target !=it.target }.forEach { s->
                    val sTarget = graph.outgoingEdgesOf(s.target)
                    sTarget.forEach {
                        graph.addEdge(e.target,it.target,RegexEdge(it))
                        margeEdge.add(it)
                    }
                    margeEdge.add(s)
                }
            }
           margeEdge.forEach { graph.removeEdge(it)}
        }
        graph.vertexSet().filter { graph.degreeOf(it) == 0 }.forEach {
            nodeMap.filter { m->m.key.contains(it) }.forEach { v-> v.key.remove(it) }
            graph.removeVertex(it)
        }
    }

}